package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.ResponseStatus;

@Setter
@Getter
@AllArgsConstructor
public class AvailableRoomsResp implements Resp {
    private ResponseStatus responseStatus;
    private String availableRoomsJson;

    @Override
    public String toMessage(ProtocolConfig config) {
        String eventId = ClientEvent.AVAILABLE_ROOMS.id();
        String statusCode = getResponseStatus().toString();
        return String.join(config.getProtocol_delimiter(), eventId, statusCode, availableRoomsJson);
    }
}
