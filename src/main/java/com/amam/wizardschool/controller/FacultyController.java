package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.service.FacultyService;
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
    public Collection<Faculty> getFaculties() {
        return facultyService.getFaculties();
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable("id") Long id) {
        return facultyService.findFaculty(id).isPresent() ?
                ResponseEntity.ok(facultyService.findFaculty(id).get()) :
                ResponseEntity.badRequest().build();
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        return facultyService.editFaculty(faculty).isPresent() ?
                ResponseEntity.ok(facultyService.editFaculty(faculty).get()) :
                ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable("id") Long id) {
        try {
            facultyService.deleteFaculty(id);
            return ResponseEntity.ok().build();
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getFacultyByColor(@RequestParam("color") String color) {
        return ResponseEntity.ok(facultyService.getFacultyByColor(color));
    }
}
