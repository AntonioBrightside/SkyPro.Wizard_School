package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private StudentService out;

    // Замокал, т.к. у нас разные БД и при тестировании с другой БД будет кидать ошибки
    @Mock
    private StudentRepository studentRepositoryMock;
    private final Student student1 = new Student();
    private final Student student2 = new Student();


    @BeforeEach
    public void setUp() {
        out = new StudentService(studentRepositoryMock);

        student1.setId(1L);
        student1.setAge(15);
        student1.setName("AAA");

        student2.setId(1L);
        student2.setAge(14);
        student2.setName("BBB");

    }

    @Test
    public void whenCreateStudentShouldReturnStudent() {
        when(studentRepositoryMock.save(student1)).thenReturn(student1);
        assertEquals(out.createStudent(student1), student1);
    }

    @Test
    public void whenFindStudentByIdShouldReturnOptionalStudent() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.of(student1));
        assertEquals(out.findStudent(1L), Optional.of(student1));
    }

    @Test
    public void whenFindStudentByIdShouldReturnEmptyOptional() {
        when(studentRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        assertEquals(out.findStudent(3L), Optional.empty());
    }

    @Test
    public void whenEditStudentShouldReturnOptionalStudent() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepositoryMock.save(student2)).thenReturn(student2);
        assertEquals(out.editStudent(student2), Optional.of(student2));
    }

    @Test
    public void whenEditStudentShouldReturnEmptyOptional() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertEquals(out.editStudent(student1), Optional.empty());
    }

    @Test
    public void whenGetAllStudentsShouldReturnCollectionOfStudents() {
        when(studentRepositoryMock.findAll()).thenReturn(List.of(student1, student2));
        assertEquals(out.getStudents(), List.of(student1, student2));
    }

    @Test
    public void whenFindStudentsByAgeShouldReturnCollectionOfStudents() {
        when(studentRepositoryMock.findByAgeLike(15)).thenReturn(List.of(student1));
        assertEquals(out.getStudentsByAge(15), List.of(student1));
    }

    @Test
    public void whenFindStudentsByAgeShouldReturnEmptyCollection() {
        when(studentRepositoryMock.findByAgeLike(20)).thenReturn(List.of());
        assertEquals(out.getStudentsByAge(20), List.of());
    }

    @Test
    public void whenDeleteStudentShouldThrowException() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.deleteStudent(1L));
    }
}