package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FacultyServiceTest {

    private final FacultyService out = new FacultyService();
    private final Faculty testFaculty = new Faculty(1L, "Test", "white");
    private final Faculty testFaculty2 = new Faculty(2L, "Test_2", "black");

    @Test
    public void whenCreateFacultyShouldReturnFaculty() {
        assertEquals(testFaculty, out.createFaculty(testFaculty));
    }

    @Test
    public void whenFindFacultyShouldReturnFaculty() throws FacultyNotFoundException {
        out.createFaculty(testFaculty);
        assertEquals(testFaculty, out.findFaculty(1L));
    }

    @Test
    public void whenFindIncorrectFacultyShouldThrowException() {
        assertThrows(FacultyNotFoundException.class, () -> out.findFaculty(2L));
    }

    @Test
    public void whenDeleteFacultyShouldReturnDeletedFaculty() throws FacultyNotFoundException {
        out.createFaculty(testFaculty);
        assertEquals(testFaculty, out.deleteFaculty(1L));
    }

    @Test
    public void whenDeleteFacultyShouldReturnEmptyMap() throws FacultyNotFoundException {
        out.createFaculty(testFaculty);
        out.deleteFaculty(1L);
        assertTrue(out.getFaculties().isEmpty());
    }

    @Test
    public void whenDeleteIncorrectFacultyShouldThrowException() {
        assertThrows(FacultyNotFoundException.class, () -> out.deleteFaculty(5L));
    }

    @Test
    public void whenEditFacultyShouldReturnStudent() throws FacultyNotFoundException {
        out.createFaculty(testFaculty);
        assertEquals(testFaculty2, out.editFaculty(testFaculty2));
    }

    @Test
    public void whenGetAllFacultiesShouldReturnMap() {
        out.createFaculty(testFaculty);
        out.createFaculty(testFaculty2);
        Map<Long, Faculty> expected = Map.of(1L, testFaculty, 2L, testFaculty2);
        assertEquals(expected, out.getFaculties());
    }
}