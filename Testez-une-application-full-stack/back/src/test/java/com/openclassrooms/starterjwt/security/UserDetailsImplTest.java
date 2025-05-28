package com.openclassrooms.starterjwt.security;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) // Pour intégrer Mockito avec JUnit 5
class UserDetailsImplTest {

    @InjectMocks
    private UserDetailsImpl userDetails;

    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser@email.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final Boolean IS_ADMIN = true;
    private static final String PASSWORD = "testpassword";

    @BeforeEach
    void setUp() {
        // Crée un utilisateur de test avec des valeurs constantes
        userDetails = UserDetailsImpl.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .admin(IS_ADMIN)
                .password(PASSWORD)
                .build();
    }

    @Test
    void testGetAuthorities() {
        // Arrange: Aucun setup nécessaire car la méthode est simple et retourne une collection vide.

        // Act
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert: Vérifier que la méthode retourne une collection vide.
        assertNotNull(authorities, "Authorities should not be null.");
        assertTrue(authorities.isEmpty(), "Authorities should be empty.");
    }

    @Test
    void testIsAccountNonExpired() {
        // Vérifier que le compte n'est pas expiré
        assertTrue(userDetails.isAccountNonExpired(), "Account should not be expired.");
    }

    @Test
    void testIsAccountNonLocked() {
        // Vérifier que le compte n'est pas verrouillé
        assertTrue(userDetails.isAccountNonLocked(), "Account should not be locked.");
    }

    @Test
    void testIsCredentialsNonExpired() {
        // Vérifier que les identifiants ne sont pas expirés
        assertTrue(userDetails.isCredentialsNonExpired(), "Credentials should not be expired.");
    }

    @Test
    void testIsEnabled() {
        // Vérifier que l'utilisateur est activé
        assertTrue(userDetails.isEnabled(), "User should be enabled.");
    }

    @Test
    void testEquals_Success() {
        // Arrange: Création d'un autre objet UserDetailsImpl avec le même id
        UserDetailsImpl otherUserDetails = UserDetailsImpl.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .admin(IS_ADMIN)
                .password(PASSWORD)
                .build();

        // Act & Assert: Vérifier que les deux objets avec le même id sont égaux.
        assertEquals(userDetails, otherUserDetails, "UserDetails objects with the same id should be equal.");
    }

    @Test
    void testEquals_Failure() {
        // Arrange: Création d'un autre objet UserDetailsImpl avec un id différent
        UserDetailsImpl otherUserDetails = UserDetailsImpl.builder()
                .id(2L) // Id différent
                .username("anotheruser@email.com")
                .firstName("Jane")
                .lastName("Doe")
                .admin(false)
                .password("differentpassword")
                .build();

        // Act & Assert: Vérifier que les deux objets avec des id différents ne sont pas égaux.
        assertNotEquals(userDetails, otherUserDetails, "UserDetails objects with different ids should not be equal.");
    }

    @Test
    void testEquals_WithNull() {
        // Act & Assert: Comparer avec null doit retourner false
        assertNotEquals(userDetails, null, "Comparing with null should return false.");
    }

    @Test
    void testEquals_WithDifferentClass() {
        // Act & Assert: Comparer avec un objet d'une autre classe doit retourner false
        assertNotEquals(userDetails, "Some string", "Comparing with a different class should return false.");
    }

    @Test
    void testEquals_SameObject() {
        // Act & Assert: Comparer l'objet avec lui-même doit retourner true
        assertEquals(userDetails, userDetails, "An object should be equal to itself.");
    }

    @Test
    void testGetAdmin() {
        // Arrange: Créer un objet UserDetailsImpl avec un admin défini
        userDetails = UserDetailsImpl.builder()
                .id(USER_ID)
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .admin(IS_ADMIN) // admin est défini ici
                .password(PASSWORD)
                .build();

        // Act: Appeler la méthode getAdmin()
        Boolean adminValue = userDetails.getAdmin();

        // Assert: Vérifier que la valeur retournée par getAdmin est correcte
        assertEquals(IS_ADMIN, adminValue, "Admin value should match.");
    }

}