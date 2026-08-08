package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.RoomDto;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public final class AvailableRoomsResp implements Resp {
    private ResponseStatus responseStatus;
    private List<RoomDto> availableRoomsDto;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.AVAILABLE_ROOMS.id(),
                getResponseStatus().toString(),
                objectMapper.writeValueAsString(availableRoomsDto)
        );
    }
}
