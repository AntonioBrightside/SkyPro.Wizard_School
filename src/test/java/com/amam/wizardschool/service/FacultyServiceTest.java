package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.FacultyRepository;
import com.amam.wizardschool.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    private FacultyService out;

    @Mock
    private FacultyRepository facultyRepositoryMock;

    @Mock
    private StudentRepository studentRepositoryMock;
    private final Faculty faculty1 = new Faculty();
    private final Faculty faculty2 = new Faculty();
    private final Student student = new Student();


    @BeforeEach
    public void setUp() {
        out = new FacultyService(facultyRepositoryMock, studentRepositoryMock);

        faculty1.setId(1L);
        faculty1.setName("AAA");
        faculty1.setColor("Red");

        faculty2.setId(1L);
        faculty2.setName("BBB");
        faculty2.setColor("Blue");

        student.setAge(15);
        student.setName("AAA");
        student.setId(1L);
        student.setFaculty(faculty1);
    }

    @Test
    public void whenCreateFacultyShouldReturnFaculty() {
        when(facultyRepositoryMock.save(faculty1)).thenReturn(faculty1);
        assertEquals(out.createFaculty(faculty1), faculty1);
    }

    @Test
    public void whenFindFacultyByIdShouldReturnOptionalFaculty() {
        when(facultyRepositoryMock.findById(1L)).thenReturn(Optional.of(faculty1));
        assertEquals(out.findFaculty(1L), Optional.of(faculty1));
    }

    @Test
    public void whenFindFacultyByIdShouldReturnEmptyOptional() {
        when(facultyRepositoryMock.findById(3L)).thenReturn(Optional.empty());
        assertEquals(out.findFaculty(3L), Optional.empty());
    }

    @Test
    public void whenEditFacultyShouldReturnOptionalFaculty() {
        when(facultyRepositoryMock.findById(1L)).thenReturn(Optional.of(faculty1));
        when(facultyRepositoryMock.save(faculty2)).thenReturn(faculty2);
        assertEquals(out.editFaculty(faculty2), Optional.of(faculty2));
    }

    @Test
    public void whenEditFacultyShouldReturnEmptyOptional() {
        when(facultyRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertEquals(out.editFaculty(faculty1), Optional.empty());
    }

    @Test
    public void whenGetAllFacultiesShouldReturnCollectionOfFaculties() {
        when(facultyRepositoryMock.findAll()).thenReturn(List.of(faculty1, faculty2));
        assertEquals(out.getFaculties(), List.of(faculty1, faculty2));
    }

    @Test
    public void whenFindFacultiesByColorShouldReturnCollectionOfFaculties() {
        when(facultyRepositoryMock.getFacultyByColorIgnoreCase("Red")).thenReturn(List.of(faculty1));
        assertEquals(out.getFacultyByColor("Red"), List.of(faculty1));
    }

    @Test
    public void whenFindFacultiesByColorShouldReturnEmptyCollection() {
        when(facultyRepositoryMock.getFacultyByColorIgnoreCase("Green")).thenReturn(List.of());
        assertEquals(out.getFacultyByColor("Green"), List.of());
    }

    @Test
    public void whenDeleteFacultyShouldThrowException() {
        when(facultyRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FacultyNotFoundException.class, () -> out.deleteFaculty(1L));
    }

    @Test
    public void whenGetFacultyByNameShouldReturnCorrectFaculty() {
        when(facultyRepositoryMock.findFacultyByNameIgnoreCaseOrColorIgnoreCase("AAA"))
                .thenReturn(List.of(faculty1));
        assertEquals(out.getFacultyByNameOrColorIgnoreCase("AAA"), List.of(faculty1));
    }

    @Test
    public void whenGetFacultyByColorShouldReturnCorrectFaculty() {
        when(facultyRepositoryMock.findFacultyByNameIgnoreCaseOrColorIgnoreCase("Blue"))
                .thenReturn(List.of(faculty2));
        assertEquals(out.getFacultyByNameOrColorIgnoreCase("Blue"), List.of(faculty2));
    }

    @Test
    public void whenGetStudentsInFacultyByFacultyIDShouldReturnCollectionOfStudents() {
        when(studentRepositoryMock.getStudentsByFaculty_id(1L)).thenReturn(List.of(student));
        assertEquals(out.getStudentsFromFaculty(1L), List.of(student));
    }



}