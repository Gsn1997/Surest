package com.surest_member_managemant.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        SecurityContextHolder.clearContext(); // clear previous authentication
    }

    // ---------------------- Valid Token ----------------------
    @Test
    void doFilterInternalValidTokenSetsAuthentication() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.getUsername("valid-token")).thenReturn("admin");
        when(jwtUtil.getRoles("valid-token")).thenReturn(List.of("ROLE_ADMIN"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("admin", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(filterChain).doFilter(request, response);
    }

    // ---------------------- Invalid Token ----------------------
    @Test
    void doFilterInternalInvalidTokenReturnsUnauthorized() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String json = responseWriter.toString();
        assertTrue(json.contains("Token is invalid or expired"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    // ---------------------- Missing Token ----------------------
    @Test
    void doFilterInternalNoTokenCallsFilterChain() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }
}
