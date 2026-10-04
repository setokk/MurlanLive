package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.config.ProtocolConfig;

import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
public final class UnMuteResp implements Resp {
    private ResponseStatus responseStatus;
    private Set<Long> unmutedPlayerIds;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(
                config.getProtocol_delimiter(),
                ClientEvent.UNMUTE.id(),
                getResponseStatus().toString(),
                unmutedPlayerIds.stream().map(String::valueOf).collect(Collectors.joining(config.getProtocol_list_delimiter()))
        );
    }
}
