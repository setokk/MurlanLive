package org.murlan.live.endpoint;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.GameStateFactory;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.AvailableRoomsResp;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.CreateRoomResp;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.api.GiveCardReq;
import org.murlan.live.protocol.api.GiveCardResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.InformSurrenderResp;
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
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.jwt.JwtUtils;
import org.murlan.live.protocol.rest.PlayerRESTClient;
import org.murlan.live.protocol.rest.RoomRESTClient;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger log = LogManager.getLogger(GameLobbyEndpoint.class);

    /**
     * Shared state between sessions
     */
    private static final ProtocolConfig config = ConfigProvider.getProtocolConfig();
    private static final MLObjectMapper objectMapper = new MLObjectMapper();

    private static final Parser parser = new Parser(config);
    private static final Generator generator = new Generator(config, objectMapper);

    private static final EndpointHelper endpointHelper = new EndpointHelper(parser, generator, config);
    private static final RoomHandler roomHandler = new RoomHandler();

    private static final PlayerRESTClient playerRESTClient = new PlayerRESTClient(config);
    private static final RoomRESTClient roomRESTClient = new RoomRESTClient(config, objectMapper);

    static {
        // Save rooms if any shutdown happens to the game server, no matter the state they are in.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Room room : roomHandler.getAllRooms()) {
                try {
                    roomRESTClient.createRoom(room);
                } catch (IOException | InterruptedException e) {
                    log.error("Could not save room with id: {}", room.getId());
                    log.error(e);
                }
            }
        }));
    }

    @OnOpen
    public void onOpen(Session session) throws IOException, InterruptedException {
        log.info("Incoming connection request with sessionId: {}", session.getId());

        String jwt = endpointHelper.getAndCheckQueryParam("jwt", session.getQueryString()).orElse("");
        if (!playerRESTClient.validateJwt(jwt)) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.FORBIDDEN);
            return;
        }
        Player player = JwtUtils.decodeJWT(jwt);
        if (player.isInvalid()) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.INVALID_JWT);
            return;
        }
        if (roomHandler.jwtSessionExists(jwt)) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.JWT_SESSION_ALREADY_EXISTS);
            return;
        }
        roomHandler.addSession(new PlayerSession(session, player));

        log.info("Connection with sessionId: {} established!", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("From {{}}, received message: {}", session.getId(), message);

        Req req;
        try {
            req = parser.parse(message);
        } catch (InvalidDataException e) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.REQUEST_BODY_ERROR);
            return;
        }

        Optional<PlayerSession> optionalPlayerSession = roomHandler.getSession(session);
        if (optionalPlayerSession.isEmpty()) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.NO_ACTIVE_SESSION);
            return;
        }

        PlayerSession playerSession = optionalPlayerSession.get();
        Player player = playerSession.getPlayer();
        Room room = roomHandler.getPlayerRoom(playerSession);

        Resp informResp = null;
        Resp resp = switch (req) {
            case GameStateReq gameStateReq -> {
                GameStateDto gameStateDto = GameStateDto.from(room, player, config);
                yield new GameStateResp(
                        ResponseStatus.OK,
                        objectMapper.writeValueAsString(gameStateDto)
                );
            }
            case PlayHandReq playHandReq -> {
                boolean isSuccessful = room.getActiveGameState().playHand(player, playHandReq.getCardCombination());
                if (isSuccessful) {
                    informResp = new InformPlayHandResp(ResponseStatus.OK, player.getId(), playHandReq.getCardCombination());
                }
                yield new PlayHandResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case PassReq passReq -> {
                boolean isSuccessful = room.getActiveGameState().pass(player);
                if (isSuccessful) {
                    informResp = new InformPassResp(ResponseStatus.OK, player.getId());
                }
                yield new PassResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case SurrenderReq surrenderReq -> {
                boolean isSuccessful = room.getActiveGameState().surrender(player);
                if (isSuccessful) {
                    informResp = new InformSurrenderResp(ResponseStatus.OK, player.getId());
                }
                yield new SurrenderResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case AvailableRoomsReq availableRoomsReq -> {
                List<RoomDto> availableRooms = roomHandler.getAvailableRooms();
                yield new AvailableRoomsResp(ResponseStatus.OK, availableRooms);
            }
            case JoinRoomReq joinRoomReq -> {
                boolean isSuccessful = roomHandler.joinRoom(joinRoomReq.getRoomId(), playerSession);
                yield new JoinRoomResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case CreateRoomReq createRoomReq -> {
                Room newRoom = new Room(
                        createRoomReq.getRoomName(),
                        createRoomReq.isPublic(),
                        LocalDateTime.now(),
                        createRoomReq.getTotalScoreToWin(),
                        playerSession.getPlayer(),
                        new GameStateFactory(roomRESTClient, endpointHelper, roomHandler, config)
                );

                RoomDto roomDto = roomHandler.createRoom(newRoom, playerSession);
                yield new CreateRoomResp(
                        roomDto.isValid() ? ResponseStatus.OK : ResponseStatus.ERROR,
                        roomDto
                );
            }
            case GiveCardReq giveCardReq -> {
                Player receivingPlayer = new Player(giveCardReq.getReceivingPlayerId());
                boolean isSuccessful = room.getActiveGameState().giveCard(giveCardReq.getCard(), player, receivingPlayer);
                if (isSuccessful) {
                    informResp = new InformGiveCardResp(ResponseStatus.OK,
                            player.getId(), receivingPlayer.getId(), giveCardReq.getCard(),
                            room.getActiveGameState().haveBothPlayersGivenCards()
                    );
                }
                yield new GiveCardResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
                );
            }
            default -> throw new IllegalStateException("Unexpected request: " + req);
        };

        String responseString = generator.generateMessage(resp);
        if (!responseString.isEmpty()) {
            session.getBasicRemote().sendText(responseString);
        }

        if (room != null) {
            endpointHelper.informPlayers(informResp, playerSession, roomHandler.getPlayersInRoom(room.getId()));
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Optional<PlayerSession> optionalPlayerSession = roomHandler.getSession(session);
        if (optionalPlayerSession.isEmpty()) {
            return;
        }
        PlayerSession playerSession = optionalPlayerSession.get();

        Optional<String> optionalRoomId = roomHandler.removeSession(playerSession);
        if (optionalRoomId.isPresent()) {
            Room room = roomHandler.getRoom(optionalRoomId.get());
            synchronized (room) {
                room.getActiveGameState().surrender(playerSession.getPlayer());
                if (room.getNumPlayers() == 0) {
                    roomHandler.removeRoom(room.getId());
                }
            }
        }
        log.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.error("Error", throwable);
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("Failed to close session", e);
            }
        }
    }
}
