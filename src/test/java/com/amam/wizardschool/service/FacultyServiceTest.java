package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.repository.FacultyRepository;
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
class FacultyServiceTest {

    private FacultyService out;

    // Замокал, т.к. у нас разные БД и при тестировании с другой БД будет кидать ошибки
    @Mock
    private FacultyRepository facultyRepositoryMock;
    private final Faculty faculty1 = new Faculty();
    private final Faculty faculty2 = new Faculty();


    @BeforeEach
    public void setUp() {
        out = new FacultyService(facultyRepositoryMock);

        faculty1.setId(1L);
        faculty1.setName("AAA");
        faculty1.setColor("Red");

        faculty2.setId(1L);
        faculty2.setName("BBB");
        faculty2.setColor("Blue");

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
        when(facultyRepositoryMock.getFacultyByColor("Red")).thenReturn(List.of(faculty1));
        assertEquals(out.getFacultyByColor("Red"), List.of(faculty1));
    }

    @Test
    public void whenFindFacultiesByColorShouldReturnEmptyCollection() {
        when(facultyRepositoryMock.getFacultyByColor("Green")).thenReturn(List.of());
        assertEquals(out.getFacultyByColor("Green"), List.of());
    }

    @Test
    public void whenDeleteFacultyShouldThrowException() {
        when(facultyRepositoryMock.findById(1L)).thenReturn(Optional.empty());
        assertThrows(FacultyNotFoundException.class, () -> out.deleteFaculty(1L));
    }


}