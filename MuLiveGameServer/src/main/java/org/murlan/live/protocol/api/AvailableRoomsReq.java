package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class AvailableRoomsReq implements Req {
    public AvailableRoomsReq(String[] messageParts, ProtocolConfig config) {

    }
}
