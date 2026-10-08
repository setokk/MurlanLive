package org.murlan.live.endpoint.session;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.websocket.Session;
import lombok.NonNull;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.dto.RoomDetailsDto;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.dto.UpdatedRoomDetailsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoomHandler {
    private record DisconnectionTask(UUID roomId, ScheduledFuture<?> graceTask) {}

    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, UUID> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<PlayerSession>> roomIdToSessionMap = new ConcurrentHashMap<>(); // for efficient retrieval of players in a room
    private final ConcurrentHashMap<UUID, Room> roomIdToRoomMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, DisconnectionTask> disconnectedPlayers = new ConcurrentHashMap<>();

    public void addSession(@NonNull PlayerSession playerSession) {
        jwtToSessionMap.putIfAbsent(playerSession.getPlayer().getJwt(), playerSession);
    }

    public Optional<PlayerSession> getSession(@NonNull Session session) {
        return jwtToSessionMap.values().stream()
                .filter(s -> s.getSession().getId().equals(session.getId()))
                .findAny();
    }

    public Optional<List<PlayerSession>> removeSession(
            @NonNull PlayerSession playerSession,
            boolean hasPlayerLostConnection,
            Consumer<Room> onPlayerLeaveOrDisconnect
    ) {
        if (hasPlayerLostConnection) {
            jwtToSessionMap.remove(playerSession.getPlayer().getJwt());
        }

        UUID roomId = sessionToRoomIdMap.remove(playerSession);
        if (roomId == null) {
            return Optional.empty();
        }

        Room room = roomIdToRoomMap.get(roomId);
        if (room == null) {
            return Optional.empty();
        }

        synchronized (room) {
            unlinkPlayerSession(roomId, playerSession);
            return finalizePlayerRemoval(room, playerSession.getPlayer(), hasPlayerLostConnection, onPlayerLeaveOrDisconnect);
        }
    }

    /**
     * Handles an {@code @OnClose} (lost connection) event.
     * If the room's active game is PLAYING/GIVING_CARDS, the player's seat is kept and a reconnection grace
     * period is started (see {@link #reconnectPlayer}); otherwise this behaves like an immediate leave.
     * @return the other player sessions in the room, to be informed of the lost connection (present whether
     * or not a grace period was started).
     */
    public Optional<List<PlayerSession>> handleDisconnection(
            @NonNull PlayerSession playerSession,
            @NonNull ScheduledExecutorService scheduler,
            Consumer<Room> onPlayerLeaveOrDisconnect
    ) {
        jwtToSessionMap.remove(playerSession.getPlayer().getJwt());

        UUID roomId = sessionToRoomIdMap.get(playerSession);
        if (roomId == null) {
            return Optional.empty();
        }

        Room room = roomIdToRoomMap.get(roomId);
        if (room == null) {
            return Optional.empty();
        }

        synchronized (room) {
            GameState.State state = room.getActiveGameState().getState();
            boolean canStartGracePeriod = GameState.State.PLAYING.equals(state) || GameState.State.GIVING_CARDS.equals(state);

            if (!canStartGracePeriod) {
                sessionToRoomIdMap.remove(playerSession);
                unlinkPlayerSession(roomId, playerSession);
                return finalizePlayerRemoval(room, playerSession.getPlayer(), true, onPlayerLeaveOrDisconnect);
            }

            sessionToRoomIdMap.remove(playerSession);
            List<PlayerSession> playersInRoom = unlinkPlayerSession(roomId, playerSession);

            Player player = playerSession.getPlayer();
            ScheduledFuture<?> graceTask = scheduler.schedule(
                    () -> finalizeDisconnectedPlayer(room, player, onPlayerLeaveOrDisconnect),
                    GameConstants.RECONNECTION_GRACE_PERIOD_SECONDS,
                    TimeUnit.SECONDS
            );
            disconnectedPlayers.put(player, new DisconnectionTask(roomId, graceTask));

            return Optional.ofNullable(playersInRoom);
        }
    }

    /**
     * Reconnects a player within their grace period: cancels the pending removal and re-links the new
     * session to the room they were disconnected from.
     * @return the room the player was reconnected to, or empty if the grace period already expired/was
     * cancelled (e.g. another player's exit already finished the room) or the player wasn't disconnected.
     */
    public Optional<Room> reconnectPlayer(@NonNull PlayerSession newPlayerSession) {
        Player player = newPlayerSession.getPlayer();
        DisconnectionTask pending = disconnectedPlayers.get(player);
        if (pending == null) {
            return Optional.empty();
        }

        Room room = roomIdToRoomMap.get(pending.roomId());
        if (room == null) {
            disconnectedPlayers.remove(player);
            return Optional.empty();
        }

        synchronized (room) {
            DisconnectionTask stillPending = disconnectedPlayers.remove(player);
            if (stillPending == null) {
                return Optional.empty();
            }
            stillPending.graceTask().cancel(false);

            sessionToRoomIdMap.put(newPlayerSession, room.getId());
            roomIdToSessionMap.computeIfAbsent(room.getId(), k -> new ArrayList<>()).add(newPlayerSession);

            return Optional.of(room);
        }
    }

    private void finalizeDisconnectedPlayer(Room room, Player player, Consumer<Room> onPlayerLeaveOrDisconnect) {
        synchronized (room) {
            DisconnectionTask pending = disconnectedPlayers.get(player);
            if (pending == null || !roomIdToRoomMap.containsKey(pending.roomId())) {
                disconnectedPlayers.remove(player);
                return; // reconnected already, or room already finished/removed by another player's exit
            }
            finalizePlayerRemoval(room, player, true, onPlayerLeaveOrDisconnect);
        }
    }

    private List<PlayerSession> unlinkPlayerSession(UUID roomId, PlayerSession playerSession) {
        List<PlayerSession> playerSessions = roomIdToSessionMap.get(roomId);
        if (playerSessions != null) {
            playerSessions.remove(playerSession);
        }
        return playerSessions;
    }

    private void cancelOtherPendingDisconnections(UUID roomId, Player excludePlayer) {
        disconnectedPlayers.entrySet().removeIf(entry -> {
            if (!roomId.equals(entry.getValue().roomId()) || entry.getKey().equals(excludePlayer)) {
                return false;
            }
            entry.getValue().graceTask().cancel(false);
            return true;
        });
    }

    private Optional<List<PlayerSession>> finalizePlayerRemoval(
            Room room,
            Player player,
            boolean hasPlayerLostConnection,
            Consumer<Room> onPlayerLeaveOrDisconnect
    ) {
        disconnectedPlayers.remove(player);

        List<Player> players = room.getActiveGameState().getPlayers();
        List<Player> readyPlayers = room.getActiveGameState().getReadyPlayers();
        players.remove(player);
        readyPlayers.remove(player);
        room.setOwner(!players.isEmpty() ? players.getFirst() : room.getOwner());

        GameState.State state = room.getActiveGameState().getState();

        if (GameState.State.WAITING.equals(state) && !room.getPlayers().isEmpty()) {
            return Optional.ofNullable(roomIdToSessionMap.get(room.getId()));
        }

        boolean wasActiveGame = GameState.State.PLAYING.equals(state) || GameState.State.GIVING_CARDS.equals(state);

        if (wasActiveGame) {
            short penalty = hasPlayerLostConnection
                    ? GameConstants.SCORE_PENALTY_LOST_CONNECTION
                    : GameConstants.SCORE_PENALTY_LEAVE_ROOM;
            room.applyExitPenalty(player, penalty);
            cancelOtherPendingDisconnections(room.getId(), player);

            List<PlayerSession> preHookSnapshot = roomIdToSessionMap.get(room.getId());
            List<PlayerSession> toInform = preHookSnapshot != null ? new ArrayList<>(preHookSnapshot) : null;

            onPlayerLeaveOrDisconnect.accept(room);

            if (roomIdToRoomMap.containsKey(room.getId())) {
                List<PlayerSession> playersInRoom = removeRoom(room.getId());
                if (playersInRoom != null) {
                    for (PlayerSession otherPlayerSession : playersInRoom) {
                        sessionToRoomIdMap.remove(otherPlayerSession);
                    }
                }
            }

            return Optional.ofNullable(toInform);
        }

        List<PlayerSession> playersInRoom = removeRoom(room.getId());
        if (playersInRoom == null) {
            return Optional.empty();
        }
        for (PlayerSession otherPlayerSession : playersInRoom) {
            sessionToRoomIdMap.remove(otherPlayerSession);
        }
        return Optional.of(playersInRoom);
    }

    public synchronized boolean isPlayerSessionCurrentlyActive(@NonNull Player player) {
        return jwtToSessionMap.containsValue(new PlayerSession(null, player, null, null));
    }

    private void linkSessionWithRoom(@NonNull PlayerSession playerSession, @NonNull UUID roomId) {
        if (!jwtToSessionMap.containsKey(playerSession.getPlayer().getJwt())) {
            throw new RuntimeException("Player session with JWT " + playerSession.getPlayer().getJwt() + " not found");
        }
        sessionToRoomIdMap.putIfAbsent(playerSession, roomId);
        roomIdToSessionMap.putIfAbsent(roomId, new ArrayList<>(GameConstants.MAX_PLAYERS));
        roomIdToSessionMap.get(roomId).add(playerSession);
    }

    public RoomDto createRoom(@NonNull Room room, @NonNull PlayerSession playerSession) {
        if (isPlayerInRoom(playerSession)) {
            return RoomDto.invalid();
        }

        room.setId(UuidCreator.getTimeOrderedEpoch());
        room.initialGameState();

        roomIdToRoomMap.put(room.getId(), room);
        linkSessionWithRoom(playerSession, room.getId());

        return RoomDto.fromRoom(room);
    }

    public RoomDto copyRoom(@NonNull UUID roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            return null;
        }

        synchronized (room) {
            List<PlayerSession> playersInRoom = removeRoom(roomId);
            PlayerSession ownerPlayerSession = playersInRoom.stream()
                    .filter(ps -> ps.getPlayer().equals(room.getOwner()))
                    .findAny()
                    .orElseThrow();

            for (PlayerSession playerSession : playersInRoom) {
                sessionToRoomIdMap.remove(playerSession);
            }

            Room copyRoom = new Room(
                    room.getName(),
                    room.isPublic(),
                    LocalDateTime.now(),
                    room.getTotalScoreToWin(),
                    room.getOwner(),
                    room.getTurnDurationInSeconds(),
                    room.getGameStateFactory()
            );

            RoomDto copyRoomDto = createRoom(copyRoom, ownerPlayerSession);

            for (PlayerSession playerSession : playersInRoom) {
                if (!ownerPlayerSession.equals(playerSession)) {
                    joinRoom(copyRoom.getId(), playerSession);
                }
            }
            return copyRoomDto;
        }
    }

    public Optional<UpdatedRoomDetailsDto> updateRoom(@NonNull UUID roomId, @NonNull RoomDetailsDto roomDetailsDto, @NonNull Player player) {
        Room room = getRoom(roomId);
        if (room == null) {
            return Optional.empty();
        }

        synchronized (room) {
            if (!GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
                return Optional.empty();
            }

            if (!room.getOwner().equals(player)) {
                return Optional.empty();
            }

            if (roomDetailsDto.roomName() != null) {
                room.setName(roomDetailsDto.roomName());
            }
            if (roomDetailsDto.totalScoreToWin() != null) {
                room.setTotalScoreToWin(roomDetailsDto.totalScoreToWin());
            }
            if (roomDetailsDto.turnDurationInSeconds() != null) {
                room.setTurnDurationInSeconds(roomDetailsDto.turnDurationInSeconds());
            }
        }

        return Optional.of(new UpdatedRoomDetailsDto(
                room.getName(),
                room.getTotalScoreToWin(),
                room.getTurnDurationInSeconds()
        ));
    }

    public PlayerSession kickPlayer(@NonNull UUID roomId, long playerToKickId, @NonNull Player player) {
        Room room = getRoom(roomId);
        if (room == null) {
            return null;
        }

        synchronized (room) {
            if (!GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
                return null;
            }

            if (!room.getOwner().equals(player)) {
                return null;
            }

            PlayerSession playerSessionToBeKicked = getPlayersInRoom(roomId).stream()
                    .filter(ps -> ps.getPlayer().getId() == playerToKickId)
                    .findAny()
                    .orElseThrow();

            removeSession(playerSessionToBeKicked, false, r -> {});

            return playerSessionToBeKicked;
        }
    }

    public Room getRoom(@NonNull UUID roomId) {
        return roomIdToRoomMap.get(roomId);
    }

    public List<PlayerSession> removeRoom(@NonNull UUID roomId) {
        List<PlayerSession> playersInRoom = roomIdToSessionMap.remove(roomId);
        roomIdToRoomMap.remove(roomId);
        return playersInRoom;
    }

    public Room getPlayerRoom(@NonNull PlayerSession playerSession) {
        UUID roomId = sessionToRoomIdMap.get(playerSession);
        if (roomId == null) {
            return null;
        }
        return roomIdToRoomMap.get(roomId);
    }

    public boolean isPlayerInRoom(@NonNull PlayerSession playerSession) {
        return sessionToRoomIdMap.containsKey(playerSession);
    }

    public boolean addPlayerToRoom(@NonNull Room room, @NonNull PlayerSession playerSession) {
        synchronized (room) {
            if (isPlayerInRoom(playerSession)) {
                return false;
            }

            if (!GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
                return false;
            }

            return room.addPlayer(
                    playerSession.getPlayer(),
                    () -> linkSessionWithRoom(playerSession, room.getId())
            );
        }
    }

    public List<RoomDto> getAvailableRooms() {
        return roomIdToRoomMap.values()
                .stream()
                .filter(Room::isPublic)
                .map(RoomDto::fromRoom)
                .collect(Collectors.toList());
    }

    public synchronized List<Room> getAllRooms() {
        return roomIdToRoomMap.values().stream().toList();
    }

    public boolean joinRoom(@NonNull UUID roomId, @NonNull PlayerSession playerSession) {
        Room room = getRoom(roomId);
        if (room == null) {
            return false;
        }

        return addPlayerToRoom(room, playerSession);
    }

    public List<PlayerSession> getPlayersInRoom(UUID roomId) {
        return roomIdToSessionMap.get(roomId);
    }
}
