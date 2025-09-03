package com.pragma.powerup.infrastructure.controller;

import com.pragma.powerup.application.dto.request.UserEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.IUserHandler;
import com.pragma.powerup.infrastructure.input.rest.UserRestController;
import com.pragma.powerup.shared.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserRestController infrastructure layer.
 * Tests REST endpoint behavior and HTTP response handling.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Infrastructure: User REST Controller Tests")
class UserRestControllerTest {

    @Mock
    private IUserHandler userHandler;

    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        userRestController = new UserRestController(userHandler);
    }

    @Test
    @DisplayName("Should create owner and return 201 CREATED")
    void shouldCreateOwnerAndReturn201Created() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidOwnerRequest();
        UserResponseDto responseDto = TestDataFactory.createOwnerResponseDto();

        when(userHandler.createOwner(requestDto)).thenReturn(responseDto);

        // When
        ResponseEntity<UserResponseDto> result = userRestController.createOwner(requestDto);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody())
                .isNotNull()
                .isEqualTo(responseDto);

        verify(userHandler).createOwner(requestDto);
    }

    @Test
    @DisplayName("Should create employee and return 201 CREATED")
    void shouldCreateEmployeeAndReturn201Created() {
        // Given
        UserEmployeeRequestDto requestDto = TestDataFactory.createValidEmployeeRequestDto();
        UserResponseDto responseDto = TestDataFactory.createEmployeeResponseDto();

        when(userHandler.createEmployee(requestDto)).thenReturn(responseDto);

        // When
        ResponseEntity<UserResponseDto> result = userRestController.createEmployee(requestDto);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody())
                .isNotNull()
                .isEqualTo(responseDto);

        verify(userHandler).createEmployee(requestDto);
    }

    @Test
    @DisplayName("Should create customer and return 201 CREATED")
    void shouldCreateCustomerAndReturn201Created() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidCustomerRequestDto();
        UserResponseDto responseDto = TestDataFactory.createCustomerResponseDto();

        when(userHandler.createCustomer(requestDto)).thenReturn(responseDto);

        // When
        ResponseEntity<UserResponseDto> result = userRestController.createCustomer(requestDto);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody())
                .isNotNull()
                .isEqualTo(responseDto);

        verify(userHandler).createCustomer(requestDto);
    }

    @Test
    @DisplayName("Should get user by ID and return 200 OK")
    void shouldGetUserByIdAndReturn200Ok() {
        // Given
        Long userId = 2L;
        UserResponseDto responseDto = TestDataFactory.createOwnerResponseDto();

        when(userHandler.getUserById(userId)).thenReturn(responseDto);

        // When
        ResponseEntity<UserResponseDto> result = userRestController.getUserById(userId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
                .isNotNull()
                .isEqualTo(responseDto);

        verify(userHandler).getUserById(userId);
    }

    @Test
    @DisplayName("Should check if user is employee of restaurant and return true")
    void shouldCheckIfUserIsEmployeeOfRestaurantAndReturnTrue() {
        // Given
        Long userId = 3L;
        Long restaurantId = 1L;
        boolean isEmployee = true;

        when(userHandler.isEmployeeOfRestaurant(userId, restaurantId)).thenReturn(isEmployee);

        // When
        ResponseEntity<Boolean> result = userRestController.isEmployeeOfRestaurant(userId, restaurantId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
                .isNotNull()
                .isTrue();

        verify(userHandler).isEmployeeOfRestaurant(userId, restaurantId);
    }

    @Test
    @DisplayName("Should check if user is employee of restaurant and return false")
    void shouldCheckIfUserIsEmployeeOfRestaurantAndReturnFalse() {
        // Given
        Long userId = 4L; // Customer ID
        Long restaurantId = 1L;
        boolean isEmployee = false;

        when(userHandler.isEmployeeOfRestaurant(userId, restaurantId)).thenReturn(isEmployee);

        // When
        ResponseEntity<Boolean> result = userRestController.isEmployeeOfRestaurant(userId, restaurantId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
                .isNotNull()
                .isFalse();

        verify(userHandler).isEmployeeOfRestaurant(userId, restaurantId);
    }

    @Test
    @DisplayName("Should delegate all operations to handler without transformation")
    void shouldDelegateAllOperationsToHandlerWithoutTransformation() {
        // Given
        UserRequestDto ownerRequest = TestDataFactory.createValidOwnerRequest();
        UserEmployeeRequestDto employeeRequest = TestDataFactory.createValidEmployeeRequestDto();
        UserRequestDto customerRequest = TestDataFactory.createValidCustomerRequestDto();

        UserResponseDto ownerResponse = TestDataFactory.createOwnerResponseDto();
        UserResponseDto employeeResponse = TestDataFactory.createEmployeeResponseDto();
        UserResponseDto customerResponse = TestDataFactory.createCustomerResponseDto();

        when(userHandler.createOwner(ownerRequest)).thenReturn(ownerResponse);
        when(userHandler.createEmployee(employeeRequest)).thenReturn(employeeResponse);
        when(userHandler.createCustomer(customerRequest)).thenReturn(customerResponse);

        // When
        userRestController.createOwner(ownerRequest);
        userRestController.createEmployee(employeeRequest);
        userRestController.createCustomer(customerRequest);

        // Then - Verify controller passes objects directly without modification
        verify(userHandler).createOwner(ownerRequest); // Exact DTO passed
        verify(userHandler).createEmployee(employeeRequest); // Exact DTO passed
        verify(userHandler).createCustomer(customerRequest); // Exact DTO passed
    }

    @Test
    @DisplayName("Should handle null response from handler gracefully")
    void shouldHandleNullResponseFromHandlerGracefully() {
        // Given
        Long userId = 999L; // Non-existent user
        when(userHandler.getUserById(userId)).thenReturn(null);

        // When
        ResponseEntity<UserResponseDto> result = userRestController.getUserById(userId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();

        verify(userHandler).getUserById(userId);
    }

    @Test
    @DisplayName("Should ensure correct HTTP status codes for all endpoints")
    void shouldEnsureCorrectHttpStatusCodesForAllEndpoints() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidCustomerRequestDto();
        UserEmployeeRequestDto employeeRequestDto = TestDataFactory.createValidEmployeeRequestDto();
        UserResponseDto responseDto = TestDataFactory.createCustomerResponseDto();
        Long userId = 4L;
        Long restaurantId = 1L;

        when(userHandler.createOwner(any(UserRequestDto.class))).thenReturn(responseDto);
        when(userHandler.createEmployee(any(UserEmployeeRequestDto.class))).thenReturn(responseDto);
        when(userHandler.createCustomer(any(UserRequestDto.class))).thenReturn(responseDto);
        when(userHandler.getUserById(any(Long.class))).thenReturn(responseDto);
        when(userHandler.isEmployeeOfRestaurant(any(Long.class), any(Long.class))).thenReturn(true);

        // When & Then
        // POST endpoints should return 201 CREATED
        assertThat(userRestController.createOwner(requestDto).getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(userRestController.createEmployee(employeeRequestDto).getStatusCode())
                .isEqualTo(HttpStatus.CREATED);
        assertThat(userRestController.createCustomer(requestDto).getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        // GET endpoints should return 200 OK
        assertThat(userRestController.getUserById(userId).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(userRestController.isEmployeeOfRestaurant(userId, restaurantId).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }
}
