package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AuthenticateUserUseCaseTest {

  @Mock private IUserPersistencePort userPersistencePort;
  @Mock private IPasswordEncoderPort passwordEncoderPort;

  private AuthenticateUserUseCase useCase;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    useCase = new AuthenticateUserUseCase(userPersistencePort, passwordEncoderPort);
  }

  @Test
  @DisplayName("shouldAuthenticateWhenCredentialsAreValid")
  void shouldAuthenticateWhenCredentialsAreValid() {
    var user = new UserModel();
    user.setId(1L);
    user.setEmail("user@test.com");
    user.setPassword("encoded");
    user.setRole(RoleEnum.CUSTOMER);
    user.setActive(true);

    when(userPersistencePort.findByEmail("user@test.com")).thenReturn(user);
    when(passwordEncoderPort.matches("raw", "encoded")).thenReturn(true);

    UserModel result = useCase.authenticate("user@test.com", "raw");

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getRole()).isEqualTo(RoleEnum.CUSTOMER);
  }

  @Test
  @DisplayName("shouldThrowWhenUserNotFound")
  void shouldThrowWhenUserNotFound() {
    when(userPersistencePort.findByEmail(anyString())).thenReturn(null);

    assertThatThrownBy(() -> useCase.authenticate("x@test.com", "x"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Invalid credentials");
  }

  @Test
  @DisplayName("shouldThrowWhenPasswordDoesNotMatch")
  void shouldThrowWhenPasswordDoesNotMatch() {
    var user = new UserModel();
    user.setEmail("user@test.com");
    user.setPassword("encoded");
    user.setActive(true);

    when(userPersistencePort.findByEmail("user@test.com")).thenReturn(user);
    when(passwordEncoderPort.matches("raw", "encoded")).thenReturn(false);

    assertThatThrownBy(() -> useCase.authenticate("user@test.com", "raw"))
        .isInstanceOf(DomainException.class)
        .hasMessage("Invalid credentials");
  }

  @Test
  @DisplayName("shouldThrowWhenUserIsInactive")
  void shouldThrowWhenUserIsInactive() {
    var user = new UserModel();
    user.setEmail("user@test.com");
    user.setPassword("encoded");
    user.setActive(false);

    when(userPersistencePort.findByEmail("user@test.com")).thenReturn(user);

    assertThatThrownBy(() -> useCase.authenticate("user@test.com", "raw"))
        .isInstanceOf(DomainException.class)
        .hasMessage("User is not active");
  }
}
