package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ResponseStatus;

@Setter
@Getter
@AllArgsConstructor
public class JoinRoomResp implements Resp {
    private ResponseStatus responseStatus;

    @Override
    public String convertToString(ProtocolConfig config) {
        return "";
    }
}
