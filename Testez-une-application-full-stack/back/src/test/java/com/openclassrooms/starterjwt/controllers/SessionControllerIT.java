package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher savedTeacher;
    private String jwtToken;
    private User testUser;
    private Session savedSession;

    @BeforeEach
    void setup() throws Exception {
        testUser = userRepository.findByEmail("yoga2@studio.com").orElseGet(() -> {
            User user = new User();
            user.setEmail("yoga2@studio.com");
            user.setFirstName("Yoga");
            user.setLastName("Studio");
            user.setPassword("$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq");
            user.setAdmin(false);
            return userRepository.save(user);
        });

        String loginPayload = "{"
                + "\"email\":\"yoga2@studio.com\","
                + "\"password\":\"test!1234\""
                + "}";

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        jwtToken = objectMapper.readTree(response).get("token").asText();

        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        savedTeacher = teacherRepository.save(teacher);

        Session session = new Session();
        session.setName("Test Session");
        session.setDescription("Session de test");
        session.setDate(new Date());
        savedSession = sessionRepository.save(session);
    }

    @Test
    void testFindById_shouldReturnSession() throws Exception {
        mockMvc.perform(get("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Session"));
    }

    @Test
    void testFindAll_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreate_shouldReturnCreatedSession() throws Exception {
        String payload = "{"
                + "\"name\":\"Advanced Yoga\","
                + "\"description\":\"An advanced yoga session\","
                + "\"date\":\"2025-05-01T10:00:00.000+00:00\","
                + "\"teacher_id\":" + savedTeacher.getId()
                + "}";

        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Advanced Yoga"))
                .andExpect(jsonPath("$.description").value("An advanced yoga session"))
                .andExpect(jsonPath("$.teacher_id").value(savedTeacher.getId()));
    }

    @Test
    void testUpdate_shouldModifySession() throws Exception {
        String updatedSessionDto = "{"
                + "\"name\":\"Updated Session\","
                + "\"description\":\"Updated description\","
                + "\"date\":\"2025-04-10T10:00:00\","
                + "\"teacher_id\":" + savedTeacher.getId() + ","
                + "\"users\":[],"
                + "\"createdAt\":\"2025-04-10T10:00:00\","
                + "\"updatedAt\":\"2025-04-10T10:00:00\""
                + "}";

        mockMvc.perform(put("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedSessionDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Session"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void testDelete_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testParticipate_shouldReturnOk() throws Exception {
        // Création d'une session fraîche avec teacher lié et users initialisés
        Session session = new Session();
        session.setName("Session Participate");
        session.setDescription("Session test participation");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<>());
        Session freshSession = sessionRepository.save(session);

        // Appel API participation
        mockMvc.perform(post("/api/session/" + freshSession.getId() + "/participate/" + testUser.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testNoLongerParticipate_shouldReturnOk() throws Exception {
        // Création d'une session fraîche
        Session session = new Session();
        session.setName("Session NoMoreParticipate");
        session.setDescription("Session test désinscription");
        session.setDate(new Date());
        session.setTeacher(savedTeacher);
        session.setUsers(new ArrayList<User>());
        Session freshSession = sessionRepository.save(session);

        // Participation
        mockMvc.perform(post("/api/session/" + freshSession.getId() + "/participate/" + testUser.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        // Désinscription
        mockMvc.perform(delete("/api/session/" + freshSession.getId() + "/participate/" + testUser.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}