package com.surest_member_managemant.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secretKey;

    @BeforeEach
    void setUp() {
        secretKey = "MySuperSecretKeyForJwtTesting1234567890"; // 32+ chars for HS256
        long expirationMs = 1000 * 60 * 60; // 1 hour
        jwtUtil = new JwtUtil(secretKey, expirationMs);
    }

    @Test
    void testGenerateTokenNotNullOrEmpty() {
        String token = jwtUtil.generateToken("admin", List.of("ROLE_ADMIN"));

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
    }

    @Test
    void testGenerateTokenSubjectAndRoles() {
        String username = "admin";
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");

        String token = jwtUtil.generateToken(username, roles);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject(), "Username should match");
        assertEquals(roles, claims.get("roles"), "Roles should match");
    }

    @Test
    void testGenerateTokenExpirationAndIssuedAt() {
        String token = jwtUtil.generateToken("user", List.of("ROLE_USER"));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date now = new Date();
        assertTrue(claims.getIssuedAt().before(new Date(now.getTime() + 1000)), "IssuedAt should be before now+1s");
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()), "Expiration should be after issuedAt");
    }

    @Test
    void testValidateTokenValidAndInvalid() {
        String token = jwtUtil.generateToken("user", List.of("ROLE_USER"));

        assertTrue(jwtUtil.validateToken(token), "Token should be valid");

        String invalidToken = token + "invalid";
        assertFalse(jwtUtil.validateToken(invalidToken), "Token should be invalid");
    }

    @Test
    void testGetUsername() {
        String username = "admin";
        String token = jwtUtil.generateToken(username, List.of("ROLE_ADMIN"));

        String extractedUsername = jwtUtil.getUsername(token);
        assertEquals(username, extractedUsername, "Username extracted from token should match");
    }

    @Test
    void testGetRoles() {
        List<String> roles = List.of("ROLE_ADMIN", "ROLE_USER");
        String token = jwtUtil.generateToken("admin", roles);

        List<String> extractedRoles = jwtUtil.getRoles(token);
        assertEquals(roles, extractedRoles, "Roles extracted from token should match");
    }
}
