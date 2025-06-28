package org.murlan.live.endpoint;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.config.ConfigProvider;
import org.murlan.live.protocol.Parser;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.SurrenderReq;
import org.murlan.live.protocol.auth.AuthHttpClient;
import org.murlan.live.protocol.auth.JwtUtils;
import org.murlan.live.session.GameState;
import org.murlan.live.session.Room;
import org.murlan.live.session.RoomHandler;
import org.murlan.live.session.player.PlayerDto;
import org.murlan.live.session.player.PlayerSession;

import java.io.IOException;
import java.util.Map;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);

    private final Parser parser = new Parser(ConfigProvider.getProtocolConfig());
    private final AuthHttpClient authHttpClient = new AuthHttpClient(ConfigProvider.getProtocolConfig());
    private final RoomHandler roomHandler = new RoomHandler();

    @OnOpen
    public void onOpen(Session session) throws IOException, InterruptedException {
        LOGGER.info("New connection with sessionId: {}", session.getId());

        Map<String, String> queryParams = parser.parseQueryParams(session.getQueryString());
        String roomId = queryParams.get("roomId");
        String roomName = queryParams.get("roomName");
        boolean isPublic = Boolean.parseBoolean(queryParams.get("isPublic"));
        String passcode = queryParams.get("passcode");
        String jwt = queryParams.get("jwt");

        // Skip request if no roomId is provided
        if (roomId == null) {
            return;
        }

        // Check validity of JWT token that was sent
        boolean isValidJWT = authHttpClient.validateJwt(jwt);
        if (!isValidJWT) {
            return;
        }

        // Check if player is actually valid
        PlayerDto playerDto = JwtUtils.decodeJWT(jwt);
        if (!playerDto.isValid()) {
            return;
        }

        // Update <session -> room ID> map to be able to remove them later easily
        var sessionToRoomMap = roomHandler.getSessionToRoomMap();
        var rooms = roomHandler.getRooms();
        sessionToRoomMap.put(new PlayerSession(session, playerDto), roomId);

        if (rooms.containsKey(roomId)) { // Player wants to join Existing Room

        } else { // Player wants to create New Room
            rooms.put(roomId, new Room(roomId, roomName, isPublic, passcode, new GameState(GameState.State.WAITING, playerDto)));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Req req = parser.parse(message);
        switch (req) {
            case GameStateReq gameStateReq -> {

            }
            case PlayHandReq playHandReq -> {

            }
            case PassReq passReq -> {

            }
            case SurrenderReq surrenderReq -> {

            }
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String roomId = roomHandler.getSessionToRoomMap().remove(PlayerSession.fromSession(session));
        roomHandler.getRooms().remove(roomId);
        session.close();
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error", throwable);
    }
}
