package org.murlan.live.endpoint.session;

import jakarta.websocket.Session;
import lombok.NonNull;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.dto.RoomDetailsDto;
import org.murlan.live.protocol.dto.RoomDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RoomHandler {
    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<PlayerSession>> roomIdToSessionMap = new ConcurrentHashMap<>(); // for efficient retrieval of players in a room
    private final ConcurrentHashMap<String, Room> roomIdToRoomMap = new ConcurrentHashMap<>();

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

        String roomId = sessionToRoomIdMap.remove(playerSession);
        if (roomId == null) {
            return Optional.empty();
        }

        Room room = roomIdToRoomMap.get(roomId);
        if (room == null) {
            return Optional.empty();
        }

        synchronized (room) {
            List<PlayerSession> playerSessions = roomIdToSessionMap.get(roomId);
            if (playerSessions != null) {
                playerSessions.remove(playerSession);
            }

            List<Player> players = room.getActiveGameState().getPlayers();
            players.remove(playerSession.getPlayer());
            room.setOwner(!players.isEmpty() ? players.getFirst() : room.getOwner());

            // if game has not started yet (initial state where not all players have joined)
            // do NOT remove room.
            // remove room and player sessions ONLY in the case of active game
            if (GameState.State.WAITING.equals(room.getActiveGameState().getState()) && !room.getPlayers().isEmpty()) {
                return Optional.ofNullable(playerSessions);
            }

            room.getActiveGameState().handlePlayerNotInRoom(playerSession.getPlayer(), hasPlayerLostConnection);
            onPlayerLeaveOrDisconnect.accept(room);

            List<PlayerSession> playersInRoom = removeRoom(roomId);
            if (playersInRoom == null) {
                return Optional.empty();
            }

            for (PlayerSession otherPlayerSession : playersInRoom) {
                sessionToRoomIdMap.remove(otherPlayerSession);
            }
            return Optional.of(playersInRoom);
        }
    }

    public synchronized boolean isPlayerSessionCurrentlyActive(@NonNull Player player) {
        return jwtToSessionMap.containsValue(new PlayerSession(null, player));
    }

    private void linkSessionWithRoom(@NonNull PlayerSession playerSession, @NonNull String roomId) {
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

        room.setId(UUID.randomUUID().toString());
        room.initialGameState();

        roomIdToRoomMap.put(room.getId(), room);
        linkSessionWithRoom(playerSession, room.getId());

        return new RoomDto(room.getId(), room.getName(), room.getPlayers(), room.getTotalScoreToWin(), room.getTurnDurationInSeconds());
    }

    public void copyRoom(@NonNull String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            return;
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
            createRoom(copyRoom, ownerPlayerSession);

            for (PlayerSession playerSession : playersInRoom) {
                if (!ownerPlayerSession.equals(playerSession)) {
                    joinRoom(copyRoom.getId(), playerSession);
                }
            }
        }
    }

    public boolean updateRoom(@NonNull String roomId, @NonNull RoomDetailsDto roomDetailsDto, @NonNull Player player) {
        Room room = getRoom(roomId);
        if (room == null) {
            return false;
        }

        synchronized (room) {
            if (!GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
                return false;
            }

            if (!room.getOwner().equals(player)) {
                return false;
            }

            if (room.getActiveGameState().getReadyPlayers().contains(player)) {
                return false;
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

        return true;
    }

    public PlayerSession kickPlayer(@NonNull String roomId, long playerToKickId, @NonNull Player player) {
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

    public Room getRoom(@NonNull String roomId) {
        return roomIdToRoomMap.get(roomId);
    }

    public List<PlayerSession> removeRoom(@NonNull String roomId) {
        List<PlayerSession> playersInRoom = roomIdToSessionMap.remove(roomId);
        roomIdToRoomMap.remove(roomId);
        return playersInRoom;
    }

    public Room getPlayerRoom(@NonNull PlayerSession playerSession) {
        String roomId = sessionToRoomIdMap.get(playerSession);
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
                .map(room -> new RoomDto(room.getId(), room.getName(), room.getPlayers(), room.getTotalScoreToWin(), room.getTurnDurationInSeconds()))
                .collect(Collectors.toList());
    }

    public synchronized List<Room> getAllRooms() {
        return roomIdToRoomMap.values().stream().toList();
    }

    public boolean joinRoom(@NonNull String roomId, @NonNull PlayerSession playerSession) {
        Room room = getRoom(roomId);
        if (room == null) {
            return false;
        }

        return addPlayerToRoom(room, playerSession);
    }

    public List<PlayerSession> getPlayersInRoom(String roomId) {
        return roomIdToSessionMap.get(roomId);
    }
}
