package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionController sessionController;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(1L);
        session.setName("Java Basics");
        session.setDescription("Introduction to Java");

        sessionDto = new SessionDto();
        sessionDto.setName("Java Basics");
        sessionDto.setDescription("Introduction to Java");
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.findById("1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testCreate_Success() {
        // Arrange
        when(sessionMapper.toEntity(sessionDto)).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.create(sessionDto);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        Session sessionToUpdate = new Session();
        sessionToUpdate.setId(1L);
        sessionToUpdate.setName("Java Basics Updated");
        sessionToUpdate.setDescription("Updated description");

        when(sessionMapper.toEntity(sessionDto)).thenReturn(sessionToUpdate);
        when(sessionService.update(1L, sessionToUpdate)).thenReturn(sessionToUpdate);
        when(sessionMapper.toDto(sessionToUpdate)).thenReturn(sessionDto);

        // Act
        ResponseEntity<?> response = sessionController.update("1", sessionDto);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(sessionDto);
    }
    @Test
    void testDelete_Success() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(session);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(sessionService.getById(1L)).thenReturn(null);

        // Act
        ResponseEntity<?> response = sessionController.save("1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testParticipate_Success() {
        // Arrange
        doNothing().when(sessionService).participate(1L, 1L);

        // Act
        ResponseEntity<?> response = sessionController.participate("1", "1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testNoLongerParticipate_Success() {
        // Arrange
        doNothing().when(sessionService).noLongerParticipate(1L, 1L);

        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate("1", "1");

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        when(sessionService.findAll()).thenReturn(Arrays.asList(session));
        when(sessionMapper.toDto(Arrays.asList(session))).thenReturn(Arrays.asList(sessionDto));

        // Act
        ResponseEntity<?> response = sessionController.findAll();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(Arrays.asList(sessionDto));
    }

    @Test
    void testFindById_BadRequest() {
        // Act
        ResponseEntity<?> response = sessionController.findById("abc");
        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);  // Attends une erreur 400 - Bad Request
    }

    @Test
    void testUpdate_BadRequest() {
        // Arrange
        // On simulate un mauvais format d'ID (non numérique)
        String invalidId = "abc";

        // Act
        ResponseEntity<?> response = sessionController.update(invalidId, sessionDto);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);  // Le contrôleur doit renvoyer une erreur 400
    }

    @Test
    void testDelete_BadRequest() {
        // Arrange
        // On simulate un mauvais format d'ID (non numérique)
        String invalidId = "xyz";

        // Act
        ResponseEntity<?> response = sessionController.save(invalidId);  // La méthode delete est appelée 'save' dans le test

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);  // Attends une réponse 400
    }

    @Test
    void testParticipate_BadRequest() {
        // Arrange
        String invalidId = "abc";  // ID invalide

        // Act
        ResponseEntity<?> response = sessionController.participate(invalidId, "1");  // Utilisation d'un ID invalide

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);  // Attendre une réponse 400 - Bad Request
    }

    @Test
    void testNoLongerParticipate_BadRequest() {
        // Arrange
        String invalidId = "xyz";  // ID invalide

        // Act
        ResponseEntity<?> response = sessionController.noLongerParticipate(invalidId, "1");  // Utilisation d'un ID invalide

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);  // Attendre une réponse 400 - Bad Request
    }
}