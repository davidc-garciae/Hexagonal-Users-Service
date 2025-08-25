package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IJwtProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthHandler implements IAuthHandler {

  private final IAuthServicePort authServicePort;
  private final IJwtProviderPort jwtProviderPort;

  @Override
  public AuthResponseDto login(LoginRequestDto request) {
    UserModel user = authServicePort.authenticate(request.getEmail(), request.getPassword());
    String token = jwtProviderPort.generateToken(user);

    return AuthResponseDto.builder()
        .token(token)
        .userId(user.getId())
        .role(user.getRole() != null ? user.getRole().name() : null)
        .expiresIn(jwtProviderPort.getExpirationMs())
        .build();
  }
}
