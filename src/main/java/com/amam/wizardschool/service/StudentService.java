package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.StudentNotFoundException;
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

    public Student createStudent(Student student) {
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

    public Collection<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Collection<Student> getStudentsByAge(int age) {
        return studentRepository.findByAgeLike(age);
    }
}
