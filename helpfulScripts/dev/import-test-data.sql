-- ============================================================================
--
-- Generates test data for the MuLive DB.
--
-- Parameters (pass with -v, both optional):
--   player_size  -> number of players to create (default: 4, minimum: 4)
--   room_size    -> number of rooms to create   (default: 10)
--
-- Usage:
--   docker exec -it mulive-db psql -U postgres -d "${POSTGRES_DB}" \
--     --set ON_ERROR_QUIT=1 \
--     -v player_size=10 -v room_size=40 \
--     -f "${script}"
-- ============================================================================

\set ON_ERROR_STOP on

-- Defaults for player_size / room_size if not supplied via -v
\if :{?player_size}
\else
  \set player_size 4
\endif

\if :{?room_size}
\else
  \set room_size 25
\endif

-- ----------------------------------------------------------------------------
-- UUIDv7 support
-- ----------------------------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE OR REPLACE FUNCTION uuid_generate_v7(p_timestamp timestamptz DEFAULT clock_timestamp())
RETURNS uuid
LANGUAGE plpgsql
VOLATILE
AS $$
DECLARE
    unix_ts_ms BYTEA;
    rand_bytes BYTEA;
    uuid_bytes BYTEA;
BEGIN
    -- 48-bit big-endian millisecond unix timestamp
    unix_ts_ms := substring(int8send(floor(extract(epoch FROM p_timestamp) * 1000)::bigint) FROM 3 FOR 6);
    -- 10 random bytes fill the rest
    rand_bytes := gen_random_bytes(10);
    uuid_bytes := unix_ts_ms || rand_bytes;
    -- version 7 in top nibble of byte 6
    uuid_bytes := set_byte(uuid_bytes, 6, (get_byte(uuid_bytes, 6) & 15) | 112);
    -- RFC 4122 variant in top bits of byte 8
    uuid_bytes := set_byte(uuid_bytes, 8, (get_byte(uuid_bytes, 8) & 63) | 128);
    RETURN encode(uuid_bytes, 'hex')::uuid;
END;
$$;

-- ----------------------------------------------------------------------------
-- Data generation
-- ----------------------------------------------------------------------------
-- psql does not interpolate :variables inside $$ dollar-quoted bodies, so the
-- values are passed in via a session GUC instead, set here outside the DO block.
SET myapp.player_size = :'player_size';
SET myapp.room_size = :'room_size';

DO $$
DECLARE
    v_player_count       INT := current_setting('myapp.player_size')::int;
    v_room_count         INT := current_setting('myapp.room_size')::int;

    v_player_ids         BIGINT[]    := ARRAY[]::BIGINT[];
    v_player_dates       TIMESTAMP[] := ARRAY[]::TIMESTAMP[];

    v_now_utc            TIMESTAMP := (now() AT TIME ZONE 'utc');

    i                    INT;
    j                    INT;
    v_new_id             BIGINT;
    v_creation_date      TIMESTAMP;

    v_available          INT[];
    v_selected_idx       INT[];
    v_pick               INT;

    v_max_player_date    TIMESTAMP;
    v_seconds_range      DOUBLE PRECISION;
    v_room_creation      TIMESTAMP;
    v_room_finish        TIMESTAMP;

    v_room_id            UUID;
    v_owner_id           BIGINT;
    v_total_score_to_win SMALLINT;
    v_turn_duration_in_seconds SMALLINT;
    v_rounds_played      SMALLINT;

    v_placement          SMALLINT;
    v_score              SMALLINT;
BEGIN
    IF v_player_count < 4 THEN
        RAISE EXCEPTION 'player_size must be at least 4 (got %) - every room needs 4 distinct participants', v_player_count;
    END IF;

    -- -------------------------------------------------------------------
    -- 1) Create players
    -- -------------------------------------------------------------------
    FOR i IN 1..v_player_count LOOP
        v_new_id := nextval('player_seq');

        -- UTC timestamp between ~1 year ago and ~6 months ago
        v_creation_date := v_now_utc
            - make_interval(days => 182 + floor(random() * 183)::int)
            - make_interval(secs => floor(random() * 86400)::int);

        INSERT INTO player (id, username, password, email, creation_date, is_verified, profile_icon_id)
        VALUES (
            v_new_id,
            'player_' || i,
            'player_' || i,
            'player_' || i || '@gmail.com',
            v_creation_date,
            TRUE,
            1
        );

        v_player_ids   := array_append(v_player_ids, v_new_id);
        v_player_dates := array_append(v_player_dates, v_creation_date);
    END LOOP;

    RAISE NOTICE 'Created % player(s)', v_player_count;

    FOR j IN 1..v_room_count LOOP

        -- Pick 4 distinct random players out of the whole pool for this room
        v_available    := ARRAY(SELECT generate_series(1, v_player_count));
        v_selected_idx := ARRAY[]::INT[];
        FOR i IN 1..4 LOOP
            v_pick := 1 + floor(random() * array_length(v_available, 1))::int;
            v_selected_idx := array_append(v_selected_idx, v_available[v_pick]);
            v_available    := array_remove(v_available, v_available[v_pick]);
        END LOOP;

        -- Room must start after every participant already existed
        SELECT max(d) INTO v_max_player_date
        FROM unnest(v_selected_idx) AS idx
        CROSS JOIN LATERAL (SELECT v_player_dates[idx] AS d) sub;

        v_seconds_range := GREATEST(
            120,
            EXTRACT(EPOCH FROM (v_now_utc - v_max_player_date))
        );

        -- creation_date: somewhere after the last participant was created,
        -- leaving enough room before "now" for a finished_date to also fit
        v_room_creation := v_max_player_date
            + (60 + floor(random() * (v_seconds_range * 0.9))) * INTERVAL '1 second';

        -- finished_date: after creation_date, 10 min - 4 h later, capped at now()
        v_room_finish := LEAST(
            v_room_creation + (600 + floor(random() * 13800)) * INTERVAL '1 second',
            v_now_utc - INTERVAL '1 second'
        );
        IF v_room_finish <= v_room_creation THEN
            v_room_finish := v_room_creation + INTERVAL '1 second';
        END IF;

        v_owner_id := v_player_ids[v_selected_idx[1 + floor(random() * array_length(v_selected_idx, 1))::int]];
        v_total_score_to_win := (ARRAY[25, 50, 75, 100])[1 + floor(random() * 4)::int];
        v_turn_duration_in_seconds := (ARRAY[30, 45, 60, 75, 90, 105, 120])[1 + floor(random() * 7)::int];
        v_rounds_played := (4 + floor(random() * 20))::smallint;

        v_room_id := uuid_generate_v7(v_room_creation AT TIME ZONE 'utc');

        INSERT INTO room (id, name, is_public, total_score_to_win, turn_duration_in_seconds, rounds_played, creation_date, finished_date, owner_player_id)
        VALUES (
            v_room_id,
            'Test Room ' || j,
            (random() < 0.5),
            v_total_score_to_win,
            v_turn_duration_in_seconds,
            v_rounds_played,
            v_room_creation,
            v_room_finish,
            v_owner_id
        );

        -- room_player rows: v_selected_idx is already in random order, so its
        -- position doubles as a random placement 1..4
        FOR v_placement IN 1..4 LOOP
            v_score := GREATEST(
                0,
                v_total_score_to_win - (v_placement - 1) * (5 + floor(random() * 15))::int
            )::smallint;

            INSERT INTO room_player (room_id, player_id, placement_index, total_score)
            VALUES (
                v_room_id,
                v_player_ids[v_selected_idx[v_placement]],
                v_placement,
                v_score
            );
        END LOOP;

    END LOOP;

    RAISE NOTICE 'Created % room(s)', v_room_count;
END;
$$;