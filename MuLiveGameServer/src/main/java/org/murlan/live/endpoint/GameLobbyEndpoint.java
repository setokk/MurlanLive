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
import org.murlan.live.config.ProtocolConfig;
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
import java.util.UUID;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);

    private final ProtocolConfig config = ConfigProvider.getProtocolConfig();
    private final Parser parser = new Parser(config);
    private final AuthHttpClient authHttpClient = new AuthHttpClient(config);
    private final RoomHandler roomHandler = new RoomHandler();

    @OnOpen
    public void onOpen(Session session) throws IOException, InterruptedException {
        LOGGER.info("New connection with sessionId: {}", session.getId());

        if (session.getQueryString() == null || session.getQueryString().isEmpty()) {
            session.close();
            return;
        }

        Map<String, String> queryParams = parser.parseQueryParams(session.getQueryString());
        String roomId = queryParams.get("roomId");
        String roomName = queryParams.get("roomName");
        boolean isPublic = Boolean.parseBoolean(queryParams.get("isPublic"));
        String passcode = queryParams.get("passcode");
        String jwt = queryParams.get("jwt");

        // Skip request if no roomId is provided
        if (roomId == null && roomName == null) {
            session.close();
            return;
        }

        // Check validity of JWT token that was sent
        boolean isValidJWT = authHttpClient.validateJwt(jwt);
        if (!isValidJWT) {
            session.close();
            return;
        }

        // Check if player is actually valid
        PlayerDto playerDto = JwtUtils.decodeJWT(jwt);
        if (!playerDto.isValid()) {
            session.close();
            return;
        }

        // Create New Room ID
        roomId = (roomId == null) ? UUID.randomUUID().toString() : roomId;

        // Update <session -> room ID> map to be able to remove them later easily
        PlayerSession playerSession = new PlayerSession(session, playerDto);
        roomHandler.addSession(roomId, playerSession);

        if (roomHandler.roomExists(roomId)) { // Player wants to join Existing Room
            boolean wasJoinSuccessful = roomHandler.addPlayerToRoom(roomId, playerDto);
            if (!wasJoinSuccessful) {
                roomHandler.removeSession(jwt);
            }
        } else { // Player wants to create New Room
            roomHandler.addRoom(roomId, new Room(roomId, roomName, isPublic, passcode, new GameState(GameState.State.WAITING, playerDto)));
            session.getBasicRemote().sendText(jwt + config.getProtocol_delimiter() + roomId);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Req req = parser.parse(message);
        PlayerSession playerSession = roomHandler.getPlayerSession(req.getJWT());
        Room room = roomHandler.getPlayerRoom(playerSession);
        switch (req) {
            case GameStateReq gameStateReq -> {

            }
            case PlayHandReq playHandReq -> {
                room.getGameState().playHand(req.getJWT(), playHandReq.getCardCombination());
                session.getBasicRemote().sendText(req.getJWT() + config.getProtocol_delimiter() + room.getId() + config.getProtocol_delimiter() + room.getGameState().getPlayers().size());
            }
            case PassReq passReq -> {
                room.getGameState().pass(req.getJWT());
            }
            case SurrenderReq surrenderReq -> {
                room.getGameState().surrender(req.getJWT());
            }
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        PlayerSession playerSession = PlayerSession.fromSession(session);
        Room room = roomHandler.getRoom(roomHandler.removeSession(playerSession));
        synchronized (room) {
            if (room.getGameState().getPlayers().size() == 1) {
                roomHandler.removeRoom(room.getId());
            }
        }
        session.close();
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error", throwable);
    }
}
