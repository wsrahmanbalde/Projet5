package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void testCreateSession() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertEquals(session, result);
        verify(sessionRepository).save(session);
    }


    @Test
    void testDeleteSession() {
        Long sessionId = 1L;

        sessionService.delete(sessionId);

        verify(sessionRepository).deleteById(sessionId);
    }

    @Test
    void testFindAllSessions() {
        Session session1 = new Session();
        Session session2 = new Session();
        when(sessionRepository.findAll()).thenReturn(Arrays.asList(session1, session2));

        List<Session> sessions = sessionService.findAll();

        assertEquals(2, sessions.size());
        verify(sessionRepository).findAll();
    }

    @Test
    void testGetByIdFound() {
        Session session = new Session();
        session.setId(1L);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetByIdNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(1L);

        assertNull(result);
    }

    @Test
    void testUpdateSession() {
        Session session = new Session();
        session.setId(null);

        Session updatedSession = new Session();
        updatedSession.setId(1L);

        when(sessionRepository.save(any(Session.class))).thenReturn(updatedSession);

        Session result = sessionService.update(1L, session);

        assertEquals(1L, result.getId());
        verify(sessionRepository).save(session);
    }

    @Test
    void testParticipateSuccess() {
        // Arrange
        User user = new User();
        user.setId(2L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>()); // Aucun utilisateur inscrit

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(sessionRepository.save(session)).thenReturn(session);

        // Act
        assertDoesNotThrow(() -> sessionService.participate(1L, 2L));

        // Assert
        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository).save(session);
    }

    @Test
    void testParticipateNotFoundException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        // Vérifier que l'exception est bien lancée
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateBadRequestException() {
        Session session = new Session();
        session.setId(1L);
        User user = new User();
        user.setId(2L);
        session.setUsers(Collections.singletonList(user));  // L'utilisateur est déjà dans la session

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // Vérifier que l'exception est bien lancée
        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateUserNotFound() {
        Session session = new Session();
        session.setId(1L);
        session.setUsers(Collections.emptyList());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testParticipateAlreadyParticipating() {
        User user = new User();
        user.setId(2L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(List.of(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
    }

    @Test
    void testNoLongerParticipateSuccess() {
        User user = new User();
        user.setId(2L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(List.of(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenReturn(session);

        assertDoesNotThrow(() -> sessionService.noLongerParticipate(1L, 2L));
    }

    @Test
    void testNoLongerParticipateSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 2L));
    }

    @Test
    void testNoLongerParticipateUserNotInSession() {
        User user = new User();
        user.setId(3L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(List.of(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 2L));
    }

    @Test
    void testNoLongerParticipate_UserRemoved_OthersRemain() {
        User user1 = new User();
        user1.setId(2L);
        User user2 = new User();
        user2.setId(3L);

        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>(List.of(user1, user2)));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenReturn(session);

        sessionService.noLongerParticipate(1L, 2L);

        // Vérifie que seul user2 reste
        assertEquals(1, session.getUsers().size());
        assertEquals(3L, session.getUsers().get(0).getId());
    }

}