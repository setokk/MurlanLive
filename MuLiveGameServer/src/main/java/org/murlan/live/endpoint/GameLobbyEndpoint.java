package org.murlan.live.endpoint;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.session.Room;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/game-lobby")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);
    private final ConcurrentHashMap<String, String> roomsBySessionMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        LOGGER.info("New connection with sessionId: {}", session.getId());
        roomsBySessionMap.put(session.getId(), roomId);
        if (rooms.contains(Room.fromId(roomId))) {

        } else {
            rooms.put(roomId, Room.fromId(roomId));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String roomId = roomsBySessionMap.remove(session.getId());
        rooms.remove(roomId);
        session.close();
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error", throwable);
    }
}
