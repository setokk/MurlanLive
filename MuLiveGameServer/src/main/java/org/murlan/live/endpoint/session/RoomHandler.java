package org.murlan.live.endpoint.session;

import jakarta.websocket.Session;
import lombok.NonNull;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.RoomDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

    public Optional<String> removeSession(@NonNull PlayerSession playerSession) {
        jwtToSessionMap.remove(playerSession.getPlayer().getJwt());
        String roomId = sessionToRoomIdMap.remove(playerSession);
        if (roomId == null) {
            return Optional.empty();
        }
        roomIdToSessionMap.get(roomId).remove(playerSession);
        return Optional.of(roomId);
    }

    public boolean jwtSessionExists(@NonNull String jwt) {
        return jwtToSessionMap.containsKey(jwt);
    }

    public void linkSessionWithRoom(@NonNull PlayerSession playerSession, @NonNull String roomId) {
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
        room.newGameState();

        roomIdToRoomMap.put(room.getId(), room);
        linkSessionWithRoom(playerSession, room.getId());

        return new RoomDto(room.getId(), room.getName(), room.getPlayers());
    }

    public Room getRoom(@NonNull String roomId) {
        return roomIdToRoomMap.get(roomId);
    }

    public Room removeRoom(@NonNull String roomId) {
        roomIdToSessionMap.remove(roomId);
        return roomIdToRoomMap.remove(roomId);
    }

    public Room getPlayerRoom(@NonNull PlayerSession playerSession) {
        String roomId = sessionToRoomIdMap.get(playerSession);
        if (roomId == null) {
            return null;
        }
        return roomIdToRoomMap.get(roomId);
    }

    public boolean roomExists(@NonNull String roomId) {
        return roomIdToRoomMap.containsKey(roomId);
    }

    public boolean isPlayerInRoom(@NonNull PlayerSession playerSession) {
        return sessionToRoomIdMap.containsKey(playerSession);
    }

    public synchronized boolean addPlayerToRoom(@NonNull String roomId, @NonNull PlayerSession playerSession) {
        if (!roomExists(roomId)) {
            return false;
        }
        if (isPlayerInRoom(playerSession)) {
            return false;
        }
        Room room = getRoom(roomId);
        if (!GameState.State.WAITING.equals(room.getActiveGameState().getState())) {
            return false;
        }

        return room.addPlayer(playerSession.getPlayer());
    }

    public List<RoomDto> getAvailableRooms() {
        return roomIdToRoomMap.values()
                .stream()
                .filter(Room::isPublic)
                .map(room -> new RoomDto(room.getId(), room.getName(), room.getPlayers()))
                .collect(Collectors.toList());
    }

    public List<Room> getAllRooms() {
        return roomIdToRoomMap.values().stream().toList();
    }

    public synchronized boolean joinRoom(@NonNull String roomId, @NonNull PlayerSession playerSession) {
        boolean isJoinSuccessful = addPlayerToRoom(roomId, playerSession);
        if (!isJoinSuccessful) {
            return false;
        }
        linkSessionWithRoom(playerSession, roomId);
        return true;
    }

    public synchronized List<PlayerSession> getPlayersInRoom(String roomId) {
        return roomIdToSessionMap.get(roomId);
    }
}
