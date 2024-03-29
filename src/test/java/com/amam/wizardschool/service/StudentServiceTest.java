package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.rmi.StubNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    private StudentService out;

    @Mock
    private StudentRepository studentRepositoryMock;
    private final Student student1 = new Student();
    private final Student student2 = new Student();
    private final Faculty faculty = new Faculty();


    @BeforeEach
    public void setUp() {
        out = new StudentService(studentRepositoryMock);

        student1.setId(1L);
        student1.setAge(15);
        student1.setName("AAA");
        student1.setFaculty(faculty);

        student2.setId(1L);
        student2.setAge(14);
        student2.setName("BBB");

        faculty.setColor("Red");
        faculty.setName("AAA");
        faculty.setId(1L);

    }

    @Test
    public void whenCreateStudentShouldReturnStudent() throws FacultyNotFoundException {
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
        when(studentRepositoryMock.findByAge(15)).thenReturn(List.of(student1));
        assertEquals(out.getStudentsByAge(15), List.of(student1));
    }

    @Test
    public void whenFindStudentsByAgeShouldReturnEmptyCollection() {
        when(studentRepositoryMock.findByAge(20)).thenReturn(List.of());
        assertEquals(out.getStudentsByAge(20), List.of());
    }

    @Test
    public void whenDeleteStudentShouldThrowException() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.deleteStudent(1L));
    }

    @Test
    public void whenGetStudentsBetween10And15ShouldReturnCollection() {
        when(studentRepositoryMock.findByAgeBetween(10, 15)).thenReturn(List.of(student2));
        assertEquals(out.getStudentsByAgeBetween(10, 15), List.of(student2));
    }

    @Test
    public void whenGetStudentFacultyShouldReturnFaculty() throws StudentNotFoundException {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.of(student1));
        assertEquals(out.getStudentFaculty(1L), faculty);
    }

    @Test
    public void whenGetStudentFacultyShouldThrowException() {
        when(studentRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(StudentNotFoundException.class, () -> out.getStudentFaculty(1L));
    }

}