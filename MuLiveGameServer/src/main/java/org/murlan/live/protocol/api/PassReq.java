package org.murlan.live.protocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.config.ProtocolConfig;

@Setter
@Getter
@NoArgsConstructor
public final class PassReq implements Req {
    private String JWT;

    public PassReq(String[] messageParts, ProtocolConfig config) {

    }
}
