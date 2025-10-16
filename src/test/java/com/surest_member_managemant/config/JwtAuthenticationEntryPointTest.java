package com.surest_member_managemant.config;


import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationEntryPointTest {

    private JwtAuthenticationEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testCommenceReturns401() throws IOException {
        // Arrange
        AuthenticationException authException = new AuthenticationException("Unauthorized access") {};

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(), "Response status should be 401");
        String responseMessage = response.getErrorMessage();
        assertEquals("Unauthorized", responseMessage, "Response message should be 'Unauthorized'");
    }

}
