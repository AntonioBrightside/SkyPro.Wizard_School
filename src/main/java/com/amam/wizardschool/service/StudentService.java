package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) throws FacultyNotFoundException {
        student.setId(null);

        if (student.getFaculty() == null) {
            throw new FacultyNotFoundException("You should fill in faculty information");
        }

        return studentRepository.save(student);
    }

    public Optional<Student> editStudent(Student student) {
        return findStudent(student.getId()).isPresent() ?
                Optional.of(studentRepository.save(student)) :
                Optional.empty();
    }

    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    public void deleteStudent(Long id) throws StudentNotFoundException {
        if (findStudent(id).isEmpty()) {
            throw new StudentNotFoundException("Student is not found");
        }
        studentRepository.deleteById(id);
    }

    @Deprecated(since = "SQL task")
    public Collection<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getStudentFaculty(Long id) throws StudentNotFoundException {
        return findStudent(id).map(Student::getFaculty).orElseThrow(() -> new StudentNotFoundException("Student is not found"));
    }
}
