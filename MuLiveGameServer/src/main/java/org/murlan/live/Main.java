package org.murlan.live;

import jakarta.websocket.DeploymentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.server.Server;
import org.murlan.live.config.ConfigProvider;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.endpoint.GameLobbyEndpoint;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        ProtocolConfig protocolConfig = ConfigProvider.getProtocolConfig();

        Server gameServer = new Server(protocolConfig.getProtocol_host(), protocolConfig.getProtocol_port(), "/", null, GameLobbyEndpoint.class);
        try {
            gameServer.start();
            System.out.printf("WebSocket server started at ws://%s:%S/game-lobby%n", protocolConfig.getProtocol_host(), protocolConfig.getProtocol_port());
            Thread.currentThread().join();
        } catch (DeploymentException|InterruptedException e) {
            LOGGER.error(e);
        } finally {
            gameServer.stop();
        }
    }
}
