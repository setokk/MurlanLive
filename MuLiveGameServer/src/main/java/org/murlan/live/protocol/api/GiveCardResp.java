package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@AllArgsConstructor
public final class GiveCardResp implements Resp {
    private ResponseStatus responseStatus;
    @Accessors(fluent = true) private boolean haveBothPlayersGivenCards;

    @Override
    public String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException {
        return String.join(config.getProtocol_delimiter(),
                ClientEvent.GIVE_CARD.id(),
                getResponseStatus().toString(),
                String.valueOf(haveBothPlayersGivenCards)
        );
    }
}
