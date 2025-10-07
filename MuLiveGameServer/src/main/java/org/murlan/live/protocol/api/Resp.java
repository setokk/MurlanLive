package org.murlan.live.protocol.api;

import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

public interface Resp {
    ResponseStatus getResponseStatus();
    String toMessage(ProtocolConfig config);
}
