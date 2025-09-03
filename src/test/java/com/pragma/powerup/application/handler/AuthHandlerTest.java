package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.application.handler.impl.AuthHandler;
import com.pragma.powerup.domain.api.IAuthServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.spi.IJwtProviderPort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.shared.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthHandler application layer.
 * Tests the orchestration of domain services and DTO mapping.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Application: Auth Handler Tests")
class AuthHandlerTest {

    @Mock
    private IAuthServicePort authServicePort;

    @Mock
    private IJwtProviderPort jwtProviderPort;

    private AuthHandler authHandler;

    private UserModel adminUser;
    private UserModel customerUser;
    private UserModel ownerUser;

    @BeforeEach
    void setUp() {
        authHandler = new AuthHandler(authServicePort, jwtProviderPort);

        adminUser = TestDataFactory.createValidAdminUser();
        customerUser = TestDataFactory.createValidCustomerUser();
        ownerUser = TestDataFactory.createValidOwnerUser();
    }

    @Test
    @DisplayName("Should login admin user successfully and return valid auth response")
    void shouldLoginAdminUserSuccessfully() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidAdminLoginRequest();
        String expectedToken = "jwt-admin-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(adminUser);
        when(jwtProviderPort.generateToken(adminUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getUserId()).isEqualTo(adminUser.getId());
        assertThat(result.getRole()).isEqualTo("ADMIN");
        assertThat(result.getExpiresIn()).isEqualTo(expectedExpiration);

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort).generateToken(adminUser);
        verify(jwtProviderPort).getExpirationMs();
    }

    @Test
    @DisplayName("Should login customer user successfully and return valid auth response")
    void shouldLoginCustomerUserSuccessfully() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidCustomerLoginRequest();
        String expectedToken = "jwt-customer-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(customerUser);
        when(jwtProviderPort.generateToken(customerUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getUserId()).isEqualTo(customerUser.getId());
        assertThat(result.getRole()).isEqualTo("CUSTOMER");
        assertThat(result.getExpiresIn()).isEqualTo(expectedExpiration);

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort).generateToken(customerUser);
        verify(jwtProviderPort).getExpirationMs();
    }

    @Test
    @DisplayName("Should login owner user successfully and return valid auth response")
    void shouldLoginOwnerUserSuccessfully() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidOwnerLoginRequest();
        String expectedToken = "jwt-owner-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(ownerUser);
        when(jwtProviderPort.generateToken(ownerUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getUserId()).isEqualTo(ownerUser.getId());
        assertThat(result.getRole()).isEqualTo("OWNER");
        assertThat(result.getExpiresIn()).isEqualTo(expectedExpiration);

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort).generateToken(ownerUser);
        verify(jwtProviderPort).getExpirationMs();
    }

    @Test
    @DisplayName("Should handle user with null role gracefully")
    void shouldHandleUserWithNullRoleGracefully() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidCustomerLoginRequest();
        UserModel userWithNullRole = TestDataFactory.createUserWithNullRole();
        String expectedToken = "jwt-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(userWithNullRole);
        when(jwtProviderPort.generateToken(userWithNullRole)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getUserId()).isEqualTo(userWithNullRole.getId());
        assertThat(result.getRole()).isNull();
        assertThat(result.getExpiresIn()).isEqualTo(expectedExpiration);
    }

    @Test
    @DisplayName("Should throw exception when authentication fails")
    void shouldThrowExceptionWhenAuthenticationFails() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createInvalidEmailLoginRequest();
        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new DomainException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authHandler.login(loginRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid credentials");

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort, never()).generateToken(any());
        verify(jwtProviderPort, never()).getExpirationMs();
    }

    @Test
    @DisplayName("Should throw exception when user is inactive")
    void shouldThrowExceptionWhenUserIsInactive() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidCustomerLoginRequest();
        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenThrow(new DomainException("User is not active"));

        // When & Then
        assertThatThrownBy(() -> authHandler.login(loginRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("User is not active");

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should throw exception when JWT generation fails")
    void shouldThrowExceptionWhenJwtGenerationFails() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidAdminLoginRequest();
        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(adminUser);
        when(jwtProviderPort.generateToken(adminUser))
                .thenThrow(new RuntimeException("JWT generation failed"));

        // When & Then
        assertThatThrownBy(() -> authHandler.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("JWT generation failed");

        verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(jwtProviderPort).generateToken(adminUser);
    }

    @ParameterizedTest
    @MethodSource("validLoginScenarios")
    @DisplayName("Should handle different valid login scenarios")
    void shouldHandleValidLoginScenarios(LoginRequestDto loginRequest, UserModel expectedUser, String expectedRole) {
        // Given
        String expectedToken = "jwt-token-" + expectedRole.toLowerCase();
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(expectedUser);
        when(jwtProviderPort.generateToken(expectedUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(expectedToken);
        assertThat(result.getUserId()).isEqualTo(expectedUser.getId());
        assertThat(result.getRole()).isEqualTo(expectedRole);
        assertThat(result.getExpiresIn()).isEqualTo(expectedExpiration);
    }

    @Test
    @DisplayName("Should verify interaction sequence is correct")
    void shouldVerifyInteractionSequenceIsCorrect() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidAdminLoginRequest();
        String expectedToken = "jwt-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(adminUser);
        when(jwtProviderPort.generateToken(adminUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then - verify order of operations
        var inOrder = inOrder(authServicePort, jwtProviderPort);
        inOrder.verify(authServicePort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        inOrder.verify(jwtProviderPort).generateToken(adminUser);
        inOrder.verify(jwtProviderPort).getExpirationMs();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("Should build response with all required fields")
    void shouldBuildResponseWithAllRequiredFields() {
        // Given
        LoginRequestDto loginRequest = TestDataFactory.createValidAdminLoginRequest();
        String expectedToken = "jwt-admin-token-12345";
        long expectedExpiration = 86400000L;

        when(authServicePort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(adminUser);
        when(jwtProviderPort.generateToken(adminUser)).thenReturn(expectedToken);
        when(jwtProviderPort.getExpirationMs()).thenReturn(expectedExpiration);

        // When
        AuthResponseDto result = authHandler.login(loginRequest);

        // Then - verify all fields are properly set
        assertThat(result.getToken()).isNotEmpty();
        assertThat(result.getUserId()).isPositive();
        assertThat(result.getRole()).isNotEmpty();
        assertThat(result.getExpiresIn()).isPositive();
    }

    private static Stream<Arguments> validLoginScenarios() {
        return Stream.of(
                Arguments.of(
                        TestDataFactory.createValidAdminLoginRequest(),
                        TestDataFactory.createValidAdminUser(),
                        "ADMIN"),
                Arguments.of(
                        TestDataFactory.createValidOwnerLoginRequest(),
                        TestDataFactory.createValidOwnerUser(),
                        "OWNER"),
                Arguments.of(
                        TestDataFactory.createValidCustomerLoginRequest(),
                        TestDataFactory.createValidCustomerUser(),
                        "CUSTOMER"));
    }
}
