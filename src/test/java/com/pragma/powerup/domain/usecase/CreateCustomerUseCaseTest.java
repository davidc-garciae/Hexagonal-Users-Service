package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.domain.spi.IUserPersistencePort;
import com.pragma.powerup.domain.spi.IPasswordEncoderPort;
import com.pragma.powerup.domain.spi.IDateProviderPort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.shared.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateCustomerUseCase domain logic.
 * Tests customer creation business rules without external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Domain: Create Customer Use Case Tests")
class CreateCustomerUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IDateProviderPort dateProviderPort;

    private CreateCustomerUseCase createCustomerUseCase;

    private UserModel validCustomerRequest;
    private UserModel savedCustomer;

    @BeforeEach
    void setUp() {
        createCustomerUseCase = new CreateCustomerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);

        validCustomerRequest = TestDataFactory.createValidCustomerRequestModel();
        validCustomerRequest.setId(null); // New user should not have ID
        validCustomerRequest.setRole(null); // Role will be set by use case
        validCustomerRequest.setActive(null); // Active will be set by use case
        validCustomerRequest.setPassword("plainCustomerPassword123"); // Plain password for encoding

        savedCustomer = TestDataFactory.createValidCustomerUser();
        savedCustomer.setPassword("$2a$10$encodedCustomerPassword"); // Encoded password
    }

    @Test
    @DisplayName("Should create customer successfully with valid data")
    void shouldCreateCustomerSuccessfully() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validCustomerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validCustomerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validCustomerRequest.getPassword()))
                .thenReturn("$2a$10$encodedCustomerPassword");
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedCustomer);

        // When
        UserModel result = createCustomerUseCase.createCustomer(validCustomerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(RoleEnum.CUSTOMER);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getPassword()).isEqualTo("$2a$10$encodedCustomerPassword");
        assertThat(result.getRestaurantId()).isNull(); // Customers don't have restaurant

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validCustomerRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validCustomerRequest.getDocument());
        verify(passwordEncoderPort).encode("plainCustomerPassword123");
        verify(userPersistencePort).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validCustomerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createCustomerUseCase.createCustomer(validCustomerRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Email already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validCustomerRequest.getEmail());
        verify(userPersistencePort, never()).existsByDocument(any());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when document already exists")
    void shouldThrowExceptionWhenDocumentAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validCustomerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validCustomerRequest.getDocument())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createCustomerUseCase.createCustomer(validCustomerRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Document already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validCustomerRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validCustomerRequest.getDocument());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        String plainPassword = "plainCustomerPassword123";
        String encodedPassword = "$2a$10$encodedCustomerPassword";
        validCustomerRequest.setPassword(plainPassword);

        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validCustomerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validCustomerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(plainPassword)).thenReturn(encodedPassword);
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedCustomer);

        // When
        createCustomerUseCase.createCustomer(validCustomerRequest);

        // Then
        verify(passwordEncoderPort).encode(plainPassword);
        // Verify that the request object was modified with encoded password
        assertThat(validCustomerRequest.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Should set customer role and active status with null restaurant")
    void shouldSetCustomerRoleAndActiveStatusWithNullRestaurant() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validCustomerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validCustomerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validCustomerRequest.getPassword()))
                .thenReturn("$2a$10$encodedCustomerPassword");
        when(userPersistencePort.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel savedUser = invocation.getArgument(0);
            savedUser.setId(4L);
            return savedUser;
        });

        // When
        createCustomerUseCase.createCustomer(validCustomerRequest);

        // Then
        // Verify that the request object was modified with correct values
        assertThat(validCustomerRequest.getRole()).isEqualTo(RoleEnum.CUSTOMER);
        assertThat(validCustomerRequest.getActive()).isTrue();
        assertThat(validCustomerRequest.getRestaurantId()).isNull(); // Customers have no restaurant
    }

    @Test
    @DisplayName("Should validate customer age requirement (18+)")
    void shouldValidateCustomerAgeRequirement() {
        // Given - Create customer that is too young (minor)
        validCustomerRequest.setBirthDate(LocalDate.now().minusYears(17)); // 17 years old

        when(dateProviderPort.today()).thenReturn(LocalDate.now());

        // When & Then
        assertThatThrownBy(() -> createCustomerUseCase.createCustomer(validCustomerRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("User must be of legal age");

        verify(dateProviderPort).today();
        verify(userPersistencePort, never()).existsByEmail(any());
        verify(userPersistencePort, never()).existsByDocument(any());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }
}
