package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IJwtProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements IJwtProviderPort {

    @Value("${spring.security.jwt.secret:change-me-change-me-change-me-change-me}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    @Override
    public String generateToken(UserModel user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("role", user.getRole() != null ? user.getRole().name() : null)
                .claim("email", user.getEmail())
                .claim("sub", user.getEmail())
                .claim("iat", new Date(now))
                .claim("exp", new Date(now + jwtExpiration))
                .signWith(getKey())
                .compact();
    }

    @Override
    public long getExpirationMs() {
        return jwtExpiration;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
