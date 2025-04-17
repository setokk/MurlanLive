package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;

@AllArgsConstructor
public class Parser {
    private final ProtocolConfig config;

    public Object parse(ClientEvent clientEvent, String message) {
        String[] parts = message.split(config.getProtocol_delimiter());
        switch (clientEvent) {
            case ClientEvent.PLAY_HAND -> {

            }
            case ClientEvent.GAME_STATE -> {

            }
            case ClientEvent.PASS -> {

            }
            default -> {

            }
        }
        return null;
    }
}
