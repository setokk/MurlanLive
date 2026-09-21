package org.murlan.live.game.logic;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.handler.OnGameFinish;
import org.murlan.live.game.logic.handler.OnGameStart;
import org.murlan.live.game.logic.handler.OnTurnTimeout;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
public class GameStateFactory {
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;
    private final RoomRESTClient roomRESTClient;
    private final ProtocolConfig config;
    private final ScheduledExecutorService scheduler;

    public GameState createGameState(Room room) {
        return new GameState(
                GameState.State.WAITING,
                room.getOwner(),
                room.getTurnDurationSeconds(),
                new OnGameStart(room, roomHandler, endpointHelper, config, scheduler),
                new OnGameFinish(room, roomHandler, endpointHelper, roomRESTClient),
                new OnTurnTimeout(room, roomHandler, endpointHelper)
        );
    }
}
