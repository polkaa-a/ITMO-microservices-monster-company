package com.example.gatewayservice.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class JwtUtil {
    private final String jwtSecret;

    public JwtUtil(String jwtSecret) {
        this.jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.error(ex.getMessage());
        }
        return false;
    }

}
