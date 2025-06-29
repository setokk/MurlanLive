package org.murlan.um.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.murlan.um.model.dto.PlayerDto;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private final static Key secretKey;
    static {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(new SecureRandom());
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateJWT(PlayerDto player) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put("id", player.getId());
        claims.put("username", player.getUsername());
        claims.put("creationDate", player.getCreationDate().toString());

        Instant currentInstant = Instant.now();
        return Jwts.builder()
                .issuer("mulive-um")
                .subject(String.valueOf(player.getId()))
                .claims(claims)
                .issuedAt(Date.from(currentInstant))
                .signWith(secretKey)
                .compact();
    }

    public static Claims extractAllClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
