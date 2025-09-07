package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.api.Resp;

@AllArgsConstructor
public class Generator {
    private final ProtocolConfig config;

    public String generateMessage(Resp resp) {
        return resp.convertToString(config);
    }
}
