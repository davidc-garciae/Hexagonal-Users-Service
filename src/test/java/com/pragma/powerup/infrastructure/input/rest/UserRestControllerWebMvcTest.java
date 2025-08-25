package com.pragma.powerup.infrastructure.input.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.UserRequestDto;
import com.pragma.powerup.application.handler.IUserHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserRestControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @org.springframework.boot.test.context.TestConfiguration
  static class TestConfig {
    @Bean
    IUserHandler userHandler() {
      return Mockito.mock(IUserHandler.class);
    }
  }

  @Test
  @DisplayName("POST /api/v1/users/owner returns 201 when valid and ADMIN role")
  @WithMockUser(roles = { "ADMIN" })
  void createOwnerCreated() throws Exception {
    UserRequestDto req = new UserRequestDto();
    req.setFirstName("John");
    req.setLastName("Doe");
    req.setDocument("1002003004");
    req.setPhone("+573001112233");
    req.setEmail("john.doe@example.com");
    req.setPassword("secret");
    req.setBirthDate(java.time.LocalDate.of(1990, 1, 1));

    mockMvc
        .perform(
            post("/api/v1/users/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("POST /api/v1/users/employee returns 201 when valid and OWNER role")
  @WithMockUser(roles = { "OWNER" })
  void createEmployeeCreated() throws Exception {
    var req = new com.pragma.powerup.application.dto.request.UserEmployeeRequestDto();
    req.setFirstName("Jane");
    req.setLastName("Smith");
    req.setDocument("99887766");
    req.setPhone("+573114445566");
    req.setBirthDate(java.time.LocalDate.of(1990, 6, 15));
    req.setEmail("jane.smith@example.com");
    req.setPassword("secret");
    req.setRestaurantId(10L);

    mockMvc
        .perform(
            post("/api/v1/users/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated());
  }
}
