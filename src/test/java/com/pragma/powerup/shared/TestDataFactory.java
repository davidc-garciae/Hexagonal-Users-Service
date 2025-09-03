package com.pragma.powerup.shared;

import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.model.RoleEnum;
import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.dto.request.UserEmployeeRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.application.dto.response.UserResponseDto;
import java.time.LocalDate;

/**
 * Factory for creating test data objects.
 * Provides consistent test data across all test suites.
 */
public class TestDataFactory {

    // =================================
    // Domain Model Factory Methods
    // =================================

    public static UserModel createValidAdminUser() {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setDocument("12345678");
        user.setPhone("+573001234567");
        user.setBirthDate(LocalDate.of(1980, 1, 15));
        user.setEmail("admin@test.com");
        user.setPassword("$2a$10$encodedPassword"); // BCrypt encoded
        user.setRole(RoleEnum.ADMIN);
        user.setActive(true);
        user.setRestaurantId(null);
        return user;
    }

    public static UserModel createValidOwnerUser() {
        UserModel user = new UserModel();
        user.setId(2L);
        user.setFirstName("Restaurant");
        user.setLastName("Owner");
        user.setDocument("87654321");
        user.setPhone("+573009876543");
        user.setBirthDate(LocalDate.of(1975, 8, 22));
        user.setEmail("owner@test.com");
        user.setPassword("$2a$10$encodedPassword");
        user.setRole(RoleEnum.OWNER);
        user.setActive(true);
        user.setRestaurantId(1L);
        return user;
    }

    public static UserModel createValidEmployeeUser() {
        UserModel user = new UserModel();
        user.setId(3L);
        user.setFirstName("Employee");
        user.setLastName("User");
        user.setDocument("11223344");
        user.setPhone("+573005566778");
        user.setBirthDate(LocalDate.of(1990, 3, 10));
        user.setEmail("employee@test.com");
        user.setPassword("$2a$10$encodedPassword");
        user.setRole(RoleEnum.EMPLOYEE);
        user.setActive(true);
        user.setRestaurantId(1L);
        return user;
    }

    public static UserModel createValidCustomerUser() {
        UserModel user = new UserModel();
        user.setId(4L);
        user.setFirstName("Customer");
        user.setLastName("User");
        user.setDocument("99887766");
        user.setPhone("+573002233445");
        user.setBirthDate(LocalDate.of(1995, 12, 5));
        user.setEmail("customer@test.com");
        user.setPassword("$2a$10$encodedPassword");
        user.setRole(RoleEnum.CUSTOMER);
        user.setActive(true);
        user.setRestaurantId(null);
        return user;
    }

    public static UserModel createInactiveUser() {
        UserModel user = createValidCustomerUser();
        user.setActive(false);
        return user;
    }

    public static UserModel createUserWithNullRole() {
        UserModel user = createValidCustomerUser();
        user.setRole(null);
        return user;
    }

    // =================================
    // DTO Factory Methods
    // =================================

    public static LoginRequestDto createValidAdminLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("admin@test.com");
        request.setPassword("admin123");
        return request;
    }

    public static LoginRequestDto createValidOwnerLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("owner@test.com");
        request.setPassword("owner123");
        return request;
    }

    public static LoginRequestDto createValidCustomerLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("customer@test.com");
        request.setPassword("customer123");
        return request;
    }

    public static LoginRequestDto createInvalidEmailLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("invalid-email");
        request.setPassword("password123");
        return request;
    }

    public static LoginRequestDto createEmptyPasswordLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("valid@test.com");
        request.setPassword("");
        return request;
    }

    public static LoginRequestDto createNullEmailLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail(null);
        request.setPassword("password123");
        return request;
    }

    public static LoginRequestDto createNullPasswordLoginRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("valid@test.com");
        request.setPassword(null);
        return request;
    }

    public static UserRequestDto createValidOwnerRequest() {
        UserRequestDto request = new UserRequestDto();
        request.setFirstName("New");
        request.setLastName("Owner");
        request.setDocument("12345679");
        request.setPhone("+573001234568");
        request.setEmail("newowner@test.com");
        request.setPassword("newowner123");
        request.setBirthDate(LocalDate.of(1985, 6, 15));
        return request;
    }

    public static UserRequestDto createInvalidOwnerRequest() {
        UserRequestDto request = new UserRequestDto();
        request.setFirstName(""); // Invalid empty name
        request.setLastName("Owner");
        request.setDocument("123"); // Too short
        request.setPhone("invalid-phone");
        request.setEmail("invalid-email");
        request.setPassword("123"); // Too short
        request.setBirthDate(LocalDate.of(2010, 1, 1)); // Too young
        return request;
    }

    public static UserEmployeeRequestDto createValidEmployeeRequestDto() {
        UserEmployeeRequestDto request = new UserEmployeeRequestDto();
        request.setFirstName("New");
        request.setLastName("Employee");
        request.setDocument("55443322");
        request.setPhone("+573005544332");
        request.setEmail("newemployee@test.com");
        request.setPassword("employee123");
        request.setBirthDate(LocalDate.of(1988, 11, 12));
        request.setRestaurantId(1L);
        return request;
    }

    public static UserRequestDto createValidCustomerRequestDto() {
        UserRequestDto request = new UserRequestDto();
        request.setFirstName("New");
        request.setLastName("Customer");
        request.setDocument("98765432");
        request.setPhone("+573009876543");
        request.setEmail("newcustomer@test.com");
        request.setPassword("customer123");
        request.setBirthDate(LocalDate.of(1990, 8, 20));
        return request;
    }

    public static UserResponseDto createOwnerResponseDto() {
        UserResponseDto response = new UserResponseDto();
        response.setId(2L);
        response.setFirstName("Restaurant");
        response.setLastName("Owner");
        response.setEmail("owner@test.com");
        response.setPhone("+573009876543");
        response.setRole("OWNER");
        response.setActive(true);
        return response;
    }

    public static UserResponseDto createEmployeeResponseDto() {
        UserResponseDto response = new UserResponseDto();
        response.setId(3L);
        response.setFirstName("Employee");
        response.setLastName("User");
        response.setEmail("employee@test.com");
        response.setPhone("+573005566778");
        response.setRole("EMPLOYEE");
        response.setActive(true);
        return response;
    }

    public static UserResponseDto createCustomerResponseDto() {
        UserResponseDto response = new UserResponseDto();
        response.setId(4L);
        response.setFirstName("Customer");
        response.setLastName("User");
        response.setEmail("customer@test.com");
        response.setPhone("+573002233445");
        response.setRole("CUSTOMER");
        response.setActive(true);
        return response;
    }

    public static AuthResponseDto createAdminAuthResponse() {
        AuthResponseDto response = new AuthResponseDto();
        response.setToken("jwt-admin-token-12345");
        response.setUserId(1L);
        response.setRole("ADMIN");
        response.setExpiresIn(86400000L);
        return response;
    }

    public static AuthResponseDto createOwnerAuthResponse() {
        AuthResponseDto response = new AuthResponseDto();
        response.setToken("jwt-owner-token-12345");
        response.setUserId(2L);
        response.setRole("OWNER");
        response.setExpiresIn(86400000L);
        return response;
    }

    public static AuthResponseDto createCustomerAuthResponse() {
        AuthResponseDto response = new AuthResponseDto();
        response.setToken("jwt-customer-token-12345");
        response.setUserId(4L);
        response.setRole("CUSTOMER");
        response.setExpiresIn(86400000L);
        return response;
    }

    // =================================
    // Invalid/Edge Cases Factory Methods
    // =================================

    public static UserModel createUserWithInvalidEmail() {
        UserModel user = createValidCustomerUser();
        user.setEmail("invalid-email-format");
        return user;
    }

    public static UserModel createUserWithNullEmail() {
        UserModel user = createValidCustomerUser();
        user.setEmail(null);
        return user;
    }

    public static UserModel createUserWithEmptyName() {
        UserModel user = createValidCustomerUser();
        user.setFirstName("");
        user.setLastName("");
        return user;
    }

    public static UserModel createUserWithInvalidPhone() {
        UserModel user = createValidCustomerUser();
        user.setPhone("invalid-phone");
        return user;
    }

    // =================================
    // Domain Model Factory Methods for Requests
    // =================================

    public static UserModel createValidOwnerRequestModel() {
        UserModel user = new UserModel();
        user.setFirstName("New");
        user.setLastName("Owner");
        user.setDocument("12345679");
        user.setPhone("+573001234568");
        user.setBirthDate(LocalDate.of(1985, 6, 15));
        user.setEmail("newowner@test.com");
        user.setPassword("newowner123");
        return user;
    }

    public static UserModel createValidCustomerRequestModel() {
        UserModel user = new UserModel();
        user.setFirstName("New");
        user.setLastName("Customer");
        user.setDocument("98765432");
        user.setPhone("+573009876543");
        user.setBirthDate(LocalDate.of(1990, 8, 20));
        user.setEmail("newcustomer@test.com");
        user.setPassword("customer123");
        return user;
    }

    public static UserModel createValidEmployeeRequestModel() {
        UserModel user = new UserModel();
        user.setFirstName("New");
        user.setLastName("Employee");
        user.setDocument("55443322");
        user.setPhone("+573005544332");
        user.setBirthDate(LocalDate.of(1988, 11, 12));
        user.setEmail("newemployee@test.com");
        user.setPassword("employee123");
        return user;
    }
}
