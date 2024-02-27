package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Student;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService out = new StudentService();
    private Student testStudent = new Student(1L, "John", 15);
    private Student testStudent2 = new Student(1L, "Anton", 17);

    @Test
    public void whenCreateStudentShouldReturnStudent() {
        assertEquals(testStudent, out.createStudent(testStudent));
    }

    @Test
    public void whenFindStudentShouldReturnStudent() throws StudentNotFoundException {
        out.createStudent(testStudent);
        assertEquals(testStudent, out.findStudent(1L));
    }

    @Test
    public void whenFindIncorrectStudentShouldThrowException() {
        assertThrows(StudentNotFoundException.class, () -> out.findStudent(2L));
    }

    @Test
    public void whenDeleteStudentShouldReturnDeletedStudent() throws StudentNotFoundException {
        out.createStudent(testStudent);
        assertEquals(testStudent, out.deleteStudent(1L));
    }

    @Test
    public void whenDeleteStudentShouldReturnEmptyMap() throws StudentNotFoundException {
        out.createStudent(testStudent);
        out.deleteStudent(1L);
        assertTrue(out.getStudents().isEmpty());
    }

    @Test
    public void whenDeleteIncorrectStudentShouldThrowException() {
        assertThrows(StudentNotFoundException.class, () -> out.deleteStudent(5L));
    }

    @Test
    public void whenEditStudentShouldReturnStudent() throws StudentNotFoundException {
        out.createStudent(testStudent);
        assertEquals(testStudent2, out.editStudent(testStudent2));
    }

    @Test
    public void whenGetAllStudentsShouldReturnMap() {
        out.createStudent(testStudent);
        out.createStudent(testStudent2);
        Map<Long, Student> expected = Map.of(1L, testStudent, 2L, testStudent2);
        assertEquals(expected, out.getStudents());
    }

    @Test
    public void whenFilterByAgeShouldThrowException() {
        out.createStudent(testStudent);
        out.createStudent(testStudent2);
        assertThrows(StudentNotFoundException.class, () -> out.getStudentsByAge(21));
    }

    @Test
    public void whenFilterByAgeShouldReturnCollection() throws StudentNotFoundException {
        out.createStudent(testStudent);
        out.createStudent(testStudent2);
        Collection<Student> expected = List.of(testStudent);
        assertEquals(expected, out.getStudentsByAge(15));
    }


}