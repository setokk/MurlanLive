package org.murlan.live.protocol.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.config.ProtocolConfig;

public interface Resp {
    ResponseStatus getResponseStatus();
    String toMessage(ProtocolConfig config, ObjectMapper objectMapper) throws JsonProcessingException;
}
