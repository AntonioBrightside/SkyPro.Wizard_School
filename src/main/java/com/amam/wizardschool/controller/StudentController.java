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

    @GetMapping
    @Operation(summary = "Get all students / Get students by Age / Get students by Age between two values")
    public ResponseEntity<Collection<Student>> getStudents(@RequestParam(required = false) Integer age,
                                                           @RequestParam(required = false, defaultValue = "0") int minAge,
                                                           @RequestParam(required = false, defaultValue = "1000") int maxAge) {
        if (age != null) {
            return ResponseEntity.ok(studentService.getStudentsByAge(age));
        }

        return ResponseEntity.ok(studentService.getStudentsByAgeBetween(minAge, maxAge));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        return studentService.findStudent(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping
    @Operation(summary = "Create student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping
    @Operation(summary = "Edit student")
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        return studentService.editStudent(student).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

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
            return ResponseEntity.notFound().build();
        }
    }

}
