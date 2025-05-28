package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    @Test
    void testFindById_whenTeacherExists_shouldReturnOk() {
        // GIVEN
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");

        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setFirstName("John");
        teacherDto.setLastName("Doe");

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // WHEN
        ResponseEntity<?> response = teacherController.findById("1");

        // THEN
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(teacherDto);
    }

    @Test
    void testFindById_whenTeacherNotFound_shouldReturnNotFound() {
        when(teacherService.findById(999L)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById("999");

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    void testFindById_whenInvalidId_shouldReturnBadRequest() {
        ResponseEntity<?> response = teacherController.findById("abc");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    void testFindAll_shouldReturnListOfTeachers() {
        // GIVEN
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");

        List<Teacher> teachers = Collections.singletonList(teacher1);

        TeacherDto teacherDto1 = new TeacherDto();
        teacherDto1.setId(1L);
        teacherDto1.setFirstName("John");
        teacherDto1.setLastName("Doe");

        List<TeacherDto> teacherDtos = Collections.singletonList(teacherDto1);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        // WHEN
        ResponseEntity<?> response = teacherController.findAll();

        // THEN
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(teacherDtos);
    }
}