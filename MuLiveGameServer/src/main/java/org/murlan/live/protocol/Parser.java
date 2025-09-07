package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.api.Req;

import java.util.HashMap;
import java.util.Map;

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
            Req request = clientEvent.getReqFactory().newReq(messageParts, config);
            request.setJWT(messageParts[1]);
            return request;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] keyValues = queryString.split("&"); // queryString is basically this pattern: roomId=ROOM1ID000&jwt=2mkkfds0d89fsdfj
        for (String keyValue : keyValues) {
            // keyValue is basically this pattern: roomId=ROOM1ID000 (key=value)
            String[] keyAndValue = keyValue.split("=");
            if (keyAndValue.length != 2) {
                break;
            }
            queryParams.put(keyAndValue[0], keyAndValue[1]);
        }
        return queryParams;
    }
}
