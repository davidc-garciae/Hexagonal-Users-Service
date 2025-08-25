package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CreateOwnerUseCaseTest {

  @Mock private IUserPersistencePort userPersistencePort;

  @Mock private IPasswordEncoderPort passwordEncoderPort;

  @Mock private IDateProviderPort dateProviderPort;

  @InjectMocks private CreateOwnerUseCase useCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(dateProviderPort.today()).thenReturn(LocalDate.of(2025, 1, 1));
    useCase = new CreateOwnerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
  }

  private UserModel buildValidUser() {
    UserModel u = new UserModel();
    u.setFirstName("John");
    u.setLastName("Doe");
    u.setDocument("1002003004");
    u.setPhone("+573001112233");
    u.setBirthDate(LocalDate.of(1990, 1, 1));
    u.setEmail("john.doe@example.com");
    u.setPassword("plain-secret");
    return u;
  }

  @Test
  @DisplayName("Should create owner when data is valid")
  void createOwnerOk() {
    UserModel request = buildValidUser();
    when(userPersistencePort.existsByEmail(request.getEmail())).thenReturn(false);
    when(userPersistencePort.existsByDocument(request.getDocument())).thenReturn(false);
    when(passwordEncoderPort.encode("plain-secret")).thenReturn("ENCODED");
    when(userPersistencePort.save(any(UserModel.class)))
        .thenAnswer(
            invocation -> {
              UserModel arg = invocation.getArgument(0);
              arg.setId(1L);
              return arg;
            });

    UserModel created = useCase.createOwner(request);

    assertThat(created.getId()).isEqualTo(1L);
    assertThat(created.getRole()).isEqualTo(RoleEnum.OWNER);
    assertThat(created.getPassword()).isEqualTo("ENCODED");
    assertThat(created.getActive()).isTrue();
    verify(userPersistencePort).save(any(UserModel.class));
  }

  @Test
  @DisplayName("Should fail when email is invalid")
  void createOwnerEmailInvalid() {
    UserModel request = buildValidUser();
    request.setEmail("invalid-email");

    assertThatThrownBy(() -> useCase.createOwner(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid email");
  }

  @Test
  @DisplayName("Should fail when phone is invalid or too long")
  void createOwnerPhoneInvalid() {
    UserModel request = buildValidUser();
    request.setPhone("++57300111223344");

    assertThatThrownBy(() -> useCase.createOwner(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid phone");
  }

  @Test
  @DisplayName("Should fail when document is not numeric")
  void createOwnerDocumentInvalid() {
    UserModel request = buildValidUser();
    request.setDocument("A1234");

    assertThatThrownBy(() -> useCase.createOwner(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Invalid document");
  }

  @Test
  @DisplayName("Should fail when user is underage")
  void createOwnerUnderage() {
    UserModel request = buildValidUser();
    request.setBirthDate(LocalDate.of(2010, 1, 1));

    assertThatThrownBy(() -> useCase.createOwner(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("User must be of legal age");
  }

  @Test
  @DisplayName("Should fail when email already exists")
  void createOwnerEmailDuplicated() {
    UserModel request = buildValidUser();
    when(userPersistencePort.existsByEmail(request.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> useCase.createOwner(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Email already registered");
  }
}


