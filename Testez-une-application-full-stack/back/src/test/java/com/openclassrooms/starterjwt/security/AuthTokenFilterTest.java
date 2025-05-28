package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.jwt.AuthTokenFilter;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private final String jwtSecret = "testSecret";
    private final int jwtExpirationMs = 3600000;

    private String jwt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        jwt = Jwts.builder()
                .setSubject("testuser@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        request.addHeader("Authorization", "Bearer " + jwt);

        when(jwtUtils.validateJwtToken(jwt)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(jwt)).thenReturn("testuser@example.com");

        UserDetails mockUser = new User("testuser@example.com", "password", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("testuser@example.com")).thenReturn(mockUser);
    }

    @Test
    void testDoFilterInternal_validToken_authenticationSet() throws Exception {
        Method method = AuthTokenFilter.class.getDeclaredMethod("doFilterInternal",
                javax.servlet.http.HttpServletRequest.class,
                javax.servlet.http.HttpServletResponse.class,
                javax.servlet.FilterChain.class);
        method.setAccessible(true);

        method.invoke(authTokenFilter, request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser@example.com",
                SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_invalidToken_noAuthentication() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer invalidtoken");

        when(jwtUtils.validateJwtToken("invalidtoken")).thenReturn(false);

        Method method = AuthTokenFilter.class.getDeclaredMethod("doFilterInternal",
                javax.servlet.http.HttpServletRequest.class,
                javax.servlet.http.HttpServletResponse.class,
                javax.servlet.FilterChain.class);
        method.setAccessible(true);

        method.invoke(authTokenFilter, request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}