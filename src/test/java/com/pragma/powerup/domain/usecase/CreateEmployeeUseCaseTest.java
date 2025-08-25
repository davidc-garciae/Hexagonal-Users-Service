package com.pragma.powerup.domain.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

class CreateEmployeeUseCaseTest {

  @Mock private IUserPersistencePort userPersistencePort;
  @Mock private IPasswordEncoderPort passwordEncoderPort;
  @Mock private IDateProviderPort dateProviderPort;

  @InjectMocks private CreateEmployeeUseCase useCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(dateProviderPort.today()).thenReturn(LocalDate.of(2025, 1, 1));
    useCase = new CreateEmployeeUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
  }

  private UserModel buildValidEmployee() {
    UserModel u = new UserModel();
    u.setFirstName("Jane");
    u.setLastName("Smith");
    u.setDocument("99887766");
    u.setPhone("+573114445566");
    u.setBirthDate(LocalDate.of(1990, 6, 15));
    u.setEmail("jane.smith@example.com");
    u.setPassword("plain");
    u.setRestaurantId(10L);
    return u;
  }

  @Test
  @DisplayName("Should create employee when data is valid")
  void createEmployeeOk() {
    UserModel request = buildValidEmployee();
    when(userPersistencePort.existsByEmail(request.getEmail())).thenReturn(false);
    when(userPersistencePort.existsByDocument(request.getDocument())).thenReturn(false);
    when(passwordEncoderPort.encode("plain")).thenReturn("ENC");
    when(userPersistencePort.save(any(UserModel.class)))
        .thenAnswer(
            inv -> {
              UserModel m = inv.getArgument(0);
              m.setId(2L);
              return m;
            });

    UserModel created = useCase.createEmployee(request);

    assertThat(created.getId()).isEqualTo(2L);
    assertThat(created.getRole()).isEqualTo(RoleEnum.EMPLOYEE);
    assertThat(created.getPassword()).isEqualTo("ENC");
  }

  @Test
  @DisplayName("Should fail when restaurantId is null")
  void createEmployeeRequiresRestaurant() {
    UserModel request = buildValidEmployee();
    request.setRestaurantId(null);

    assertThatThrownBy(() -> useCase.createEmployee(request))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("Restaurant is required");
  }
}
