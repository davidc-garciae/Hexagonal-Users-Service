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

class CreateCustomerUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IPasswordEncoderPort passwordEncoderPort;
    @Mock
    private IDateProviderPort dateProviderPort;

    @InjectMocks
    private CreateCustomerUseCase useCase;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        when(dateProviderPort.today()).thenReturn(LocalDate.of(2025, 1, 1));
        useCase = new CreateCustomerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);
    }

    private UserModel buildValidCustomer() {
        UserModel u = new UserModel();
        u.setFirstName("Alice");
        u.setLastName("Brown");
        u.setDocument("12345678");
        u.setPhone("+573001234567");
        u.setBirthDate(LocalDate.of(1995, 5, 20));
        u.setEmail("alice.brown@example.com");
        u.setPassword("plain");
        return u;
    }

    @Test
    @DisplayName("Should create customer with encoded password and role CUSTOMER")
    void createCustomerOk() {
        var request = buildValidCustomer();
        when(userPersistencePort.existsByEmail(request.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(request.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode("plain")).thenReturn("ENC");
        when(userPersistencePort.save(any(UserModel.class)))
                .thenAnswer(
                        inv -> {
                            UserModel m = inv.getArgument(0);
                            m.setId(5L);
                            return m;
                        });

        var created = useCase.createCustomer(request);

        assertThat(created.getId()).isEqualTo(5L);
        assertThat(created.getRole()).isEqualTo(RoleEnum.CUSTOMER);
        assertThat(created.getPassword()).isEqualTo("ENC");
        assertThat(created.getActive()).isTrue();
        assertThat(created.getRestaurantId()).isNull();
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void emailExists() {
        var request = buildValidCustomer();
        when(userPersistencePort.existsByEmail(request.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> useCase.createCustomer(request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Email already registered");
    }
}
