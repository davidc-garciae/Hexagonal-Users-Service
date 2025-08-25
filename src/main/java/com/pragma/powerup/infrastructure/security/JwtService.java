package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IJwtProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());
    claims.put("role", user.getRole() != null ? user.getRole().name() : null);
    claims.put("email", user.getEmail());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getEmail())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .signWith(getKey(), SignatureAlgorithm.HS256)
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
