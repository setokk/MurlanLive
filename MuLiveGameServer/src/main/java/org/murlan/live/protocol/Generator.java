package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.api.Resp;

import java.util.Optional;

@AllArgsConstructor
public class Generator {
    private final ProtocolConfig config;

    public String generateMessage(Resp resp) {
        return Optional.ofNullable(resp.convertToString(config))
                .orElse("");
    }
}
