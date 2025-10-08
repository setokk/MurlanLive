package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class PassReq implements Req {
    public PassReq(String[] messageParts, ProtocolConfig config) {

    }
}
