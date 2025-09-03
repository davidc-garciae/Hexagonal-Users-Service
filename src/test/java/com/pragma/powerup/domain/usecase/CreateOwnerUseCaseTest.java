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
 * Unit tests for CreateOwnerUseCase domain logic.
 * Tests owner creation business rules without external dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Domain: Create Owner Use Case Tests")
class CreateOwnerUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IDateProviderPort dateProviderPort;

    private CreateOwnerUseCase createOwnerUseCase;

    private UserModel validOwnerRequest;
    private UserModel savedOwner;

    @BeforeEach
    void setUp() {
        createOwnerUseCase = new CreateOwnerUseCase(userPersistencePort, passwordEncoderPort, dateProviderPort);

        validOwnerRequest = TestDataFactory.createValidOwnerRequestModel();
        validOwnerRequest.setId(null); // New user should not have ID
        validOwnerRequest.setRole(null); // Role will be set by use case
        validOwnerRequest.setActive(null); // Active will be set by use case
        validOwnerRequest.setPassword("plainOwnerPassword123"); // Plain password for encoding

        savedOwner = TestDataFactory.createValidOwnerUser();
        savedOwner.setPassword("$2a$10$encodedOwnerPassword"); // Encoded password
    }

    @Test
    @DisplayName("Should create owner successfully with valid data")
    void shouldCreateOwnerSuccessfully() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validOwnerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validOwnerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validOwnerRequest.getPassword())).thenReturn("$2a$10$encodedOwnerPassword");
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedOwner);

        // When
        UserModel result = createOwnerUseCase.createOwner(validOwnerRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(RoleEnum.OWNER);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getPassword()).isEqualTo("$2a$10$encodedOwnerPassword");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validOwnerRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validOwnerRequest.getDocument());
        verify(passwordEncoderPort).encode("plainOwnerPassword123");
        verify(userPersistencePort).save(any(UserModel.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validOwnerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createOwnerUseCase.createOwner(validOwnerRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Email already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validOwnerRequest.getEmail());
        verify(userPersistencePort, never()).existsByDocument(any());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when document already exists")
    void shouldThrowExceptionWhenDocumentAlreadyExists() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validOwnerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validOwnerRequest.getDocument())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createOwnerUseCase.createOwner(validOwnerRequest))
                .isInstanceOf(DomainException.class)
                .hasMessage("Document already registered");

        verify(dateProviderPort).today();
        verify(userPersistencePort).existsByEmail(validOwnerRequest.getEmail());
        verify(userPersistencePort).existsByDocument(validOwnerRequest.getDocument());
        verify(passwordEncoderPort, never()).encode(any());
        verify(userPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePasswordBeforeSaving() {
        // Given
        String plainPassword = "plainOwnerPassword123";
        String encodedPassword = "$2a$10$encodedOwnerPassword";
        validOwnerRequest.setPassword(plainPassword);

        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validOwnerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validOwnerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(plainPassword)).thenReturn(encodedPassword);
        when(userPersistencePort.save(any(UserModel.class))).thenReturn(savedOwner);

        // When
        createOwnerUseCase.createOwner(validOwnerRequest);

        // Then
        verify(passwordEncoderPort).encode(plainPassword);
        // Verify that the request object was modified with encoded password
        assertThat(validOwnerRequest.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("Should set owner role and active status")
    void shouldSetOwnerRoleAndActiveStatus() {
        // Given
        when(dateProviderPort.today()).thenReturn(LocalDate.now());
        when(userPersistencePort.existsByEmail(validOwnerRequest.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocument(validOwnerRequest.getDocument())).thenReturn(false);
        when(passwordEncoderPort.encode(validOwnerRequest.getPassword())).thenReturn("$2a$10$encodedOwnerPassword");
        when(userPersistencePort.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return savedUser;
        });

        // When
        createOwnerUseCase.createOwner(validOwnerRequest);

        // Then
        // Verify that the request object was modified with correct values
        assertThat(validOwnerRequest.getRole()).isEqualTo(RoleEnum.OWNER);
        assertThat(validOwnerRequest.getActive()).isTrue();
    }
}
