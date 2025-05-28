package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Pour intégrer Mockito avec JUnit 5
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private static final String USERNAME = "testuser@email.com";
    private static final String PASSWORD = "password";
    private static final Long USER_ID = 1L;

    private User user;

    @BeforeEach
    void setUp() {
        // Préparer l'utilisateur pour les tests
        user = new User();
        user.setId(USER_ID);
        user.setEmail(USERNAME);
        user.setPassword(PASSWORD);
        user.setFirstName("John");
        user.setLastName("Doe");
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        // Arrange: Simuler le comportement du UserRepository
        when(userRepository.findByEmail(USERNAME)).thenReturn(java.util.Optional.of(user));

        // Act: Appeler la méthode de service
        UserDetails userDetails = userDetailsService.loadUserByUsername(USERNAME);

        // Assert: Vérifier les résultats
        assertNotNull(userDetails, "UserDetails should not be null.");
        assertEquals(USERNAME, userDetails.getUsername(), "Username should match.");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange: Simuler le comportement du UserRepository pour ne pas trouver l'utilisateur
        when(userRepository.findByEmail(USERNAME)).thenReturn(java.util.Optional.empty());

        // Act & Assert: Vérifier que l'exception est lancée
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(USERNAME);
        }, "Expected UsernameNotFoundException to be thrown.");
    }
}