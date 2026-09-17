package org.murlan.live.protocol.util;

import lombok.AllArgsConstructor;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.ClientEvent;
import org.murlan.live.protocol.api.Req;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Parser {
    public static final int MIN_NUM_VALUES = 1;
    private final ProtocolConfig config;

    public Req parse(String message) throws InvalidDataException {
        String[] messageParts = splitEscaped(message, config.getProtocol_delimiter());
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

    private String[] splitEscaped(String message, String delimiter) {
        char delimiterChar = delimiter.charAt(0);
        boolean encounteredBackslash = false;
        List<String> messageParts = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            if (c == '\\' && !encounteredBackslash) {
                encounteredBackslash = true;
                continue;
            } else if (c == delimiterChar && !encounteredBackslash) {
                messageParts.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }

            encounteredBackslash = false;
            sb.append(c);
        }

        if (encounteredBackslash) {
            sb.append('\\');
        }

        messageParts.add(sb.toString());

        return messageParts.toArray(new String[0]);
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
