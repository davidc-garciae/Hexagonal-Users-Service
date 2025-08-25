package com.pragma.powerup.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String userId = request.getHeader("X-User-Id");
    String userEmail = request.getHeader("X-User-Email");
    String userRole = request.getHeader("X-User-Role");

    if (userId != null && userEmail != null && userRole != null) {
      var auth = new SimpleAuthToken(userId, userEmail, userRole);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  static class SimpleAuthToken extends AbstractAuthenticationToken {
    @Getter
    private final String userId;
    private final String email;
    @Getter
    private final String role;

    SimpleAuthToken(String userId, String email, String role) {
      super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
      this.userId = userId;
      this.email = email;
      this.role = role;
      setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
      return "N/A";
    }

    @Override
    public Object getPrincipal() {
      return email;
    }

  }
}
