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

  @Value("${jwt.secret:change-me}")
  private String secretKey;

  @Value("${jwt.expiration:86400000}")
  private long jwtExpiration;

  @Override
  public String generateToken(UserModel user) {
    long nowSeconds = System.currentTimeMillis() / 1000; // JWT usa segundos, no milisegundos
    long expSeconds = nowSeconds + (jwtExpiration / 1000);

    return Jwts.builder()
        .claim("userId", user.getId())
        .claim("role", user.getRole() != null ? user.getRole().name() : null)
        .claim("email", user.getEmail())
        .subject(user.getEmail()) // Usar subject() en lugar de claim("sub")
        .issuedAt(new Date(nowSeconds * 1000)) // Convertir de vuelta a milisegundos para Date
        .expiration(new Date(expSeconds * 1000)) // Usar expiration() en lugar de claim("exp")
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
