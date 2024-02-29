package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public Collection<Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        return studentService.findStudent(id).isPresent() ?
                ResponseEntity.ok(studentService.findStudent(id).get()) :
                ResponseEntity.badRequest().build();

    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam("age") int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        return studentService.editStudent(student).isPresent() ?
                ResponseEntity.ok(studentService.editStudent(student).get()) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") Long id) throws StudentNotFoundException {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }

    }

}
