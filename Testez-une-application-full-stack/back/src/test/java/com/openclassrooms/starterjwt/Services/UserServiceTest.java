package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // Test for the findById method
    @Test
    public void testFindById_WhenUserExists() {
        // Given
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindById_WhenUserDoesNotExist() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        User result = userService.findById(userId);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    // Test for the delete method
    @Test
    public void testDelete() {
        // Given
        Long userId = 1L;

        // When
        userService.delete(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }
}