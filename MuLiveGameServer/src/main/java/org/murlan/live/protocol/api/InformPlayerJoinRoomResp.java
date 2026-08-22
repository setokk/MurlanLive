package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.ServerEvent;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;

@Setter
@Getter
@AllArgsConstructor
public class InformPlayerJoinRoomResp implements Resp {
    private ResponseStatus responseStatus;
    private Player player;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(config.getProtocol_delimiter(),
                ServerEvent.INFORM_PLAYER_JOIN_ROOM.id(),
                getResponseStatus().toString(),
                objectMapper.writeValueAsString(player)
        );
    }
}
