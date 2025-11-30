package org.murlan.live.protocol.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

public class JwtUtils {
    private static final Logger LOGGER = LogManager.getLogger(JwtUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static Player decodeJWT(String jwt) throws JsonProcessingException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            LOGGER.error("Invalid JWT format: {}", jwt);
            return new Player(); // Return empty PlayerDto
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        Map<String, Object> map = MAPPER.readValue(payload, Map.class);
        return new Player(
                Long.parseLong(map.get("id").toString()),
                map.get("username").toString(),
                LocalDateTime.parse(map.get("creationDate").toString()),
                jwt
        );
    }
}
