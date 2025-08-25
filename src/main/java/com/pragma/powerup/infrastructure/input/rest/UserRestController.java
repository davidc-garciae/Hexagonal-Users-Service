package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.UserEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.pragma.powerup.infrastructure.security.RoleConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

  private final IUserHandler userHandler;

  @PostMapping("/owner")
  @PreAuthorize("hasRole('" + RoleConstants.ADMIN + "')")
  public ResponseEntity<UserResponseDto> createOwner(@Valid @RequestBody UserRequestDto request) {
    var response = userHandler.createOwner(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/employee")
  @PreAuthorize("hasRole('" + RoleConstants.OWNER + "')")
  public ResponseEntity<UserResponseDto> createEmployee(
      @Valid @RequestBody UserEmployeeRequestDto request) {
    var response = userHandler.createEmployee(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
