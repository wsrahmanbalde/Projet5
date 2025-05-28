package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register - should create user when input is valid")
    void shouldRegisterUser_whenValidInput() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("adou@gmail.com");
        request.setFirstName("aboud");
        request.setLastName("balde");
        request.setPassword("Password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 400 when email is already taken")
    void shouldReturnBadRequest_whenEmailExists() throws Exception {
        userRepository.save(new User("adou@gmail.com", "balde", "aboud", passwordEncoder.encode("Password123"), false));

        SignupRequest request = new SignupRequest();
        request.setEmail("adou@gmail.com");
        request.setFirstName("another");
        request.setLastName("user");
        request.setPassword("OtherPass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return token when credentials are correct")
    void shouldAuthenticateUser_whenCredentialsAreValid() throws Exception {
        userRepository.save(new User("adou@gmail.com", "balde", "aboud", passwordEncoder.encode("secret"), false));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("adou@gmail.com");
        loginRequest.setPassword("secret");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("adou@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("aboud"))
                .andExpect(jsonPath("$.lastName").value("balde"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 401 when credentials are invalid")
    void shouldReturnUnauthorized_whenCredentialsAreInvalid() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("invalide@gmail.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}