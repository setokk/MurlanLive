package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@AllArgsConstructor
public class JoinRoomResp implements Resp {
    private ResponseStatus responseStatus;

    @Override
    public String toMessage(ProtocolConfig config) {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.JOIN_ROOM.id(),
                responseStatus.toString()
        );
    }
}
