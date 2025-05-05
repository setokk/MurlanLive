package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.message.GameStateReq;
import org.murlan.live.protocol.message.PassReq;
import org.murlan.live.protocol.message.PlayHandReq;
import org.murlan.live.protocol.message.Req;
import org.murlan.live.protocol.message.SurrenderReq;

@AllArgsConstructor
public class Parser {
    private final ProtocolConfig config;

    public Req parse(ClientEvent clientEvent, String message) {
        String[] parts = message.split(config.getProtocol_delimiter());
        Req request = switch (clientEvent) {
            case ClientEvent.PLAY_HAND -> {
                request = new PlayHandReq();
                yield request;
            }
            case ClientEvent.GAME_STATE -> {
                request = new GameStateReq();
                yield request;
            }
            case ClientEvent.PASS -> {
                request = new PassReq();
                yield request;
            }
            case ClientEvent.SURRENDER -> {
                request = new SurrenderReq();
                yield request;
            }
        };
        request.setJWT(parts[0]);
        return request;
    }
}
