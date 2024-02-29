package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.FacultyRepository;
import com.amam.wizardschool.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository, StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
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
        return facultyRepository.getFacultyByColorIgnoreCase(color);
    }

    public Collection<Faculty> getFacultyByNameOrColorIgnoreCase(String name, String color) {
        return facultyRepository.findFacultyByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public Collection<Student> getStudentsFromFaculty(Long id) {
        return studentRepository.getStudentsByFaculty_id(id);
    }

}
