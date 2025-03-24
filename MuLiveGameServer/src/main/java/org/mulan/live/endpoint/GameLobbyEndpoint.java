package org.mulan.live.endpoint;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mulan.live.session.MuLiveSession;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/game-lobby/{lobbyId}")
public class GameLobbyEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(GameLobbyEndpoint.class);
    private final CopyOnWriteArraySet<MuLiveSession> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("lobbyId") String lobbyId) throws IOException {
        sessions.add(new MuLiveSession(lobbyId, session, null));
        LOGGER.info("New connection with sessionId: {}, for lobbyId: {}", session.getId(), lobbyId);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(MuLiveSession.fromSession(session));
        session.close();
        LOGGER.info("Connection with sessionId: {} closed", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("Error", throwable);
    }
}
