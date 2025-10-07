package org.murlan.live.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.CreateRoomResp;
import org.murlan.live.protocol.api.error.GenericErrorResp;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.AvailableRoomsResp;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.JoinRoomResp;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.api.SurrenderResp;
import org.murlan.live.protocol.auth.JwtUtils;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.SurrenderReq;
import org.murlan.live.protocol.auth.AuthHttpClient;
import org.murlan.live.session.GameState;
import org.murlan.live.session.Room;
import org.murlan.live.session.RoomHandler;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.session.PlayerSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);

    private final ProtocolConfig config = ConfigProvider.getProtocolConfig();
    private final Parser parser = new Parser(config);
    private final Generator generator = new Generator(config);
    private final AuthHttpClient authHttpClient = new AuthHttpClient(config);
    private final RoomHandler roomHandler = new RoomHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @OnOpen
    public void onOpen(Session session) throws IOException, InterruptedException {
        LOGGER.info("New connection with sessionId: {}", session.getId());

        if (session.getQueryString() == null || session.getQueryString().isEmpty()) {
            session.close();
            return;
        }
        Map<String, String> queryParams = parser.parseQueryParams(session.getQueryString());
        String jwt = queryParams.get("jwt");
        if (!authHttpClient.validateJwt(jwt)) {
            session.close();
            return;
        }
        PlayerDto playerDto = JwtUtils.decodeJWT(jwt);
        if (playerDto.isInvalid()) {
            session.close();
            return;
        }

        roomHandler.addSession(new PlayerSession(session, playerDto));
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Req req = parser.parse(message);
        if (req == null) {
            sendErrorMessage(session, new GenericErrorResp("Request Body Error"));
            return;
        }

        PlayerSession playerSession = roomHandler.getSession(req.getJWT());
        if (playerSession == null) {
            sendErrorMessage(session, new GenericErrorResp("JWT Not Recognized"));
            return;
        }

        Room room = roomHandler.getPlayerRoom(playerSession);
        Resp resp = switch (req) {
            case GameStateReq gameStateReq -> {
                boolean isSuccessful = true;
                yield new GameStateResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case PlayHandReq playHandReq -> {
                boolean isSuccessful = room.getGameState().playHand(req.getJWT(), playHandReq.getCardCombination());
                yield new PlayHandResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case PassReq passReq -> {
                boolean isSuccessful = room.getGameState().pass(req.getJWT());
                yield new PassResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case SurrenderReq surrenderReq -> {
                boolean isSuccessful = room.getGameState().surrender(req.getJWT());
                yield new SurrenderResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case AvailableRoomsReq availableRoomsReq -> {
                List<RoomDto> availableRooms = roomHandler.getAvailableRooms();
                yield new AvailableRoomsResp(ResponseStatus.OK, objectMapper.writeValueAsString(availableRooms));
            }
            case JoinRoomReq joinRoomReq -> {
                boolean isSuccessful = roomHandler.joinRoom(joinRoomReq.getRoomId(), joinRoomReq.getPasscode(), playerSession);
                yield new JoinRoomResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case CreateRoomReq createRoomReq -> {
                Room roomToBeCreated = new Room(
                        UUID.randomUUID().toString(),
                        createRoomReq.getRoomName(),
                        createRoomReq.isPublic(),
                        createRoomReq.getPasscode(),
                        new GameState(GameState.State.WAITING, playerSession.getPlayerDto()),
                        LocalDateTime.now()
                );
                boolean isSuccessful = roomHandler.createRoom(roomToBeCreated, playerSession);
                yield new CreateRoomResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            default -> throw new IllegalStateException("Unexpected request: " + req);
        };

        String responseString = generator.generateMessage(resp);
        if (!responseString.isEmpty()) {
            session.getBasicRemote().sendText(responseString);
        }
    }

    @OnClose
    public void onClose(Session session) {
        PlayerSession playerSession = PlayerSession.fromSession(session);
        Room room = roomHandler.getRoom(roomHandler.removeSession(playerSession));
        synchronized (room) {
            if (room.getGameState().getPlayers().size() == 1) {
                roomHandler.removeRoom(room.getId());
            }
        }
        roomHandler.removeSession(playerSession);
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error", throwable);
    }

    private void sendErrorMessage(Session session, GenericErrorResp resp) throws IOException {
        String errorMessage = generator.generateMessage(resp);
        session.getBasicRemote().sendText(errorMessage);
        session.close();
    }
}
