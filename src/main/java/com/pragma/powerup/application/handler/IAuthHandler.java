package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;

public interface IAuthHandler {
  AuthResponseDto login(LoginRequestDto request);
}
