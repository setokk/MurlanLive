package org.murlan.live.protocol.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;
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
    }

    @Test
    public void printRoomJson() throws JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(prepareRoom()));
    }

    public Room prepareRoom() {
        Player owner = new Player(1L, "Player1", LocalDateTime.now(), "JWT1");
        List<Player> players = List.of(
                owner,
                new Player(2L, "Player2", LocalDateTime.now(), "JWT2"),
                new Player(3L, "Player3", LocalDateTime.now(), "JWT3"),
                new Player(4L, "Player4", LocalDateTime.now(), "JWT4")
        );
        Map<Player, Short> score = HashMap.newHashMap(players.size());
        for (int i = 0; i < players.size(); i++) {
            score.put(players.get(i), (short) (players.size() - i - 1));
        }
        List<GameState> gameStates = List.of(
                GameState.builder()
                        .withState(GameState.State.FINISHED)
                        .withPlayers(players)
                        .withScore(score)
                        .build(),
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
