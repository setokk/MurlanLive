package org.murlan.live.protocol.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.protocol.dto.Player;

import java.math.BigDecimal;
import java.math.BigInteger;
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
                map.get("email").toString(),
                Long.parseLong(map.get("profileIconId").toString()),
                parseNumber(map.get("rankOrdinal"), Short.class),
                parseNumber(map.get("displayRating"), Integer.class),
                jwt
        );
    }

    private static <T extends Number> T parseNumber(Object o, Class<T> type) {
        if (o == null) {
            return null;
        }

        String s = o.toString();
        try {
            Number result = switch (type.getSimpleName()) {
                case "Short" -> Short.valueOf(s);
                case "Integer" -> Integer.valueOf(s);
                case "Long" -> Long.valueOf(s);
                case "Float" -> Float.valueOf(s);
                case "Double" -> Double.valueOf(s);
                case "BigInteger" -> new BigInteger(s);
                case "BigDecimal" -> new BigDecimal(s);
                default -> throw new IllegalArgumentException(
                        "Unsupported type: " + type.getName()
                );
            };

            return type.cast(result);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
