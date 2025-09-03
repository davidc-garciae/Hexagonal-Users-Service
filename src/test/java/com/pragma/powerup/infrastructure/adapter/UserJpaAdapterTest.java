package com.pragma.powerup.infrastructure.adapter;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IUserRepository;
import com.pragma.powerup.shared.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserJpaAdapter infrastructure layer.
 * Tests JPA repository interactions and entity mapping without database.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Infrastructure: User JPA Adapter Tests")
class UserJpaAdapterTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IUserEntityMapper userEntityMapper;

    private UserJpaAdapter userJpaAdapter;

    private UserModel testUserModel;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        userJpaAdapter = new UserJpaAdapter(userRepository, userEntityMapper);

        testUserModel = TestDataFactory.createValidOwnerUser();
        testUserEntity = createUserEntity();
    }

    private UserEntity createUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(2L);
        entity.setFirstName("Restaurant");
        entity.setLastName("Owner");
        entity.setDocument("87654321");
        entity.setPhone("+573009876543");
        entity.setEmail("owner@test.com");
        entity.setPassword("$2a$10$encodedPassword");
        entity.setRole("OWNER");
        entity.setActive(true);
        entity.setRestaurantId(1L);
        return entity;
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Given
        String email = "owner@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // When
        boolean exists = userJpaAdapter.existsByEmail(email);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(email);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should check if email does not exist")
    void shouldCheckIfEmailDoesNotExist() {
        // Given
        String email = "nonexistent@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean exists = userJpaAdapter.existsByEmail(email);

        // Then
        assertThat(exists).isFalse();
        verify(userRepository).existsByEmail(email);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should check if document exists")
    void shouldCheckIfDocumentExists() {
        // Given
        String document = "87654321";
        when(userRepository.existsByDocument(document)).thenReturn(true);

        // When
        boolean exists = userJpaAdapter.existsByDocument(document);

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByDocument(document);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should check if document does not exist")
    void shouldCheckIfDocumentDoesNotExist() {
        // Given
        String document = "99999999";
        when(userRepository.existsByDocument(document)).thenReturn(false);

        // When
        boolean exists = userJpaAdapter.existsByDocument(document);

        // Then
        assertThat(exists).isFalse();
        verify(userRepository).existsByDocument(document);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        when(userEntityMapper.toEntity(testUserModel)).thenReturn(testUserEntity);
        when(userRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userEntityMapper.toDomain(testUserEntity)).thenReturn(testUserModel);

        // When
        UserModel result = userJpaAdapter.save(testUserModel);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testUserModel);

        verify(userEntityMapper).toEntity(testUserModel);
        verify(userRepository).save(testUserEntity);
        verify(userEntityMapper).toDomain(testUserEntity);
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        String email = "owner@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUserEntity));
        when(userEntityMapper.toDomain(testUserEntity)).thenReturn(testUserModel);

        // When
        UserModel result = userJpaAdapter.findByEmail(email);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testUserModel);

        verify(userRepository).findByEmail(email);
        verify(userEntityMapper).toDomain(testUserEntity);
    }

    @Test
    @DisplayName("Should return null when user not found by email")
    void shouldReturnNullWhenUserNotFoundByEmail() {
        // Given
        String email = "nonexistent@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        UserModel result = userJpaAdapter.findByEmail(email);

        // Then
        assertThat(result).isNull();

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void shouldFindUserByIdSuccessfully() {
        // Given
        Long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUserEntity));
        when(userEntityMapper.toDomain(testUserEntity)).thenReturn(testUserModel);

        // When
        UserModel result = userJpaAdapter.findById(userId);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testUserModel);

        verify(userRepository).findById(userId);
        verify(userEntityMapper).toDomain(testUserEntity);
    }

    @Test
    @DisplayName("Should return null when user not found by ID")
    void shouldReturnNullWhenUserNotFoundById() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        UserModel result = userJpaAdapter.findById(userId);

        // Then
        assertThat(result).isNull();

        verify(userRepository).findById(userId);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    @DisplayName("Should handle mapping in save operation correctly")
    void shouldHandleMappingInSaveOperationCorrectly() {
        // Given
        UserModel inputModel = TestDataFactory.createValidCustomerRequestModel();
        UserEntity mappedEntity = createCustomerEntity();
        UserEntity savedEntity = createCustomerEntity();
        savedEntity.setId(4L); // Simulate ID assignment by database
        UserModel resultModel = TestDataFactory.createValidCustomerUser();

        when(userEntityMapper.toEntity(inputModel)).thenReturn(mappedEntity);
        when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toDomain(savedEntity)).thenReturn(resultModel);

        // When
        UserModel result = userJpaAdapter.save(inputModel);

        // Then
        assertThat(result).isEqualTo(resultModel);

        // Verify mapping sequence
        verify(userEntityMapper).toEntity(inputModel); // Model -> Entity
        verify(userRepository).save(mappedEntity); // Save entity
        verify(userEntityMapper).toDomain(savedEntity); // Entity -> Model
    }

    @Test
    @DisplayName("Should ensure no data transformation in adapter")
    void shouldEnsureNoDataTransformationInAdapter() {
        // Given
        when(userEntityMapper.toEntity(any(UserModel.class))).thenReturn(testUserEntity);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUserEntity);
        when(userEntityMapper.toDomain(any(UserEntity.class))).thenReturn(testUserModel);

        // When
        userJpaAdapter.save(testUserModel);

        // Then - Verify adapter delegates all operations without transformation
        verify(userEntityMapper).toEntity(testUserModel); // Exact model passed to mapper
        verify(userRepository).save(testUserEntity); // Exact entity passed to repository
        verify(userEntityMapper).toDomain(testUserEntity); // Exact entity passed to mapper
    }

    private UserEntity createCustomerEntity() {
        UserEntity entity = new UserEntity();
        entity.setFirstName("New");
        entity.setLastName("Customer");
        entity.setDocument("98765432");
        entity.setPhone("+573009876543");
        entity.setEmail("newcustomer@test.com");
        entity.setPassword("customer123");
        entity.setRole("CUSTOMER");
        entity.setActive(true);
        entity.setRestaurantId(null);
        return entity;
    }
}
