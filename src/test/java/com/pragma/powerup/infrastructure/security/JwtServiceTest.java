package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.shared.TestDataFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtService infrastructure component.
 * Tests JWT token generation and validation without Spring context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Infrastructure: JWT Service Tests")
class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecretKey = "test-secret-key-for-jwt-testing-must-be-long-enough-for-hmac-sha";
    private final long testExpiration = 86400000L; // 24 hours

    private UserModel adminUser;
    private UserModel customerUser;
    private UserModel ownerUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Set private fields using reflection for testing
        setPrivateField(jwtService, "secretKey", testSecretKey);
        setPrivateField(jwtService, "jwtExpiration", testExpiration);

        adminUser = TestDataFactory.createValidAdminUser();
        customerUser = TestDataFactory.createValidCustomerUser();
        ownerUser = TestDataFactory.createValidOwnerUser();
    }

    @Test
    @DisplayName("Should generate valid JWT token for admin user")
    void shouldGenerateValidTokenForAdmin() {
        // When
        String token = jwtService.generateToken(adminUser);

        // Then
        assertThat(token).isNotNull().isNotEmpty();

        // Verify token structure (header.payload.signature)
        String[] tokenParts = token.split("\\.");
        assertThat(tokenParts).hasSize(3);

        // Verify claims
        Claims claims = extractClaims(token);
        assertThat(claims.get("userId", Long.class)).isEqualTo(adminUser.getId());
        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("email", String.class)).isEqualTo(adminUser.getEmail());
        assertThat(claims.getSubject()).isEqualTo(adminUser.getEmail());
    }

    @Test
    @DisplayName("Should generate valid JWT token for customer user")
    void shouldGenerateValidTokenForCustomer() {
        // When
        String token = jwtService.generateToken(customerUser);

        // Then
        assertThat(token).isNotNull().isNotEmpty();

        // Verify claims
        Claims claims = extractClaims(token);
        assertThat(claims.get("userId", Long.class)).isEqualTo(customerUser.getId());
        assertThat(claims.get("role", String.class)).isEqualTo("CUSTOMER");
        assertThat(claims.get("email", String.class)).isEqualTo(customerUser.getEmail());
        assertThat(claims.getSubject()).isEqualTo(customerUser.getEmail());
    }

    @Test
    @DisplayName("Should generate valid JWT token for owner user")
    void shouldGenerateValidTokenForOwner() {
        // When
        String token = jwtService.generateToken(ownerUser);

        // Then
        assertThat(token).isNotNull().isNotEmpty();

        // Verify claims
        Claims claims = extractClaims(token);
        assertThat(claims.get("userId", Long.class)).isEqualTo(ownerUser.getId());
        assertThat(claims.get("role", String.class)).isEqualTo("OWNER");
        assertThat(claims.get("email", String.class)).isEqualTo(ownerUser.getEmail());
        assertThat(claims.getSubject()).isEqualTo(ownerUser.getEmail());
    }

    @Test
    @DisplayName("Should return correct expiration time")
    void shouldReturnCorrectExpirationTime() {
        // When
        long expiration = jwtService.getExpirationMs();

        // Then
        assertThat(expiration).isEqualTo(testExpiration);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        // When
        String adminToken = jwtService.generateToken(adminUser);
        String customerToken = jwtService.generateToken(customerUser);

        // Then
        assertThat(adminToken).isNotEqualTo(customerToken);

        // Verify both tokens are valid but have different claims
        Claims adminClaims = extractClaims(adminToken);
        Claims customerClaims = extractClaims(customerToken);

        assertThat(adminClaims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(customerClaims.get("role", String.class)).isEqualTo("CUSTOMER");
        assertThat(adminClaims.get("userId", Long.class)).isNotEqualTo(customerClaims.get("userId", Long.class));
    }

    @Test
    @DisplayName("Should handle user with null role gracefully")
    void shouldHandleUserWithNullRole() {
        // Given
        UserModel userWithNullRole = TestDataFactory.createUserWithNullRole();

        // When
        String token = jwtService.generateToken(userWithNullRole);

        // Then
        assertThat(token).isNotNull();

        Claims claims = extractClaims(token);
        assertThat(claims.get("role")).isNull();
        assertThat(claims.get("userId", Long.class)).isEqualTo(userWithNullRole.getId());
        assertThat(claims.get("email", String.class)).isEqualTo(userWithNullRole.getEmail());
    }

    @Test
    @DisplayName("Should include all required claims in token")
    void shouldIncludeAllRequiredClaims() {
        // When
        String token = jwtService.generateToken(customerUser);

        // Then
        Claims claims = extractClaims(token);

        // Verify all required claims are present
        assertThat(claims.get("userId")).isNotNull();
        assertThat(claims.get("role")).isNotNull();
        assertThat(claims.get("email")).isNotNull();
        assertThat(claims.getSubject()).isNotNull();
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();

        // Verify expiration is in the future
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    @DisplayName("Should generate token with proper JWT structure")
    void shouldGenerateTokenWithProperStructure() {
        // When
        String token = jwtService.generateToken(adminUser);

        // Then
        assertThat(token).contains("."); // JWT has dots separating header.payload.signature
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3); // header, payload, signature

        // Verify it's not empty parts
        assertThat(parts[0]).isNotEmpty(); // header
        assertThat(parts[1]).isNotEmpty(); // payload
        assertThat(parts[2]).isNotEmpty(); // signature
    }

    @Test
    @DisplayName("Should generate tokens with consistent expiration time")
    void shouldGenerateTokensWithConsistentExpirationTime() {
        // When
        String token1 = jwtService.generateToken(adminUser);
        String token2 = jwtService.generateToken(customerUser);

        // Then
        Claims claims1 = extractClaims(token1);
        Claims claims2 = extractClaims(token2);

        // Both tokens should have similar expiration times (within a few seconds)
        long exp1 = claims1.getExpiration().getTime();
        long exp2 = claims2.getExpiration().getTime();
        assertThat(Math.abs(exp1 - exp2)).isLessThan(5000); // Within 5 seconds
    }

    @Test
    @DisplayName("Should handle user with minimum required fields")
    void shouldHandleUserWithMinimumRequiredFields() {
        // Given
        UserModel minimalUser = new UserModel();
        minimalUser.setId(99L);
        minimalUser.setEmail("minimal@test.com");
        minimalUser.setRole(RoleEnum.CUSTOMER);

        // When
        String token = jwtService.generateToken(minimalUser);

        // Then
        assertThat(token).isNotNull();

        Claims claims = extractClaims(token);
        assertThat(claims.get("userId", Long.class)).isEqualTo(99L);
        assertThat(claims.get("email", String.class)).isEqualTo("minimal@test.com");
        assertThat(claims.get("role", String.class)).isEqualTo("CUSTOMER");
    }

    @ParameterizedTest
    @MethodSource("userRoleTestCases")
    @DisplayName("Should generate correct role claim for different user types")
    void shouldGenerateCorrectRoleClaimForDifferentUserTypes(UserModel user, String expectedRole) {
        // When
        String token = jwtService.generateToken(user);

        // Then
        Claims claims = extractClaims(token);
        assertThat(claims.get("role", String.class)).isEqualTo(expectedRole);
    }

    @Test
    @DisplayName("Should throw exception for null user")
    void shouldThrowExceptionForNullUser() {
        // When & Then
        assertThatThrownBy(() -> jwtService.generateToken(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should generate tokens that are properly signed")
    void shouldGenerateTokensThatAreProperlyS

    () {
        // When
        String token = jwtService.generateToken(adminUser);

        // Then
        // If we can parse the token with the same key, it means it's properly signed
        assertThat(extractClaims(token)).isNotNull();
    }

    /**
     * Helper method to extract claims from a JWT token
     */
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(testSecretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Helper method to set private fields using reflection for testing
     */
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field " + fieldName, e);
        }
    }

    private static Stream<Arguments> userRoleTestCases() {
        return Stream.of(
                Arguments.of(TestDataFactory.createValidAdminUser(), "ADMIN"),
                Arguments.of(TestDataFactory.createValidOwnerUser(), "OWNER"),
                Arguments.of(TestDataFactory.createValidEmployeeUser(), "EMPLOYEE"),
                Arguments.of(TestDataFactory.createValidCustomerUser(), "CUSTOMER"));
    }
}
