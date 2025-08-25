package com.pragma.powerup.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.powerup.application.dto.request.LoginRequestDto;
import com.pragma.powerup.application.dto.response.AuthResponseDto;
import com.pragma.powerup.application.handler.IAuthHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthRestControllerWebMvcTest.MockConfig.class)
class AuthRestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IAuthHandler authHandler;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public IAuthHandler authHandler() {
            return org.mockito.Mockito.mock(IAuthHandler.class);
        }
    }

    @Test
    @DisplayName("loginShouldReturnTokenWhenCredentialsValid")
    void loginShouldReturnTokenWhenCredentialsValid() throws Exception {
        var response = AuthResponseDto.builder()
                .token("jwt-token")
                .userId(1L)
                .role("CUSTOMER")
                .expiresIn(1000L)
                .build();
        when(authHandler.login(any())).thenReturn(response);

        var request = new LoginRequestDto();
        request.setEmail("user@test.com");
        request.setPassword("pass");

        mockMvc
                .perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}
