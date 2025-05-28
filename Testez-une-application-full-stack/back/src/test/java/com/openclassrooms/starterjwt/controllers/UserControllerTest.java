package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    void testFindById_UserFound() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setEmail("test@example.com");

        when(userService.findById(id)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById(String.valueOf(id));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.findById("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testFindById_BadRequest() {
        ResponseEntity<?> response = userController.findById("abc");

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testDelete_UserFoundAndAuthorized() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findById(id)).thenReturn(user);

        ResponseEntity<?> response = userController.save(String.valueOf(id));

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).delete(id);
    }

    @Test
    void testDelete_UserNotAuthorized() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("other@example.com");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findById(id)).thenReturn(user);

        ResponseEntity<?> response = userController.save(String.valueOf(id));

        assertEquals(401, response.getStatusCodeValue());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    void testDelete_UserNotFound() {
        when(userService.findById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.save("1");

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDelete_BadRequest() {
        ResponseEntity<?> response = userController.save("abc");

        assertEquals(400, response.getStatusCodeValue());
    }
}