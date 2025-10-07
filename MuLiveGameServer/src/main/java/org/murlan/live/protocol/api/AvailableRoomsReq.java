package org.murlan.live.protocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@NoArgsConstructor
public final class AvailableRoomsReq implements Req {
    private String JWT;

    public AvailableRoomsReq(String[] messageParts, ProtocolConfig config) {

    }
}
