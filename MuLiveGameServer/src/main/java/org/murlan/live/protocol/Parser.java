package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.message.Req;

@AllArgsConstructor
public class Parser {
    private final ProtocolConfig config;

    public Req parse(String message) {
        String[] messageParts = message.split(config.getProtocol_delimiter());
        if (messageParts.length < 2) { // All messages should have at least 2 values: ClientEvent ID (ordinal) and JWT
            return null;
        }
        try {
            ClientEvent clientEvent = ClientEvent.fromOrdinal(Integer.parseInt(messageParts[0]));
            Req request = clientEvent.getRequestFactory().newReq(messageParts, config);
            request.setJWT(messageParts[1]);
            return request;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
