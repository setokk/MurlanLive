package org.murlan.live;

import jakarta.websocket.DeploymentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.server.Server;
import org.murlan.live.protocol.config.ConfigProvider;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.endpoint.GameLobbyEndpoint;

public class WebSocketApplication {
    private static final Logger log = LogManager.getLogger(WebSocketApplication.class);

    public static void main(String[] args) {
        ProtocolConfig protocolConfig = ConfigProvider.getProtocolConfig();

        Server gameServer = new Server(protocolConfig.getProtocol_host(), protocolConfig.getProtocol_port(), "/", null, GameLobbyEndpoint.class);
        try {
            log.info("Starting MurlanLive game server with the following configuration:");
            log.info("* Protocol Version -> {} V{}", protocolConfig.getProtocol_name(), protocolConfig.getProtocol_version());
            log.info("* UM Host -> {}", protocolConfig.getProtocol_um_server_host());

            gameServer.start();
            log.info("WebSocket server started at ws://{}:{}/game-lobby", protocolConfig.getProtocol_host(), protocolConfig.getProtocol_port());

            Thread.currentThread().join();
        } catch (DeploymentException|InterruptedException e) {
            log.error(e);
        } finally {
            gameServer.stop();
        }
    }
}
