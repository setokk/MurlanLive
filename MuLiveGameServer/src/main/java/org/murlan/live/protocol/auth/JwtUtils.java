package org.murlan.live.protocol.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.murlan.live.protocol.dto.PlayerDto;

import java.util.Base64;
import java.util.Map;

public class JwtUtils {
    public static PlayerDto decodeJWT(String jwt) throws JsonProcessingException {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            System.out.println("Invalid JWT format.");
            return new PlayerDto(); // Return empty PlayerDto
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(payload, Map.class);
        return new PlayerDto(
                Long.parseLong(map.get("id").toString()),
                map.get("username").toString(),
                map.get("creationDate").toString(),
                jwt
        );
    }
}
