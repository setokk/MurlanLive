package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class ReadyReq implements Req {
    public ReadyReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
    }
}
