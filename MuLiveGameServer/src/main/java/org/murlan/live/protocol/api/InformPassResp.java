package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.ServerEvent;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@AllArgsConstructor
public class InformPassResp implements Resp {
    private ResponseStatus responseStatus;
    private long playerId;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(
                config.getProtocol_delimiter(),
                ServerEvent.INFORM_PASS.id(),
                getResponseStatus().toString(),
                String.valueOf(playerId)
        );
    }
}
