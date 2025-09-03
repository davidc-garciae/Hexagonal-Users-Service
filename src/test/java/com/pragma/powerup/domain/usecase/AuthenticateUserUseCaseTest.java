package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthenticateUserUseCase domain logic.
 * Tests authentication business rules without external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Domain: Authenticate User Use Case Tests")
class AuthenticateUserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    private AuthenticateUserUseCase authenticateUserUseCase;

    private UserModel adminUser;
    private UserModel ownerUser;
    private UserModel employeeUser;
    private UserModel customerUser;

    @BeforeEach
    void setUp() {
        authenticateUserUseCase = new AuthenticateUserUseCase(userPersistencePort, passwordEncoderPort);

        adminUser = TestDataFactory.createValidAdminUser();
        ownerUser = TestDataFactory.createValidOwnerUser();
        employeeUser = TestDataFactory.createValidEmployeeUser();
        customerUser = TestDataFactory.createValidCustomerUser();
    }

    @Test
    @DisplayName("Should authenticate admin user successfully")
    void shouldAuthenticateAdminUserSuccessfully() {
        // Given
        String email = "admin@test.com";
        String password = "admin123";
        when(userPersistencePort.findByEmail(email)).thenReturn(adminUser);
        when(passwordEncoderPort.matches(password, adminUser.getPassword())).thenReturn(true);

        // When
        UserModel result = authenticateUserUseCase.authenticate(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(adminUser.getRole());
        assertThat(result.getActive()).isTrue();
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(password, adminUser.getPassword());
    }

    @Test
    @DisplayName("Should authenticate owner user successfully")
    void shouldAuthenticateOwnerUserSuccessfully() {
        // Given
        String email = "owner@test.com";
        String password = "owner123";
        when(userPersistencePort.findByEmail(email)).thenReturn(ownerUser);
        when(passwordEncoderPort.matches(password, ownerUser.getPassword())).thenReturn(true);

        // When
        UserModel result = authenticateUserUseCase.authenticate(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(ownerUser.getRole());
        assertThat(result.getRestaurantId()).isEqualTo(ownerUser.getRestaurantId());
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(password, ownerUser.getPassword());
    }

    @Test
    @DisplayName("Should authenticate customer user successfully")
    void shouldAuthenticateCustomerUserSuccessfully() {
        // Given
        String email = "customer@test.com";
        String password = "customer123";
        when(userPersistencePort.findByEmail(email)).thenReturn(customerUser);
        when(passwordEncoderPort.matches(password, customerUser.getPassword())).thenReturn(true);

        // When
        UserModel result = authenticateUserUseCase.authenticate(email, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(customerUser.getRole());
        assertThat(result.getRestaurantId()).isNull();
        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(password, customerUser.getPassword());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String email = "notfound@test.com";
        String password = "customer123";
        when(userPersistencePort.findByEmail(email)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authenticateUserUseCase.authenticate(email, password))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid credentials");

        verify(userPersistencePort).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Given
        String email = "admin@test.com";
        String wrongPassword = "wrongpassword";
        when(userPersistencePort.findByEmail(email)).thenReturn(adminUser);
        when(passwordEncoderPort.matches(wrongPassword, adminUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authenticateUserUseCase.authenticate(email, wrongPassword))
                .isInstanceOf(DomainException.class)
                .hasMessage("Invalid credentials");

        verify(userPersistencePort).findByEmail(email);
        verify(passwordEncoderPort).matches(wrongPassword, adminUser.getPassword());
    }

    @Test
    @DisplayName("Should throw exception when user is inactive")
    void shouldThrowExceptionWhenUserInactive() {
        // Given
        String email = "inactive@test.com";
        String password = "customer123";
        UserModel inactiveUser = TestDataFactory.createInactiveUser();
        when(userPersistencePort.findByEmail(email)).thenReturn(inactiveUser);

        // When & Then
        assertThatThrownBy(() -> authenticateUserUseCase.authenticate(email, password))
                .isInstanceOf(DomainException.class)
                .hasMessage("User is not active");

        verify(userPersistencePort).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void shouldThrowExceptionWhenEmailIsNull() {
        // When & Then
        assertThatThrownBy(() -> authenticateUserUseCase.authenticate(null, "password123"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Email and password are required");
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void shouldThrowExceptionWhenPasswordIsNull() {
        // When & Then
        assertThatThrownBy(() -> authenticateUserUseCase.authenticate("valid@test.com", null))
                .isInstanceOf(DomainException.class)
                .hasMessage("Email and password are required");
    }

    @ParameterizedTest
    @MethodSource("validUserRoles")
    @DisplayName("Should authenticate users with different roles successfully")
    void shouldAuthenticateUsersWithDifferentRoles(UserModel user, String description) {
        // Given
        String testPassword = "testPassword123";
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoderPort.matches(testPassword, user.getPassword())).thenReturn(true);

        // When
        UserModel result = authenticateUserUseCase.authenticate(user.getEmail(), testPassword);

        // Then
        assertThat(result.getRole()).isEqualTo(user.getRole());
        assertThat(result.getActive()).isTrue();
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    private static Stream<Arguments> validUserRoles() {
        return Stream.of(
                Arguments.of(TestDataFactory.createValidAdminUser(), "Admin user authentication"),
                Arguments.of(TestDataFactory.createValidOwnerUser(), "Owner user authentication"),
                Arguments.of(TestDataFactory.createValidEmployeeUser(), "Employee user authentication"),
                Arguments.of(TestDataFactory.createValidCustomerUser(), "Customer user authentication"));
    }
}
