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
 * Unit tests for CreateEmployeeUseCase domain logic.
 * Tests employee creation business rules without external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Domain: Create Employee Use Case Tests")
class CreateEmployeeUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IDateProviderPort dateProviderPort;

    private CreateEmployeeUseCase createEmployeeUseCase;

    private UserModel validEmployeeRequest;
    private UserModel savedEmployee;

    @BeforeEach
    void setUp() {
        createEmployeeUseCase = new CreateEmployeeUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);

        validEmployeeRequest = TestDataFactory.createValidEmployeeRequestModel();
        validEmployeeRequest.setId(null); // New user should not have ID
        validEmployeeRequest.setRole(null); // Role will be set by use case
        validEmployeeRequest.setActive(null); // Active will be set by use case
        validEmployeeRequest.setPassword("plainEmployeePassword123"); // Plain password for encoding
        validEmployeeRequest.setRestaurantId(1L); // Required for employees

        savedEmployee = TestDataFactory.createValidEmployeeUser();
        savedEmployee.setPassword("$2a$10$encodedEmployeePassword"); // Encoded password
        savedEmployee.setRestaurantId(1L);
    }

    @Test
    @DisplayName("Should create employee successfully with valid data")
    void shouldCreateEmployeeSuccessfully() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validEmployeeRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validEmployeeRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validEmployeeRequest.getPassword()))
                .thenReturn("$2a$10$encodedEmployeePassword");
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedEmployee);

        // When
        UserModel result = createEmployeeUseCase.createEmployee(validEmployeeRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(RoleEnum.EMPLOYEE);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getPassword()).isEqualTo("$2a$10$encodedEmployeePassword");
        assertThat(result.getRestaurantId()).isEqualTo(1L);

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validEmployeeRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validEmployeeRequest.getDocument());
        verify(passwordEncoderPort).encode("plainEmployeePassword123");
        verify(userPersistencePort).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validEmployeeRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createEmployeeUseCase.createEmployee(validEmployeeRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Email already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validEmployeeRequest.getEmail());
        verify(userPersistencePort, never()).existsByDocument(any());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when document already exists")
    void shouldThrowExceptionWhenDocumentAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validEmployeeRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validEmployeeRequest.getDocument())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createEmployeeUseCase.createEmployee(validEmployeeRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Document already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validEmployeeRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validEmployeeRequest.getDocument());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when restaurant ID is null")
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        // Given
        validEmployeeRequest.setRestaurantId(null);
        when(dateProviderPort.today()).thenReturn(LocalDate.now());

        // When & Then
        assertThatThrownBy(() -> createEmployeeUseCase.createEmployee(validEmployeeRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Restaurant is required for employee");

        verify(dateProviderPort).today();
        verify(userPersistencePort, never()).existsByEmail(any());
        verify(userPersistencePort, never()).existsByDocument(any());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        String plainPassword = "plainEmployeePassword123";
        String encodedPassword = "$2a$10$encodedEmployeePassword";
        validEmployeeRequest.setPassword(plainPassword);

        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validEmployeeRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validEmployeeRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(plainPassword)).thenReturn(encodedPassword);
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedEmployee);

        // When
        createEmployeeUseCase.createEmployee(validEmployeeRequest);

        // Then
        verify(passwordEncoderPort).encode(plainPassword);
        // Verify that the request object was modified with encoded password
        assertThat(validEmployeeRequest.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Should set employee role and active status")
    void shouldSetEmployeeRoleAndActiveStatus() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validEmployeeRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validEmployeeRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validEmployeeRequest.getPassword()))
                .thenReturn("$2a$10$encodedEmployeePassword");
        when(userPersistencePort.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel savedUser = invocation.getArgument(0);
            savedUser.setId(3L);
            return savedUser;
        });

        // When
        createEmployeeUseCase.createEmployee(validEmployeeRequest);

        // Then
        // Verify that the request object was modified with correct values
        assertThat(validEmployeeRequest.getRole()).isEqualTo(RoleEnum.EMPLOYEE);
        assertThat(validEmployeeRequest.getActive()).isTrue();
        assertThat(validEmployeeRequest.getRestaurantId()).isEqualTo(1L);
    }
}
