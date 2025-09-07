package org.murlan.live.protocol.api;

import lombok.NoArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.ClientEvent;

@NoArgsConstructor
public class SurrenderResp implements Resp {
    @Override
    public String convertToString(ProtocolConfig config) {
        String statusCode = getResponseStatus().toString();
        String eventOrdinal = String.valueOf(ClientEvent.SURRENDER.ordinal());
        return String.join(config.getProtocol_delimiter(), statusCode, eventOrdinal);
    }
}
