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
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.AvailableRoomsResp;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.CreateRoomResp;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.JoinRoomResp;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.SurrenderReq;
import org.murlan.live.protocol.api.SurrenderResp;
import org.murlan.live.protocol.api.error.GenericErrorResp;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.jwt.JwtUtils;
import org.murlan.live.protocol.um.AuthHttpClient;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;
import org.murlan.live.session.GameState;
import org.murlan.live.session.PlayerSession;
import org.murlan.live.session.Room;
import org.murlan.live.session.RoomHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);

    private final ProtocolConfig config = ConfigProvider.getProtocolConfig();
    private final Parser parser = new Parser(config);
    private final Generator generator = new Generator(config);
    private final EndpointHelper endpointHelper = new EndpointHelper(parser, generator);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthHttpClient authHttpClient = new AuthHttpClient(config);
    private final RoomHandler roomHandler = new RoomHandler();

    @OnOpen
    public void onOpen(Session session) throws IOException, InterruptedException {
        LOGGER.info("New connection with sessionId: {}", session.getId());

        String jwt = endpointHelper.getAndCheckQueryParam("jwt", session.getQueryString()).orElse("");
        if (!authHttpClient.validateJwt(jwt)) {
            endpointHelper.sendErrorMessage(session, new GenericErrorResp("Forbidden"));
            return;
        }
        PlayerDto playerDto = JwtUtils.decodeJWT(jwt);
        if (playerDto.isInvalid()) {
            endpointHelper.sendErrorMessage(session, new GenericErrorResp("Invalid JWT"));
            return;
        }
        if (roomHandler.jwtSessionExists(jwt)) {
            endpointHelper.sendErrorMessage(session, new GenericErrorResp("JWT session already exists"));
            return;
        }
        roomHandler.addSession(new PlayerSession(session, playerDto));

        LOGGER.info("Connection with sessionId: {} established!", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        LOGGER.info("From {{}}, received message: {}", session.getId(), message);

        Req req;
        try {
            req = parser.parse(message);
        } catch (InvalidDataException e) {
            endpointHelper.sendErrorMessage(session, new GenericErrorResp("Request body error"));
            return;
        }

        Optional<PlayerSession> optionalPlayerSession = roomHandler.getSession(session);
        if (optionalPlayerSession.isEmpty()) {
            endpointHelper.sendErrorMessage(session, new GenericErrorResp("No active session found for sessionId: " + session.getId()));
            return;
        }

        PlayerSession playerSession = optionalPlayerSession.get();
        PlayerDto player = playerSession.getPlayerDto();
        Room room = roomHandler.getPlayerRoom(playerSession);
        Resp resp = switch (req) {
            case GameStateReq gameStateReq -> {
                boolean isSuccessful = true;
                yield new GameStateResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case PlayHandReq playHandReq -> {
                boolean isSuccessful = room.getGameState().playHand(player, playHandReq.getCardCombination());
                yield new PlayHandResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case PassReq passReq -> {
                boolean isSuccessful = room.getGameState().pass(player);
                yield new PassResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case SurrenderReq surrenderReq -> {
                boolean isSuccessful = room.getGameState().surrender(player);
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
                yield new CreateRoomResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                        objectMapper.writeValueAsString(new RoomDto(roomToBeCreated.getId(), roomToBeCreated.getName(), roomToBeCreated.getNumPlayers()))
                );
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
        Optional<PlayerSession> optionalPlayerSession = roomHandler.getSession(session);
        if (optionalPlayerSession.isEmpty()) {
            return;
        }
        PlayerSession playerSession = optionalPlayerSession.get();

        Room room = roomHandler.getRoom(roomHandler.removeSession(playerSession));
        synchronized (room) {
            if (room.getNumPlayers() == 1) {
                roomHandler.removeRoom(room.getId());
            }
        }
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        LOGGER.error("Error", throwable);
    }
}
