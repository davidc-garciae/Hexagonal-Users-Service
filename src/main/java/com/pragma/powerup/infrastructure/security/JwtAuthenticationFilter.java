package com.pragma.powerup.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Ejecutar ANTES que otros filtros
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtValidator jwtValidator;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);

    try {
      if (jwtValidator.isValid(token)) {
        String userId = jwtValidator.extractUserId(token);
        String email = jwtValidator.extractEmail(token);
        String role = jwtValidator.extractRole(token);

        if (email != null && role != null) {
          UsernamePasswordAuthenticationToken auth =
              new UsernamePasswordAuthenticationToken(
                  email,
                  null,
                  Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));

          // Agregar información adicional al contexto
          auth.setDetails(new AuthDetails(userId, email, role));
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    } catch (Exception e) {
      log.debug("Invalid JWT token: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  public static class AuthDetails {
    private final String userId;
    private final String email;
    private final String role;

    public AuthDetails(String userId, String email, String role) {
      this.userId = userId;
      this.email = email;
      this.role = role;
    }

    public String getUserId() {
      return userId;
    }

    public String getEmail() {
      return email;
    }

    public String getRole() {
      return role;
    }
  }
}
