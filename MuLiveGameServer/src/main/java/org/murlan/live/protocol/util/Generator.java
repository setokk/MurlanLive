package org.murlan.live.protocol.util;

import lombok.AllArgsConstructor;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.api.Resp;

import java.util.Optional;

@AllArgsConstructor
public class Generator {
    private final ProtocolConfig config;

    public String generateMessage(Resp resp) {
        return Optional.ofNullable(resp.toMessage(config))
                .orElse("");
    }
}
