package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

@Setter
@Getter
@AllArgsConstructor
public class GameStateResp implements Resp {
    private ResponseStatus responseStatus;
    private String gameStateJson;

    @Override
    public String toMessage(ProtocolConfig config) {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.GAME_STATE.id(),
                getResponseStatus().toString(),
                gameStateJson
        );
    }
}
