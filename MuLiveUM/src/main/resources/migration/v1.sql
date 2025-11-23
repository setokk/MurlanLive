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
    id BIGINT NOT NULL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL
);
CREATE SEQUENCE IF NOT EXISTS room_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

--
-- history Table
--
CREATE TABLE IF NOT EXISTS history(
    player_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    total_score BIGINT NOT NULL,
    is_winner BOOLEAN NOT NULL
);