package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // Pour rollback automatique entre les tests
class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        savedUser = userRepository.save(user);
    }

    @Test
    void testFindById_shouldReturnUser() {
        User found = userService.findById(savedUser.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void testDelete_shouldRemoveUser() {
        Long userId = savedUser.getId();
        userService.delete(userId);

        User found = userRepository.findById(userId).orElse(null);
        assertThat(found).isNull();
    }

    @Test
    void testFindById_shouldReturnNullIfNotFound() {
        User found = userService.findById(999L);
        assertThat(found).isNull();
    }
}
