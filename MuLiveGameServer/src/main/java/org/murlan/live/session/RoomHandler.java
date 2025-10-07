package org.murlan.live.session;

import lombok.NonNull;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.protocol.dto.RoomDto;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomHandler {
    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToJwtMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> roomIdToRoomMap = new ConcurrentHashMap<>();

    public void addSession(@NonNull PlayerSession playerSession) {
        jwtToSessionMap.putIfAbsent(playerSession.getPlayerDto().getJwt(), playerSession);
        sessionToJwtMap.putIfAbsent(playerSession, playerSession.getPlayerDto().getJwt());
    }

    public PlayerSession getSession(@NonNull String jwt) {
        return jwtToSessionMap.get(jwt);
    }

    public String removeSession(@NonNull PlayerSession playerSession) {
        String jwt = sessionToJwtMap.remove(playerSession);
        jwtToSessionMap.remove(jwt);
        return sessionToRoomIdMap.remove(playerSession);
    }

    public void linkSessionWithRoom(@NonNull PlayerSession playerSession, @NonNull String roomId) {
        if (!jwtToSessionMap.containsKey(playerSession.getPlayerDto().getJwt()) || !sessionToJwtMap.containsKey(playerSession)) {
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
        return roomIdToRoomMap.get(sessionToRoomIdMap.get(playerSession));
    }

    public boolean roomExists(@NonNull String roomId) {
        return roomIdToRoomMap.containsKey(roomId);
    }

    public synchronized boolean addPlayerToRoom(@NonNull String roomId, String passcode, @NonNull PlayerDto playerDto) {
        if (!roomExists(roomId)) {
            return false;
        }
        Room room = getRoom(roomId);
        if (!GameState.State.WAITING.equals(room.getGameState().getState())) {
            return false;
        }
        if (!room.isPublic() && !room.getPasscode().equals(passcode)) {
            return false;
        }

        return room.addPlayer(playerDto);
    }

    public List<RoomDto> getAvailableRooms() {
        return roomIdToRoomMap.values()
                .stream()
                .filter(Room::isPublic)
                .map(room -> new RoomDto(room.getId(), room.getName(), room.getNumPlayers()))
                .collect(Collectors.toList());
    }

    public synchronized boolean joinRoom(@NonNull String roomId, @NonNull String passcode, @NonNull PlayerSession playerSession) {
        boolean isJoinSuccessful = addPlayerToRoom(roomId, passcode, playerSession.getPlayerDto());
        if (!isJoinSuccessful) {
            removeSession(playerSession);
            return false;
        }
        linkSessionWithRoom(playerSession, roomId);
        return true;
    }
}
