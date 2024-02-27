package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FacultyService {
    private Long id = 0L;
    private final Map<Long, Faculty> faculties;

    public FacultyService() {
        this.faculties = new HashMap<>();
    }

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++id);
        faculties.put(id, faculty);
        return faculty;
    }

    public Faculty editFaculty(Faculty faculty) throws FacultyNotFoundException {
        if (faculties.get(faculty.getId()) == null) {
            throw new FacultyNotFoundException("Факультет не найден в базе");
        }

        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty findFaculty(Long id) throws FacultyNotFoundException {
        if (faculties.get(id) == null) {
            throw new FacultyNotFoundException("Факультет не найден в базе");
        }

        return faculties.get(id);
    }

    public Faculty deleteFaculty(Long id) throws FacultyNotFoundException {
        Faculty deletedFaculty = findFaculty(id);
        return faculties.remove(deletedFaculty.getId());
    }

    public Map<Long, Faculty> getFaculties() {
        return faculties;
    }

    public Collection<Faculty> getFacultyByColor(String color) throws FacultyNotFoundException {
        List<Faculty> collection =  faculties.values()
                .stream()
                .filter(faculty -> Objects.equals(faculty.getColor(), color))
                .toList();

        if (collection.isEmpty()) {
            throw new FacultyNotFoundException("Ни один факультет не подходит под условие");
        }

        return collection;
    }

}
