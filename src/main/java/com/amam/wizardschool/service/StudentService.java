package com.amam.wizardschool.service;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Student;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {
    private Long id = 0L;
    private final Map<Long, Student> students;

    public StudentService() {
        students = new HashMap<>();
    }

    public Student createStudent(Student student) {
        student.setId(++id);
        students.put(id, student);
        return student;
    }

    public Student editStudent(Student student) throws StudentNotFoundException {
        if (students.get(student.getId()) == null) {
            throw new StudentNotFoundException("Студент не найден в базе");
        }

        students.put(student.getId(), student);
        return student;
    }

    public Student findStudent(Long id) throws StudentNotFoundException {
        if (students.get(id) == null) {
            throw new StudentNotFoundException("Студент не найден в базе");
        }

        return students.get(id);
    }

    public Student deleteStudent(Long id) throws StudentNotFoundException {
        Student deletedStudent = findStudent(id);
        return students.remove(deletedStudent.getId());
    }

    public Map<Long, Student> getStudents() {
        return students;
    }

    public Collection<Student> getStudentsByAge(int age) throws StudentNotFoundException {
        Collection<Student> collection =  students.values()
                .stream()
                .filter(student -> student.getAge() == age)
                .toList();

        if (collection.isEmpty()) {
            throw new StudentNotFoundException("Ни один студент не подходит под условие");
        }

        return collection;
    }
}
