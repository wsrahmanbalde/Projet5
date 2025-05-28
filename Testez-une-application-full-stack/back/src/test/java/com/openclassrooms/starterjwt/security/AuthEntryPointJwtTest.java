package com.openclassrooms.starterjwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.jwt.AuthEntryPointJwt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthEntryPointJwtTest {

    private AuthEntryPointJwt authEntryPointJwt;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        authEntryPointJwt = new AuthEntryPointJwt();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void commence_shouldSetUnauthorizedResponseWithJsonBody() throws Exception {
        // Given
        request.setServletPath("/api/test");
        AuthenticationException exception = Mockito.mock(AuthenticationException.class);
        Mockito.when(exception.getMessage()).thenReturn("Access denied");

        // When
        authEntryPointJwt.commence(request, response, exception);

        // Then
        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = mapper.readValue(response.getContentAsByteArray(), Map.class);

        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Access denied", body.get("message"));
        assertEquals("/api/test", body.get("path"));
    }
}