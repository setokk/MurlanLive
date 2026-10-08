package org.murlan.live.endpoint;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqDispatcher;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.req.handler.AvailableRoomsReqHandler;
import org.murlan.live.endpoint.req.handler.BlockReqHandler;
import org.murlan.live.endpoint.req.handler.ChatReqHandler;
import org.murlan.live.endpoint.req.handler.CreateRoomReqHandler;
import org.murlan.live.endpoint.req.handler.GameStateReqHandler;
import org.murlan.live.endpoint.req.handler.GiveCardReqHandler;
import org.murlan.live.endpoint.req.handler.JoinRoomReqHandler;
import org.murlan.live.endpoint.req.handler.KickReqHandler;
import org.murlan.live.endpoint.req.handler.LeaveRoomReqHandler;
import org.murlan.live.endpoint.req.handler.MuteReqHandler;
import org.murlan.live.endpoint.req.handler.PassReqHandler;
import org.murlan.live.endpoint.req.handler.PlayHandReqHandler;
import org.murlan.live.endpoint.req.handler.ReadyReqHandler;
import org.murlan.live.endpoint.req.handler.ReconnectReqHandler;
import org.murlan.live.endpoint.req.handler.UnBlockReqHandler;
import org.murlan.live.endpoint.req.handler.UnMuteReqHandler;
import org.murlan.live.endpoint.req.handler.UpdateRoomDetailsReqHandler;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerLostConnectionResp;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.ReconnectReq;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.jwt.JwtUtils;
import org.murlan.live.protocol.rest.PlayerRESTClient;
import org.murlan.live.protocol.rest.RoomRESTClient;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;
import org.murlan.live.util.MLObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    private static final PlayerRESTClient playerRESTClient = new PlayerRESTClient(config, objectMapper);
    private static final RoomRESTClient roomRESTClient = new RoomRESTClient(config, objectMapper);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private static final ReqDispatcher reqDispatcher = new ReqDispatcher(List.of(
            new GameStateReqHandler(config),
            new PlayHandReqHandler(),
            new PassReqHandler(),
            new AvailableRoomsReqHandler(roomHandler),
            new JoinRoomReqHandler(roomHandler),
            new CreateRoomReqHandler(roomHandler, endpointHelper, roomRESTClient, config, scheduler),
            new GiveCardReqHandler(),
            new LeaveRoomReqHandler(roomHandler, endpointHelper),
            new ReadyReqHandler(),
            new ChatReqHandler(),
            new UpdateRoomDetailsReqHandler(roomHandler),
            new KickReqHandler(roomHandler, endpointHelper),
            new MuteReqHandler(),
            new UnMuteReqHandler(),
            new BlockReqHandler(playerRESTClient),
            new UnBlockReqHandler(playerRESTClient),
            new ReconnectReqHandler(roomHandler)
    ));

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

        Set<Player> mutedPlayers = ConcurrentHashMap.newKeySet();
        Set<Player> blockedPlayers = ConcurrentHashMap.newKeySet();

        playerRESTClient.getBlockedPlayers(jwt)
                .ifPresent(blockedPlayers::addAll);

        roomHandler.addSession(new PlayerSession(session, player, mutedPlayers, blockedPlayers));

        log.info("Connection with sessionId: {} established! Player.id = {}, Player.username = {}", session.getId(), player.getId(), player.getUsername());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
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

        log.info(
                "[IN] Processing valid event...\n-> event={}\n\t- playerId={}\n\t- payload={}\n\t- sessionId={}\n",
                req.getClass().getSimpleName(),
                player.getId(),
                message,
                playerSession.getSession().getId()
        );

        ReqHandlerResult result = reqDispatcher.dispatch(req, new ReqContext(playerSession, room));
        endpointHelper.send(result.resp(), playerSession);

        if (req instanceof JoinRoomReq || req instanceof ReconnectReq) {
            room = roomHandler.getPlayerRoom(playerSession);
        }

        if (room != null) {
            endpointHelper.informPlayers(result.informResp(), playerSession, roomHandler.getPlayersInRoom(room.getId()));
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

        Optional<List<PlayerSession>> playersInRoom = roomHandler.handleDisconnection(playerSession, scheduler, Room::finishDueToPlayerExit);
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

    static {
        // Save rooms if any shutdown happens to the game server, no matter the state they are in.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Room room : roomHandler.getAllRooms()) {
                try {
                    if (!GameState.State.FINISHED.equals(room.getActiveGameState().getState())) {
                        return;
                    }
                    roomRESTClient.createRoom(room);
                } catch (IOException | InterruptedException e) {
                    log.error("Could not save room with id: {}", room.getId());
                    log.error(e);
                }
            }
        }));
    }
}
