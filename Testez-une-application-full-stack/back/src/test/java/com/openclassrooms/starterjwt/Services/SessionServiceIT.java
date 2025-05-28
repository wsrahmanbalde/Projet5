package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class SessionServiceIT {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private static final AtomicInteger userCounter = new AtomicInteger(0);

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();

        // Créer un utilisateur
        user = new User();
        user.setEmail("test2@example.com");
        user.setLastName("Doe");
        user.setFirstName("John");
        user.setPassword("1234");
        user = userRepository.save(user);

        // Créer une session avec tous les champs obligatoires
        session = new Session();
        session.setName("Session de test");
        session.setDescription("Description de la session de test");  // OBLIGATOIRE
        session.setDate(new Date());                                 // OBLIGATOIRE
        session.setUsers(new ArrayList<>());
        session = sessionRepository.save(session);
    }

    @Test
    void testFindAll_shouldReturnAllSessions() {
        List<Session> result = sessionService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Session de test");
    }

    @Test
    void testGetById_shouldReturnSession() {
        Session result = sessionService.getById(session.getId());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Session de test");
    }

    @Test
    public void testCreate_shouldSaveSession() {
        // Création et sauvegarde d'un teacher valide
        Teacher teacher = Teacher.builder()
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        teacher = teacherRepository.save(teacher); // Persist le teacher

        // Création de la session avec les champs requis + le teacher
        Session session = Session.builder()
                .name("Session de test")
                .description("Une description valide pour la session")
                .date(new Date())  // date non null obligatoire
                .teacher(teacher)  // liaison avec teacher persistant
                .build();

        // Sauvegarde de la session
        Session savedSession = sessionRepository.save(session);

        // Vérification
        assertNotNull(savedSession.getId());
        assertEquals("Session de test", savedSession.getName());
        assertEquals(teacher.getId(), savedSession.getTeacher().getId());
    }

    @Test
    void testUpdate_shouldUpdateSession() {
        session.setName("Session modifiée");
        Session updated = sessionService.update(session.getId(), session);

        assertThat(updated.getName()).isEqualTo("Session modifiée");
    }

    @Test
    void testDelete_shouldRemoveSession() {
        sessionService.delete(session.getId());
        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    void testParticipate_shouldAddUserToSession() {
        sessionService.participate(session.getId(), user.getId());

        Session updated = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getUsers()).extracting("id").contains(user.getId());
    }

    @Test
    void testNoLongerParticipate_shouldRemoveUserFromSession() {
        // Ajouter l’utilisateur à la session d’abord
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        // Supprimer la participation
        sessionService.noLongerParticipate(session.getId(), user.getId());

        Session updated = sessionRepository.findById(session.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getUsers()).isEmpty();
    }

    @Test
    void testParticipate_shouldThrowIfUserAlreadyParticipating() {
        session.getUsers().add(user);
        session = sessionRepository.save(session);

        assertThatThrownBy(() -> sessionService.participate(session.getId(), user.getId()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void testNoLongerParticipate_shouldThrowIfUserNotParticipating() {
        assertThatThrownBy(() -> sessionService.noLongerParticipate(session.getId(), user.getId()))
                .isInstanceOf(BadRequestException.class);
    }
}