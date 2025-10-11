package org.murlan.live.protocol.util;

import lombok.AllArgsConstructor;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.api.Req;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Parser {
    public static final int MIN_NUM_VALUES = 1;
    private final ProtocolConfig config;

    public Req parse(String message) throws InvalidDataException {
        String[] messageParts = message.split(Pattern.quote(config.getProtocol_delimiter()));
        if (messageParts.length < MIN_NUM_VALUES) { // All messages should start with: ClientEvent ID
            throw new InvalidDataException();
        }
        try {
            ClientEvent clientEvent = ClientEvent.fromId(messageParts[0]);
            return clientEvent.getReqFactory().newReq(messageParts, config);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException();
        }
    }

    public Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] keyValues = queryString.split("&");
        for (String keyValue : keyValues) {
            String[] keyAndValue = keyValue.split("=");
            if (keyAndValue.length != 2) {
                break;
            }
            queryParams.put(keyAndValue[0], keyAndValue[1]);
        }
        return queryParams;
    }
}
