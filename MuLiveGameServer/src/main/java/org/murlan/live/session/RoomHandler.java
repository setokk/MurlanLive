package org.murlan.live.session;

import jakarta.websocket.Session;
import lombok.Getter;
import org.murlan.live.session.player.PlayerDto;
import org.murlan.live.session.player.PlayerSession;

import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RoomHandler {
    private final ConcurrentHashMap<String, PlayerSession> jwtToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerSession, String> sessionToRoomIdMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> roomIdToRoomMap = new ConcurrentHashMap<>();

    public void addSession(String roomId, PlayerSession session) {
        jwtToSessionMap.putIfAbsent(session.getPlayerDto().getJwt(), session);
        sessionToRoomIdMap.putIfAbsent(session, roomId);
    }

    public void removeSession(String jwt) {
        PlayerSession playerSession = jwtToSessionMap.remove(jwt);
        sessionToRoomIdMap.remove(playerSession);
    }

    public PlayerSession getPlayerSession(String jwt) {
        return jwtToSessionMap.get(jwt);
    }

    public void addRoom(String roomId, Room room) {
        roomIdToRoomMap.putIfAbsent(roomId, room);
    }

    public Room removeRoom(String roomId) {
        return roomIdToRoomMap.remove(roomId);
    }

    public boolean roomExists(String roomId) {
        return roomIdToRoomMap.containsKey(roomId);
    }

    public synchronized boolean addPlayerToRoom(String roomId, PlayerDto playerDto) {

    }
}
