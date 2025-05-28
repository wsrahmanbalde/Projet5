package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    private String jwtToken;

    @BeforeAll
    void setup() throws Exception {
        // 1. Créer un teacher avec ID 1 pour les tests
        teacherRepository.deleteAll(); // Nettoyer au cas où
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher = teacherRepository.save(teacher); // ID auto généré

        // 2. Authentification pour récupérer le token JWT
        String loginPayload = objectMapper.writeValueAsString(Map.of(
                "email", "yoga@studio.com",
                "password", "test!1234"
        ));

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        jwtToken = JsonPath.read(response, "$.token");
    }

    @Test
    void testFindTeacherById_shouldReturnTeacher() throws Exception {
        // Récupérer le teacher créé dans la base (ici le premier)
        Teacher teacher = teacherRepository.findAll().get(0);

        mockMvc.perform(get("/api/teacher/" + teacher.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // utile pour debug en cas d'erreur
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teacher.getId()))
                .andExpect(jsonPath("$.firstName").value(teacher.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(teacher.getLastName()));
    }

    @Test
    void testFindTeacherById_shouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/999999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindTeacherById_shouldReturn400IfInvalidId() throws Exception {
        mockMvc.perform(get("/api/teacher/abc")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFindAllTeachers_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/teacher")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}