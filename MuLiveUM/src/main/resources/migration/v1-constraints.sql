ALTER TABLE ONLY history ADD CONSTRAINT fk_history_player FOREIGN KEY (player_id) REFERENCES player(id);
ALTER TABLE ONLY history ADD CONSTRAINT fk_history_room FOREIGN KEY (room_id) REFERENCES room(id);
ALTER TABLE ONLY history ADD CONSTRAINT history_pkey PRIMARY KEY (player_id, room_id);

-- Foreign key constraint fk_history_player
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'history'
        AND constraint_name = 'fk_history_player'
    ) THEN
ALTER TABLE ONLY history
    ADD CONSTRAINT fk_history_player FOREIGN KEY (player_id) REFERENCES player(id);
END IF;
END $$;

-- Foreign key constraint fk_history_room
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'history'
        AND constraint_name = 'fk_history_room'
    ) THEN
ALTER TABLE ONLY history
    ADD CONSTRAINT fk_history_room FOREIGN KEY (room_id) REFERENCES room(id);
END IF;
END $$;

-- Primary key constraint history_pkey
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'history'
        AND constraint_name = 'history_pkey'
    ) THEN
ALTER TABLE ONLY history
    ADD CONSTRAINT history_pkey PRIMARY KEY (player_id, room_id);
END IF;
END $$;