package com.surest_member_managemant.controller;
import com.surest_member_managemant.dto.AuthRequest;
import com.surest_member_managemant.dto.AuthResponse;
import com.surest_member_managemant.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
 class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

     @BeforeEach
     void setUp() {
         MockitoAnnotations.openMocks(this);
         mockMvc = MockMvcBuilders.standaloneSetup(authController)
                 .setControllerAdvice(new com.surest_member_managemant.exception.ApiExceptionHandler())
                 .build();
         objectMapper = new ObjectMapper();
     }
    @Test
    void loginAdminSuccess() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin");
        request.setPassword("Admin@123");

        AuthResponse response = new AuthResponse();
        response.setToken("admin-token-123");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-token-123"));
    }

    @Test
    void loginUserSuccess() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("User@123");

        AuthResponse response = new AuthResponse();
        response.setToken("user-token-123");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("user-token-123"));
    }

    // ---------------------- Negative Tests ----------------------

    @Test
    void loginEmptyRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); }
     @Test
     void loginUserNotFound() throws Exception {
         AuthRequest request = new AuthRequest();
         request.setUsername("unknown");
         request.setPassword("password");

         when(authService.login(any(AuthRequest.class)))
                 .thenThrow(new com.surest_member_managemant.exception.NotFoundException("User not found"));

         mockMvc.perform(post("/auth/login")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isNotFound())
                 .andExpect(jsonPath("$.message").value("User not found"));
     }

}
