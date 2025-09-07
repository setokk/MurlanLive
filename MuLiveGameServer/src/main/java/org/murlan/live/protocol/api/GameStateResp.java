package org.murlan.live.protocol.api;

import lombok.NoArgsConstructor;
import org.murlan.live.config.ProtocolConfig;

@NoArgsConstructor
public class GameStateResp implements Resp {
    @Override
    public String convertToString(ProtocolConfig config) {
        return "";
    }
}
