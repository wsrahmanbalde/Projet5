package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String jwtSecret = "mySecretKey123456789";
    private final int jwtExpirationMs = 3600000; // 1 heure
    private final String username = "testuser@example.com";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void testGenerateJwtToken_shouldGenerateToken() {
        // Arrange
        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn(username);

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // Signature JWT
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGetUserNameFromJwtToken_shouldReturnUsername() {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateJwtToken_withValidToken_shouldReturnTrue() {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_withExpiredToken_shouldReturnFalse() {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                .setExpiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_withMalformedToken_shouldReturnFalse() {
        String malformedToken = "abc.def.ghi";
        assertFalse(jwtUtils.validateJwtToken(malformedToken));
    }

    @Test
    void testValidateJwtToken_withWrongSignature_shouldReturnFalse() {
        String tokenWithWrongKey = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(SignatureAlgorithm.HS512, "wrongKey")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(tokenWithWrongKey));
    }

    @Test
    void testValidateJwtToken_withEmptyToken_shouldReturnFalse() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }
}