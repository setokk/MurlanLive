package org.murlan.live.protocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
@Setter
@NoArgsConstructor
public final class SurrenderReq implements Req {
    private String JWT;

    public SurrenderReq(String[] messageParts, ProtocolConfig config) {

    }
}
