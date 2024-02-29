package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Get all students")
    public Collection<Student> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        return studentService.findStudent(id).isPresent() ?
                ResponseEntity.ok(studentService.findStudent(id).get()) :
                ResponseEntity.badRequest().build();

    }

    @GetMapping("/age/{age}")
    @Operation(summary = "Get students by Age")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@PathVariable int age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @GetMapping("/age")
    @Operation(summary = "Get students by Age between two values")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(@RequestParam(required = false, defaultValue = "0") int min,
                                                                       @RequestParam(required = false, defaultValue = "1000") int max) {
        return ResponseEntity.ok(studentService.getStudentsByAgeBetween(min, max));
    }


    @PostMapping
    @Operation(summary = "Create student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping
    @Operation(summary = "Edit student")
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        return studentService.editStudent(student).isPresent() ?
                ResponseEntity.ok(studentService.editStudent(student).get()) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete student by ID")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") Long id) throws StudentNotFoundException {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/faculty")
    @Operation(summary = "Get student faculty")
    public ResponseEntity<Faculty> getFaculty(@RequestParam Long id) throws StudentNotFoundException {
        try {
            return ResponseEntity.ok(studentService.getStudentFaculty(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
