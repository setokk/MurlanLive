package org.murlan.live.endpoint;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import org.murlan.live.protocol.api.error.GenericErrorResp;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class EndpointHelper {
    private final Parser parser;
    private final Generator generator;

    public void sendErrorMessage(Session session, GenericErrorResp resp) throws IOException {
        String errorMessage = generator.generateMessage(resp);
        session.getBasicRemote().sendText(errorMessage);
    }

    public Optional<String> getAndCheckQueryParam(String key, String queryParamString) {
        if (queryParamString == null || queryParamString.isEmpty()) {
            return Optional.empty();
        }
        Map<String, String> queryParams = parser.parseQueryParams(queryParamString);
        return Optional.ofNullable(queryParams.get(key));
    }
}
