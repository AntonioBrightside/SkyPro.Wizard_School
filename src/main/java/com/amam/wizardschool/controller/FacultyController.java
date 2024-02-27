package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/all")
    public Map<Long, Faculty> getFaculties() {
        return facultyService.getFaculties();
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(facultyService.findFaculty(id));
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        try {
            return ResponseEntity.ok(facultyService.editFaculty(faculty));
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(facultyService.deleteFaculty(id));
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(@RequestParam("color") String color) {
        try {
            return ResponseEntity.ok(facultyService.getFacultyByColor(color));
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
