--
-- player Table
--
CREATE TABLE IF NOT EXISTS player(
    id BIGINT NOT NULL PRIMARY KEY,
    username character varying(50) NOT NULL,
    password character varying(50) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    progress BIGINT NOT NULL
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
    name character varying(50) NOT NULL,
    progress_to_unlock BIGINT NOT NULL
);

--
-- lobby Table
--
CREATE TABLE IF NOT EXISTS lobby(
    id BIGINT NOT NULL PRIMARY KEY,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL
);
CREATE SEQUENCE IF NOT EXISTS lobby_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;

--
-- history Table
--
CREATE TABLE IF NOT EXISTS history(
    player_id BIGINT NOT NULL,
    lobby_id BIGINT NOT NULL
);
ALTER TABLE ONLY history ADD CONSTRAINT fk_history_player FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE ONLY history ADD CONSTRAINT fk_history_lobby FOREIGN KEY (lobby_id) REFERENCES lobby(id);
ALTER TABLE ONLY history ADD CONSTRAINT history_pkey PRIMARY KEY (player_id, lobby_id);