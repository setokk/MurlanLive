--
-- player Table
--
CREATE TABLE IF NOT EXISTS player(
    id BIGINT NOT NULL PRIMARY KEY,
    username TEXT NOT NULL,
    password TEXT NOT NULL,
    creation_date TIMESTAMP NOT NULL
);
CREATE SEQUENCE IF NOT EXISTS player_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

--
-- card_skin Table
--
CREATE TABLE IF NOT EXISTS card_skin(
    id BIGINT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    total_required_score BIGINT NOT NULL
);

--
-- room Table
--
CREATE TABLE IF NOT EXISTS room(
    id TEXT NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    is_public BOOLEAN NOT NULL,
    total_score_to_win SMALLINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    owner_player_id BIGINT NOT NULL
);

--
-- history Table
--
CREATE TABLE IF NOT EXISTS history(
    player_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    total_score BIGINT NOT NULL,
    is_winner BOOLEAN NOT NULL
);

--
-- game_state Table
--
CREATE TABLE IF NOT EXISTS game_state(
    id BIGINT NOT NULL PRIMARY KEY,
    state SMALLINT NOT NULL,
    room_id TEXT NOT NULL
);

--
-- score Table
--
CREATE TABLE IF NOT EXISTS score(
    score SMALLINT NOT NULL,
    player_id BIGINT NOT NULL,
    game_state_id BIGINT NOT NULL
);