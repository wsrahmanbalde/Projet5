package com.openclassrooms.starterjwt.Services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TeacherServiceIT {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacher1;
    private Teacher teacher2;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();

        teacher1 = new Teacher();
        teacher1.setFirstName("Alice");
        teacher1.setLastName("Dupont");

        teacher2 = new Teacher();
        teacher2.setFirstName("Bob");
        teacher2.setLastName("Durand");

        teacherRepository.saveAll(List.of(teacher1, teacher2));
    }

    @Test
    void testFindAll_shouldReturnAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();
        assertThat(teachers).hasSize(2);
        assertThat(teachers).extracting("firstName")
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void testFindById_shouldReturnCorrectTeacher() {
        Teacher found = teacherService.findById(teacher1.getId());
        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void testFindById_shouldReturnNullIfNotFound() {
        Teacher found = teacherService.findById(999L); // ID inexistant
        assertThat(found).isNull();
    }
}
