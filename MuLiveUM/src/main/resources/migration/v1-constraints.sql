-- Foreign key constraint fk_room_player
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'room'
        AND constraint_name = 'fk_room_player'
    ) THEN
ALTER TABLE ONLY room
    ADD CONSTRAINT fk_room_player FOREIGN KEY (owner_player_id) REFERENCES player(id);
END IF;
END $$;

-- Foreign key constraint fk_game_state_room
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'game_state'
        AND constraint_name = 'fk_game_state_room'
    ) THEN
ALTER TABLE ONLY game_state
    ADD CONSTRAINT fk_game_state_room FOREIGN KEY (room_id) REFERENCES room(id);
END IF;
END $$;

-- Foreign key constraint fk_score_game_state
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'score'
        AND constraint_name = 'fk_score_game_state'
    ) THEN
ALTER TABLE ONLY score
    ADD CONSTRAINT fk_score_game_state FOREIGN KEY (game_state_id) REFERENCES game_state(id);
END IF;
END $$;

-- Foreign key constraint fk_score_player
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'score'
        AND constraint_name = 'fk_score_player'
    ) THEN
ALTER TABLE ONLY score
    ADD CONSTRAINT fk_score_player FOREIGN KEY (player_id) REFERENCES player(id);
END IF;
END $$;

-- Foreign key constraint fk_score_total_room
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'score_total'
        AND constraint_name = 'fk_score_total_room'
    ) THEN
ALTER TABLE ONLY score_total
    ADD CONSTRAINT fk_score_total_room FOREIGN KEY (room_id) REFERENCES room(id);
END IF;
END $$;

-- Foreign key constraint fk_score_total_player
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_name = 'score_total'
        AND constraint_name = 'fk_score_total_player'
    ) THEN
ALTER TABLE ONLY score_total
    ADD CONSTRAINT fk_score_total_player FOREIGN KEY (player_id) REFERENCES player(id);
END IF;
END $$;