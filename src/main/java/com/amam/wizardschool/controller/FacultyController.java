package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get all faculties")
    public Collection<Faculty> getFaculties() {
        return facultyService.getFaculties();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get faculty by ID")
    public ResponseEntity<Faculty> getFaculty(@PathVariable("id") Long id) {
        return facultyService.findFaculty(id).isPresent() ?
                ResponseEntity.ok(facultyService.findFaculty(id).get()) :
                ResponseEntity.badRequest().build();
    }

    @PostMapping
    @Operation(summary = "Create faculty")
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    @Operation(summary = "Edit faculty")
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        return facultyService.editFaculty(faculty).isPresent() ?
                ResponseEntity.ok(facultyService.editFaculty(faculty).get()) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete faculty by ID")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable("id") Long id) {
        try {
            facultyService.deleteFaculty(id);
            return ResponseEntity.ok().build();
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/color")
    @Operation(summary = "Get faculties by color")
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(@RequestParam("color") String color) {
        return ResponseEntity.ok(facultyService.getFacultyByColor(color));
    }

    @GetMapping
    @Operation(summary = "Get faculty by Name or Color")
    public ResponseEntity<Collection<Faculty>> getFacultyByNameOrColorIgnoreCase(@RequestParam(required = false) String name,
                                                                                 @RequestParam(required = false) String color) {
        if (name == null && color == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(facultyService.getFacultyByNameOrColorIgnoreCase(name, color));
    }

    @GetMapping("/faculty")
    @Operation(summary = "Get students in faculty")
    public ResponseEntity<Collection<Student>> getStudentsInFaculty(@RequestParam Long id) {
        return ResponseEntity.ok(facultyService.getStudentsFromFaculty(id));
    }

}
