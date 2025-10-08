package org.murlan.live.session;

import jakarta.websocket.Session;
import lombok.NonNull;
import org.murlan.live.protocol.dto.RoomDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomHandler {
    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> roomIdToRoomMap = new ConcurrentHashMap<>();

    public void addSession(@NonNull PlayerSession playerSession) {
        jwtToSessionMap.putIfAbsent(playerSession.getPlayerDto().getJwt(), playerSession);
    }

    public Optional<PlayerSession> getSession(@NonNull Session session) {
        return jwtToSessionMap.values().stream()
                .filter(s -> s.getSession().getId().equals(session.getId()))
                .findAny();
    }

    public Optional<String> removeSession(@NonNull PlayerSession playerSession) {
        jwtToSessionMap.remove(playerSession.getPlayerDto().getJwt());
        return Optional.ofNullable(sessionToRoomIdMap.remove(playerSession));
    }

    public boolean jwtSessionExists(@NonNull String jwt) {
        return jwtToSessionMap.containsKey(jwt);
    }

    public void linkSessionWithRoom(@NonNull PlayerSession playerSession, @NonNull String roomId) {
        if (!jwtToSessionMap.containsKey(playerSession.getPlayerDto().getJwt())) {
            throw new RuntimeException("Player session with JWT " + playerSession.getPlayerDto().getJwt() + " not found");
        }
        sessionToRoomIdMap.putIfAbsent(playerSession, roomId);
    }

    public boolean createRoom(@NonNull Room room, @NonNull PlayerSession playerSession) {
        boolean roomExists = roomIdToRoomMap.putIfAbsent(room.getId(), room) != null;
        if (roomExists) {
            return false;
        }
        linkSessionWithRoom(playerSession, room.getId());
        return roomIdToRoomMap.putIfAbsent(room.getId(), room) == null;
    }

    public Room getRoom(@NonNull String roomId) {
        return roomIdToRoomMap.get(roomId);
    }

    public Room removeRoom(@NonNull String roomId) {
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

    public synchronized boolean addPlayerToRoom(@NonNull String roomId, String passcode, @NonNull PlayerSession playerSession) {
        if (!roomExists(roomId)) {
            return false;
        }
        if (isPlayerInRoom(playerSession)) {
            return false;
        }
        Room room = getRoom(roomId);
        if (!GameState.State.WAITING.equals(room.getGameState().getState())) {
            return false;
        }
        if (!room.isPublic() && !room.getPasscode().equals(passcode)) {
            return false;
        }

        return room.addPlayer(playerSession.getPlayerDto());
    }

    public List<RoomDto> getAvailableRooms() {
        return roomIdToRoomMap.values()
                .stream()
                .filter(Room::isPublic)
                .map(room -> new RoomDto(room.getId(), room.getName(), room.getNumPlayers()))
                .collect(Collectors.toList());
    }

    public synchronized boolean joinRoom(@NonNull String roomId, @NonNull String passcode, @NonNull PlayerSession playerSession) {
        boolean isJoinSuccessful = addPlayerToRoom(roomId, passcode, playerSession);
        if (!isJoinSuccessful) {
            return false;
        }
        linkSessionWithRoom(playerSession, roomId);
        return true;
    }
}
