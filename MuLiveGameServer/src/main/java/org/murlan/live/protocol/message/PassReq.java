package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.config.ProtocolConfig;

@Setter
@Getter
@NoArgsConstructor
public class PassReq implements Req {
    private String JWT;

    public PassReq(String[] messageParts, ProtocolConfig config) {

    }
}
