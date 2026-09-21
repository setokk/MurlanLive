package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.ServerEvent;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.RoomDetailsDto;

@Setter
@Getter
@AllArgsConstructor
public final class InformUpdateRoomDetailsResp implements Resp {
    private ResponseStatus responseStatus;
    private RoomDetailsDto roomDetailsDto;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(config.getProtocol_delimiter(),
                ServerEvent.INFORM_UPDATE_ROOM_DETAILS.id(),
                getResponseStatus().toString(),
                objectMapper.writeValueAsString(roomDetailsDto)
        );
    }
}
