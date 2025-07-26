package org.murlan.live.session;

import lombok.Getter;
import lombok.NonNull;
import org.murlan.live.session.player.PlayerDto;
import org.murlan.live.session.player.PlayerSession;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomHandler {
    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToJwtMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> roomIdToRoomMap = new ConcurrentHashMap<>();

    public void addSession(@NonNull String roomId, @NonNull PlayerSession session) {
        jwtToSessionMap.putIfAbsent(session.getPlayerDto().getJwt(), session);
        sessionToJwtMap.putIfAbsent(session, session.getPlayerDto().getJwt());
        sessionToRoomIdMap.putIfAbsent(session, roomId);
    }

    public void removeSessionByJwt(@NonNull String jwt) {
        PlayerSession playerSession = jwtToSessionMap.remove(jwt);
        sessionToJwtMap.remove(playerSession);
        sessionToRoomIdMap.remove(playerSession);
    }

    public String removeSession(@NonNull PlayerSession playerSession) {
        String jwt = sessionToJwtMap.remove(playerSession);
        jwtToSessionMap.remove(jwt);
        return sessionToRoomIdMap.remove(playerSession);
    }

    public PlayerSession getPlayerSession(@NonNull String jwt) {
        return jwtToSessionMap.get(jwt);
    }

    public void createRoom(@NonNull String roomId, @NonNull Room room) {
        roomIdToRoomMap.putIfAbsent(roomId, room);
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

        final Room room = getRoom(roomId);
        if (!room.getGameState().getState().equals(GameState.State.WAITING)) {
            return false;
        }
        if (!room.isPublic() && !room.getPasscode().equals(passcode)) {
            return false;
        }

        return room.addPlayer(playerDto);
    }
}
