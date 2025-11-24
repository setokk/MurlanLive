package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

@Setter
@Getter
@AllArgsConstructor
public final class PassResp implements Resp {
    private ResponseStatus responseStatus;

    @Override
    public String toMessage(ProtocolConfig config) {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.PASS.id(),
                getResponseStatus().toString()
        );
    }
}
