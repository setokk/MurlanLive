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

@Setter
@Getter
@AllArgsConstructor
public final class CreateRoomResp implements Resp {
    private ResponseStatus responseStatus;
    private RoomDto roomDto;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.CREATE_ROOM.id(),
                getResponseStatus().toString(),
                objectMapper.writeValueAsString(roomDto)
        );
    }
}
