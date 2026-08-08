package org.murlan.live.protocol.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.util.MLObjectMapper;

import java.util.Optional;

@AllArgsConstructor
public class Generator {
    private static final Logger log = LogManager.getLogger(Generator.class);

    private final ProtocolConfig config;
    private final MLObjectMapper objectMapper;

    public String generateMessage(Resp resp) {
        if (resp == null) {
            return "";
        }

        String message = "";
        try {
            message = Optional.ofNullable(resp.toMessage(config, objectMapper)).orElse("");
        } catch (JsonProcessingException e) {
            log.error("Error writing resp DTOs into JSON, with message: {}", e.getMessage());
            log.error("No resp message will be sent. Aborting...");
        }

        return message;
    }
}
