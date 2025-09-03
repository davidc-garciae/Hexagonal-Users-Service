package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.UserEmployeeRequestDto;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.application.handler.impl.UserHandler;
import com.pragma.powerup.application.mapper.IUserRequestMapper;
import com.pragma.powerup.application.mapper.IUserResponseMapper;
import com.pragma.powerup.domain.api.IUserServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.shared.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserHandler application layer logic.
 * Tests service orchestration and DTO mapping without external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Application: User Handler Tests")
class UserHandlerTest {

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IUserRequestMapper userRequestMapper;

    @Mock
    private IUserResponseMapper userResponseMapper;

    private UserHandler userHandler;

    @BeforeEach
    void setUp() {
        userHandler = new UserHandler(userServicePort, userRequestMapper, userResponseMapper);
    }

    @Test
    @DisplayName("Should create owner successfully with proper mapping")
    void shouldCreateOwnerSuccessfully() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidOwnerRequest();
        UserModel requestModel = TestDataFactory.createValidOwnerRequestModel();
        UserModel createdModel = TestDataFactory.createValidOwnerUser();
        UserResponseDto responseDto = TestDataFactory.createOwnerResponseDto();

        when(userRequestMapper.toDomain(requestDto)).thenReturn(requestModel);
        when(userServicePort.createOwner(requestModel)).thenReturn(createdModel);
        when(userResponseMapper.toResponse(createdModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.createOwner(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getRole()).isEqualTo("OWNER");
        assertThat(result.getActive()).isTrue();

        // Verify interaction order
        InOrder inOrder = inOrder(userRequestMapper, userServicePort, userResponseMapper);
        inOrder.verify(userRequestMapper).toDomain(requestDto);
        inOrder.verify(userServicePort).createOwner(requestModel);
        inOrder.verify(userResponseMapper).toResponse(createdModel);
    }

    @Test
    @DisplayName("Should create employee successfully with proper mapping")
    void shouldCreateEmployeeSuccessfully() {
        // Given
        UserEmployeeRequestDto requestDto = TestDataFactory.createValidEmployeeRequestDto();
        UserModel requestModel = TestDataFactory.createValidEmployeeRequestModel();
        UserModel createdModel = TestDataFactory.createValidEmployeeUser();
        UserResponseDto responseDto = TestDataFactory.createEmployeeResponseDto();

        when(userRequestMapper.toDomain(requestDto)).thenReturn(requestModel);
        when(userServicePort.createEmployee(requestModel)).thenReturn(createdModel);
        when(userResponseMapper.toResponse(createdModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.createEmployee(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getRole()).isEqualTo("EMPLOYEE");
        assertThat(result.getActive()).isTrue();

        // Verify interaction order
        InOrder inOrder = inOrder(userRequestMapper, userServicePort, userResponseMapper);
        inOrder.verify(userRequestMapper).toDomain(requestDto);
        inOrder.verify(userServicePort).createEmployee(requestModel);
        inOrder.verify(userResponseMapper).toResponse(createdModel);
    }

    @Test
    @DisplayName("Should create customer successfully with proper mapping")
    void shouldCreateCustomerSuccessfully() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidCustomerRequestDto();
        UserModel requestModel = TestDataFactory.createValidCustomerRequestModel();
        UserModel createdModel = TestDataFactory.createValidCustomerUser();
        UserResponseDto responseDto = TestDataFactory.createCustomerResponseDto();

        when(userRequestMapper.toDomain(requestDto)).thenReturn(requestModel);
        when(userServicePort.createCustomer(requestModel)).thenReturn(createdModel);
        when(userResponseMapper.toResponse(createdModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.createCustomer(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getRole()).isEqualTo("CUSTOMER");
        assertThat(result.getActive()).isTrue();

        // Verify interaction order
        InOrder inOrder = inOrder(userRequestMapper, userServicePort, userResponseMapper);
        inOrder.verify(userRequestMapper).toDomain(requestDto);
        inOrder.verify(userServicePort).createCustomer(requestModel);
        inOrder.verify(userResponseMapper).toResponse(createdModel);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 2L;
        UserModel userModel = TestDataFactory.createValidOwnerUser();
        UserResponseDto responseDto = TestDataFactory.createOwnerResponseDto();

        when(userServicePort.getUserById(userId)).thenReturn(userModel);
        when(userResponseMapper.toResponse(userModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getRole()).isEqualTo("OWNER");

        verify(userServicePort).getUserById(userId);
        verify(userResponseMapper).toResponse(userModel);
        verifyNoInteractions(userRequestMapper);
    }

    @Test
    @DisplayName("Should check if user is employee of restaurant")
    void shouldCheckIfUserIsEmployeeOfRestaurant() {
        // Given
        Long userId = 3L;
        Long restaurantId = 1L;
        boolean isEmployee = true;

        when(userServicePort.isEmployeeOfRestaurant(userId, restaurantId)).thenReturn(isEmployee);

        // When
        boolean result = userHandler.isEmployeeOfRestaurant(userId, restaurantId);

        // Then
        assertThat(result).isTrue();

        verify(userServicePort).isEmployeeOfRestaurant(userId, restaurantId);
        verifyNoInteractions(userRequestMapper, userResponseMapper);
    }

    @Test
    @DisplayName("Should check if user is NOT employee of restaurant")
    void shouldCheckIfUserIsNotEmployeeOfRestaurant() {
        // Given
        Long userId = 4L; // Customer ID
        Long restaurantId = 1L;
        boolean isEmployee = false;

        when(userServicePort.isEmployeeOfRestaurant(userId, restaurantId)).thenReturn(isEmployee);

        // When
        boolean result = userHandler.isEmployeeOfRestaurant(userId, restaurantId);

        // Then
        assertThat(result).isFalse();

        verify(userServicePort).isEmployeeOfRestaurant(userId, restaurantId);
        verifyNoInteractions(userRequestMapper, userResponseMapper);
    }

    @Test
    @DisplayName("Should delegate mapper calls without transformation")
    void shouldDelegateMapperCallsWithoutTransformation() {
        // Given
        UserRequestDto requestDto = TestDataFactory.createValidCustomerRequestDto();
        UserModel mappedModel = TestDataFactory.createValidCustomerRequestModel();
        UserModel createdModel = TestDataFactory.createValidCustomerUser();
        UserResponseDto responseDto = TestDataFactory.createCustomerResponseDto();

        when(userRequestMapper.toDomain(requestDto)).thenReturn(mappedModel);
        when(userServicePort.createCustomer(mappedModel)).thenReturn(createdModel);
        when(userResponseMapper.toResponse(createdModel)).thenReturn(responseDto);

        // When
        userHandler.createCustomer(requestDto);

        // Then - Verify that handler passes objects directly without modification
        verify(userRequestMapper).toDomain(requestDto); // Exact DTO passed
        verify(userServicePort).createCustomer(mappedModel); // Exact mapped model passed
        verify(userResponseMapper).toResponse(createdModel); // Exact created model passed
    }

    @Test
    @DisplayName("Should handle employee creation with restaurant ID mapping")
    void shouldHandleEmployeeCreationWithRestaurantIdMapping() {
        // Given
        UserEmployeeRequestDto requestDto = TestDataFactory.createValidEmployeeRequestDto();
        UserModel requestModel = TestDataFactory.createValidEmployeeRequestModel();
        requestModel.setRestaurantId(1L); // Ensure restaurant ID is set
        UserModel createdModel = TestDataFactory.createValidEmployeeUser();
        UserResponseDto responseDto = TestDataFactory.createEmployeeResponseDto();

        when(userRequestMapper.toDomain(requestDto)).thenReturn(requestModel);
        when(userServicePort.createEmployee(requestModel)).thenReturn(createdModel);
        when(userResponseMapper.toResponse(createdModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.createEmployee(requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo("EMPLOYEE");

        // Verify that model with restaurant ID was passed to service
        verify(userServicePort).createEmployee(
                argThat(model -> model.getRestaurantId() != null && model.getRestaurantId().equals(1L)));
    }

    @Test
    @DisplayName("Should ensure proper null handling in responses")
    void shouldEnsureProperNullHandlingInResponses() {
        // Given
        Long userId = 999L; // Non-existent user
        UserModel userModel = null;
        UserResponseDto responseDto = null;

        when(userServicePort.getUserById(userId)).thenReturn(userModel);
        when(userResponseMapper.toResponse(userModel)).thenReturn(responseDto);

        // When
        UserResponseDto result = userHandler.getUserById(userId);

        // Then
        assertThat(result).isNull();

        verify(userServicePort).getUserById(userId);
        verify(userResponseMapper).toResponse(null);
    }
}
