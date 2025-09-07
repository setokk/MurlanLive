package org.murlan.live.protocol.api;

import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

public interface Resp {
    default ResponseStatus getResponseStatus() {
        return ResponseStatus.OK;
    }
    String convertToString(ProtocolConfig config);
}
