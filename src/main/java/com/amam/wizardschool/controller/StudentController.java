package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public Map<Long, Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(studentService.findStudent(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam("age") int age) {
        try {
            return ResponseEntity.ok(studentService.getStudentsByAge(age));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        try {
            return ResponseEntity.ok(studentService.editStudent(student));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(studentService.deleteStudent(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
