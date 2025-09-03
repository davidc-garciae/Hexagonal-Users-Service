package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.UserEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.infrastructure.security.RoleConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @PostMapping("/customer")
  public ResponseEntity<UserResponseDto> createCustomer(
      @Valid @RequestBody UserRequestDto request) {
    var response = userHandler.createCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
    var response = userHandler.getUserById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}/restaurant/{restaurantId}/is-employee")
  public ResponseEntity<Boolean> isEmployeeOfRestaurant(
      @PathVariable Long userId, @PathVariable Long restaurantId) {
    boolean isEmployee = userHandler.isEmployeeOfRestaurant(userId, restaurantId);
    return ResponseEntity.ok(isEmployee);
  }
}
