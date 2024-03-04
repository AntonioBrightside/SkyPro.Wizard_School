package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.FacultyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    @Operation(summary = "Get all faculties / Get faculty by Name or Color")
    public ResponseEntity<Collection<Faculty>> getFaculties(@RequestParam(required = false) String nameOrColor) {

        if (nameOrColor != null) {
            return ResponseEntity.ok(facultyService.getFacultyByNameOrColorIgnoreCase(nameOrColor));
        }

        return ResponseEntity.ok(facultyService.getFaculties());

    }

    @GetMapping("{id}")
    @Operation(summary = "Get faculty by ID")
    public ResponseEntity<Faculty> getFaculty(@PathVariable("id") Long id) {
        return facultyService.findFaculty(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{id}/students")
    @Operation(summary = "Get students in faculty")
    public ResponseEntity<Collection<Student>> getStudentsInFaculty(@PathVariable Long id) {
        return ResponseEntity.ok(facultyService.getStudentsFromFaculty(id));
    }

    @PostMapping
    @Operation(summary = "Create faculty")
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        faculty.setId(null);
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    @Operation(summary = "Edit faculty")
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        return facultyService.editFaculty(faculty).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete faculty by ID")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable("id") Long id) {
        try {
            facultyService.deleteFaculty(id);
            return ResponseEntity.ok().build();
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}
