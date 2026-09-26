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
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.GameStateFactory;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.AvailableRoomsResp;
import org.murlan.live.protocol.api.ChatReq;
import org.murlan.live.protocol.api.ChatResp;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.CreateRoomResp;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.api.GiveCardReq;
import org.murlan.live.protocol.api.GiveCardResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.InformPlayerChatResp;
import org.murlan.live.protocol.api.InformPlayerJoinRoomResp;
import org.murlan.live.protocol.api.InformPlayerKickedResp;
import org.murlan.live.protocol.api.InformPlayerLeaveRoomResp;
import org.murlan.live.protocol.api.InformPlayerLostConnectionResp;
import org.murlan.live.protocol.api.InformPlayerReadyResp;
import org.murlan.live.protocol.api.InformUpdateRoomDetailsResp;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.JoinRoomResp;
import org.murlan.live.protocol.api.KickReq;
import org.murlan.live.protocol.api.KickResp;
import org.murlan.live.protocol.api.LeaveRoomReq;
import org.murlan.live.protocol.api.LeaveRoomResp;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.api.ReadyReq;
import org.murlan.live.protocol.api.ReadyResp;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.UpdateRoomDetailsReq;
import org.murlan.live.protocol.api.UpdateRoomDetailsResp;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.dto.RoomDetailsDto;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

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
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static final Consumer<Room> onPlayerLeaveOrDisconnect = room -> {
        try {
            if (GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
                return;
            }
            roomRESTClient.createRoom(room);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

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

        if (roomHandler.isPlayerSessionCurrentlyActive(player)) {
            endpointHelper.closeWithErrorMessage(session, MuliveCloseReason.JWT_SESSION_ALREADY_EXISTS);
            return;
        }

        roomHandler.addSession(new PlayerSession(session, player));

        log.info("Connection with sessionId: {} established! Player.id = {}, Player.username = {}", session.getId(), player.getId(), player.getUsername());
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
        boolean isRoomPresent = room != null;

        log.info(
                "[IN] Processing valid event...\n-> event={}\n\t- playerId={}\n\t- payload={}\n\t- sessionId={}\n",
                req.getClass().getSimpleName(),
                player.getId(),
                message,
                playerSession.getSession().getId()
        );

        Resp informResp = null;
        Resp resp = switch (req) {
            case GameStateReq gameStateReq -> {
                GameStateDto gameStateDto = null;
                if (isRoomPresent) {
                    gameStateDto = GameStateDto.from(room, player, config);
                }
                yield new GameStateResp(
                        isRoomPresent ? ResponseStatus.OK : ResponseStatus.ERROR,
                        gameStateDto
                );
            }
            case PlayHandReq playHandReq -> {
                boolean isSuccessful = isRoomPresent && room.playHand(player, playHandReq.getCardCombination());
                if (isSuccessful) {
                    informResp = new InformPlayHandResp(ResponseStatus.OK, player.getId(), playHandReq.getCardCombination());
                }
                yield new PlayHandResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                        playHandReq.getCardCombination()
                );
            }
            case PassReq passReq -> {
                boolean isSuccessful = isRoomPresent && room.pass(player);
                if (isSuccessful) {
                    boolean canCurrPlayerPlayAnyHand = room.getActiveGameState().getPassCounter().getCounter() == 0;
                    informResp = new InformPassResp(ResponseStatus.OK, player.getId(), canCurrPlayerPlayAnyHand);
                }
                yield new PassResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case AvailableRoomsReq availableRoomsReq -> {
                List<RoomDto> availableRooms = roomHandler.getAvailableRooms();
                yield new AvailableRoomsResp(ResponseStatus.OK, availableRooms);
            }
            case JoinRoomReq joinRoomReq -> {
                boolean isSuccessful = roomHandler.joinRoom(joinRoomReq.getRoomId(), playerSession);
                if (isSuccessful) {
                    room = roomHandler.getPlayerRoom(playerSession);
                    informResp = new InformPlayerJoinRoomResp(ResponseStatus.OK, player);
                }
                yield new JoinRoomResp(isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR);
            }
            case CreateRoomReq createRoomReq -> {
                Room newRoom = new Room(
                        createRoomReq.getRoomName(),
                        createRoomReq.isPublic(),
                        LocalDateTime.now(),
                        createRoomReq.getTotalScoreToWin(),
                        playerSession.getPlayer(),
                        createRoomReq.getTurnDurationSeconds(),
                        new GameStateFactory(roomHandler, endpointHelper, roomRESTClient, config, scheduler)
                );

                RoomDto roomDto = roomHandler.createRoom(newRoom, playerSession);
                yield new CreateRoomResp(
                        roomDto.isValid() ? ResponseStatus.OK : ResponseStatus.ERROR,
                        roomDto
                );
            }
            case GiveCardReq giveCardReq -> {
                Player receivingPlayer = new Player(giveCardReq.getReceivingPlayerId());

                boolean haveBothPlayerGivenCards = false;

                boolean isSuccessful = isRoomPresent && room.giveCard(giveCardReq.getCard(), player, receivingPlayer);
                if (isSuccessful) {
                    haveBothPlayerGivenCards = room.getActiveGameState().haveBothPlayersGivenCards();
                    informResp = new InformGiveCardResp(ResponseStatus.OK,
                            player.getId(),
                            receivingPlayer.getId(),
                            giveCardReq.getCard(),
                            haveBothPlayerGivenCards
                    );
                }
                yield new GiveCardResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                        haveBothPlayerGivenCards,
                        giveCardReq.getCard()
                );
            }
            case LeaveRoomReq leaveRoomReq -> {
                if (!isRoomPresent) {
                    yield new LeaveRoomResp(ResponseStatus.ERROR);
                }

                Optional<List<PlayerSession>> playersInRoom = roomHandler.removeSession(playerSession, false, onPlayerLeaveOrDisconnect);

                boolean isSuccessful = playersInRoom.isPresent();
                if (isSuccessful) {
                    InformPlayerLeaveRoomResp informPlayerLeaveRoomResp = new InformPlayerLeaveRoomResp(ResponseStatus.OK, playerSession.getPlayer().getId());
                    endpointHelper.informPlayers(informPlayerLeaveRoomResp, null, playersInRoom.get());
                }

                yield new LeaveRoomResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
                );
            }
            case ReadyReq readyReq -> {
                boolean isSuccessful = isRoomPresent && room.ready(player);
                if (isSuccessful) {
                    informResp = new InformPlayerReadyResp(ResponseStatus.OK, player);
                }
                yield new ReadyResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
                );
            }
            case ChatReq chatReq -> {
                if (isRoomPresent) {
                    informResp = new InformPlayerChatResp(ResponseStatus.OK, chatReq.getMessage(), player);
                }
                yield new ChatResp(
                        isRoomPresent ? ResponseStatus.OK : ResponseStatus.ERROR
                );
            }
            case UpdateRoomDetailsReq updateRoomDetailsReq -> {
                RoomDetailsDto roomDetailsDto = new RoomDetailsDto(
                        updateRoomDetailsReq.getRoomName(),
                        updateRoomDetailsReq.getTotalScoreToWin(),
                        updateRoomDetailsReq.getTurnDurationSeconds()
                );
                boolean isSuccessful = isRoomPresent && roomHandler.updateRoom(room.getId(), roomDetailsDto, player);
                if (isSuccessful) {
                    informResp = new InformUpdateRoomDetailsResp(ResponseStatus.OK, roomDetailsDto);
                }
                yield new UpdateRoomDetailsResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                        roomDetailsDto
                );
            }
            case KickReq kickReq -> {
                PlayerSession kickedPlayerSession = null;
                if (isRoomPresent) {
                    kickedPlayerSession = roomHandler.kickPlayer(room.getId(), kickReq.getPlayerToKickId(), player);
                }

                boolean isSuccessful = kickedPlayerSession != null;
                if (isSuccessful) {
                    informResp = new InformPlayerKickedResp(ResponseStatus.OK, kickedPlayerSession.getPlayer());
                    endpointHelper.send(informResp, kickedPlayerSession); // send here because they are removed and unreachable from roomHandler.getPlayersInRoom
                }
                yield new KickResp(
                        isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                        kickedPlayerSession != null ? kickedPlayerSession.getPlayer() : null
                );
            }
            default -> throw new IllegalStateException("Unexpected request: " + req);
        };

        endpointHelper.send(resp, playerSession);

        if (room != null) {
            endpointHelper.informPlayers(informResp, playerSession, roomHandler.getPlayersInRoom(room.getId()));

            if (room.getActiveGameState().shouldGameStart()) {
                room.getActiveGameState().startGame();
            }
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Optional<PlayerSession> optionalPlayerSession = roomHandler.getSession(session);
        if (optionalPlayerSession.isEmpty()) {
            return;
        }
        PlayerSession playerSession = optionalPlayerSession.get();

        Optional<List<PlayerSession>> playersInRoom = roomHandler.removeSession(playerSession, true, onPlayerLeaveOrDisconnect);
        if (playersInRoom.isEmpty()) {
            return;
        }

        InformPlayerLostConnectionResp informPlayerLostConnectionResp = new InformPlayerLostConnectionResp(
                ResponseStatus.OK, playerSession.getPlayer().getId()
        );
        endpointHelper.informPlayers(informPlayerLostConnectionResp, null, playersInRoom.get());

        log.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.error("Error", throwable);
    }
}
