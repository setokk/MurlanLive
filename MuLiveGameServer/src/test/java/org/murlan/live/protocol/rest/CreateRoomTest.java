package org.murlan.live.protocol.rest;

import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateRoomTest {
    private ProtocolConfig config;
    private RoomRESTClient restClient;
    private MLObjectMapper objectMapper;

    @Before
    public void setUp() {
        this.config = ConfigProvider.getProtocolConfig();
        this.restClient = new RoomRESTClient(config, new MLObjectMapper());
        this.objectMapper = new MLObjectMapper();
    }

    @Test
    public void printRoomJson() throws IOException, InterruptedException {
        Room room = prepareRoom();
        System.out.println("------Saving room:------");
        System.out.println(objectMapper.writeValueAsString(room));
        System.out.println("------Making REST call to /api/rooms/create");
        HttpResponse<String> response = restClient.createRoom(room);
        System.out.println("------Finished REST call to /api/rooms/create");
        System.out.println(response);
        Assert.assertTrue(HttpStatus.OK_200.getStatusCode() == response.statusCode());
    }

    public Room prepareRoom() {
        Player owner = new Player(1L, "player1", LocalDateTime.now(), "JWT1");
        List<Player> players = List.of(
                owner,
                new Player(2L, "player2", LocalDateTime.now(), "JWT2"),
                new Player(3L, "player3", LocalDateTime.now(), "JWT3"),
                new Player(4L, "player4", LocalDateTime.now(), "JWT4")
        );
        Map<Player, Short> score = HashMap.newHashMap(players.size());
        for (int i = 0; i < players.size(); i++) {
            score.put(players.get(i), (short) (players.size() - i - 1));
        }
        Map<Player, Short> score2 = HashMap.newHashMap(players.size());
        for (int i = 0; i < players.size(); i++) {
            score2.put(players.get(i), (short) (i + 1));
        }
        Map<Player, Short> score3 = HashMap.newHashMap(players.size());
        score3.put(players.get(0), (short) 2);
        score3.put(players.get(1), (short) 4);
        score3.put(players.get(2), (short) 1);
        score3.put(players.get(3), (short) 3);

        List<GameState> gameStates = List.of(
                GameState.builder()
                        .withState(GameState.State.FINISHED)
                        .withPlayers(players)
                        .withScore(score)
                        .build(),
                GameState.builder()
                        .withState(GameState.State.FINISHED)
                        .withPlayers(players)
                        .withScore(score3)
                        .build(),
                GameState.builder()
                        .withState(GameState.State.FINISHED)
                        .withPlayers(players)
                        .withScore(score2)
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
