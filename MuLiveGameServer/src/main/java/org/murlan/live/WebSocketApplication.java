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
            log.info("""
                    \n___  ___      _     _                                     \s
                    |  \\/  |     | |   (_)                                    \s
                    | .  . |_   _| |    ___   _____                           \s
                    | |\\/| | | | | |   | \\ \\ / / _ \\                          \s
                    | |  | | |_| | |___| |\\ V /  __/                          \s
                    \\_|  |_/\\__,_\\_____/_| \\_/ \\___|                          \s
                                                                              \s
                                                                              \s
                     _____                      _____                         \s
                    |  __ \\                    /  ___|                        \s
                    | |  \\/ __ _ _ __ ___   ___\\ `--.  ___ _ ____   _____ _ __\s
                    | | __ / _` | '_ ` _ \\ / _ \\`--. \\/ _ \\ '__\\ \\ / / _ \\ '__|
                    | |_\\ \\ (_| | | | | | |  __/\\__/ /  __/ |   \\ V /  __/ |  \s
                     \\____/\\__,_|_| |_| |_|\\___\\____/ \\___|_|    \\_/ \\___|_|  \s
                                                                              \s
                                                                              \s
                    An open-source game based on Murlan, a playing cards game that first originated in communist Albania from Chinese advisors/migrants.
                    """);
            log.info("Starting MurlanLive game server with the following configuration:");
            log.info("* Protocol Version -> {} V{}", protocolConfig.getProtocol_name(), protocolConfig.getProtocol_version());
            log.info("* UM Host -> {}\n", protocolConfig.getProtocol_um_server_host());

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
