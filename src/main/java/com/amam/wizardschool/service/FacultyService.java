package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.repository.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyService {

    private FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Optional<Faculty> editFaculty(Faculty faculty) {
        return findFaculty(faculty.getId()).isPresent() ?
                Optional.of(facultyRepository.save(faculty)) :
                Optional.empty();
    }

    public Optional<Faculty> findFaculty(Long id) {
        return facultyRepository.findById(id);
    }

    public void deleteFaculty(Long id) throws FacultyNotFoundException {
        if (findFaculty(id).isEmpty()) {
            throw new FacultyNotFoundException("Faculty is not found");
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getFaculties() {
        return facultyRepository.findAll();
    }

    public Collection<Faculty> getFacultyByColor(String color) {
        return facultyRepository.getFacultyByColor(color);
    }

}
