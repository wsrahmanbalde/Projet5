package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("jane.doe@example.com");
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setPassword("password");
        testUser.setAdmin(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUser = userRepository.save(testUser);
    }

    @Test
    @WithMockUser(username = "yoga@studio.com", roles = "test!1234")
    void testFindUserById_shouldReturnUserDtoWithoutPassword() throws Exception {
        mockMvc.perform(get("/api/user/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.admin").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com", roles = "test!1234")
    void testFindById_shouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com", roles = "test!1234")
    void testFindById_shouldReturnBadRequestIfInvalidId() throws Exception {
        mockMvc.perform(get("/api/user/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com", roles = "test!1234")
    void testFindUserById_withInvalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/user/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "jane.doe@example.com")
    void testDeleteUserById_withValidAuth_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "unauthorized@example.com")
    void testDeleteUserById_withWrongUser_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteUserById_withoutAuthentication_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "yoga@studio.com", roles = "test!1234")
    void testDeleteUserById_withInvalidId_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/user/not-a-number"))
                .andExpect(status().isBadRequest());
    }
}