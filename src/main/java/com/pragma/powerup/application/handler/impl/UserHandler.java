package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserHandler implements IUserHandler {

  private final IUserServicePort userServicePort;
  private final IUserRequestMapper userRequestMapper;
  private final IUserResponseMapper userResponseMapper;

  @Override
  public UserResponseDto createOwner(UserRequestDto request) {
    var model = userRequestMapper.toDomain(request);
    var created = userServicePort.createOwner(model);
    return userResponseMapper.toResponse(created);
  }
}
