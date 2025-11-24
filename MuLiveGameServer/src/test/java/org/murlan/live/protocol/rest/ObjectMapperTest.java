package org.murlan.live.protocol.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.util.MLObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ObjectMapperTest {
    private MLObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.objectMapper = new MLObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);
    }

    @Test
    public void printRoomJson() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(prepareRoom()));
    }

    public Room prepareRoom() {
        PlayerDto owner = new PlayerDto(1L, "Player1", LocalDateTime.now().toString(), "JWT1");
        List<PlayerDto> players = List.of(
                owner,
                new PlayerDto(2L, "Player2", LocalDateTime.now().toString(), "JWT2"),
                new PlayerDto(3L, "Player3", LocalDateTime.now().toString(), "JWT3"),
                new PlayerDto(4L, "Player4", LocalDateTime.now().toString(), "JWT4")
        );
        Map<PlayerDto, Short> score = HashMap.newHashMap(players.size());
        for (int i = 0; i < players.size(); i++) {
            score.put(players.get(i), (short) (players.size() - i - 1));
        }
        List<GameState> gameStates = List.of(
                GameState.builder()
                        .withState(GameState.State.FINISHED)
                        .withPlayers(players)
                        .withScore(score)
                        .build()
        );

        return Room.builder()
                .withId(UUID.randomUUID().toString())
                .withName("Room 1")
                .withIsPublic(true)
                .withCreationDate(LocalDateTime.now())
                .withTotalScoreToWin((short) 21)
                .withOwner(owner)
                .withPasscode("passcode")
                .withGameStates(gameStates)
                .build();
    }
}
