package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    public void setUp() {
        teacher1 = Teacher.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        teacher2 = Teacher.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testFindAll() {
        // Arrange
        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

        // Act
        List<Teacher> teachers = teacherService.findAll();

        // Assert
        assertNotNull(teachers);
        assertEquals(2, teachers.size());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    public void testFindById_whenTeacherExists() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher1));

        // Act
        Teacher teacher = teacherService.findById(1L);

        // Assert
        assertNotNull(teacher);
        assertEquals("John", teacher.getFirstName());
        assertEquals("Doe", teacher.getLastName());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindById_whenTeacherDoesNotExist() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Teacher teacher = teacherService.findById(1L);

        // Assert
        assertNull(teacher);
        verify(teacherRepository, times(1)).findById(1L);
    }
}