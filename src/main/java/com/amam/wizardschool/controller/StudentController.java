package com.amam.wizardschool.controller;

import com.amam.wizardschool.exception.AvatarNotFoundException;
import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Avatar;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.AvatarService;
import com.amam.wizardschool.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@Tag(name = "Students", description = "Endpoints to work with Students")
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
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

    @GetMapping(value = "{id}/avatar")
    @Operation(summary = "Get students full size avatar or small version")
    public ResponseEntity<?> downloadAvatar(@PathVariable("id") Long id,
                                            @RequestParam(required = false, defaultValue = "false") @Parameter(description = "Return small Avatar if necessary. True - return") Boolean smallAvatar,
                                            HttpServletResponse response) throws IOException {
        try {
            Avatar avatar = avatarService.findAvatar(id);

            if (smallAvatar) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
                headers.setContentLength(avatar.getSmallPhoto().length);

                return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getSmallPhoto());
            }

            Path path = Path.of(avatar.getFilePath());

            try (InputStream is = Files.newInputStream(path);
                 OutputStream os = response.getOutputStream();) {
                response.setContentType(avatar.getMediaType());
                response.setContentLength(avatar.getFileSize().intValue());
                is.transferTo(os);
            }

            return ResponseEntity.ok().build();

        } catch (AvatarNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    @Operation(summary = "Create student")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) throws FacultyNotFoundException {
        try {
            return ResponseEntity.ok(studentService.createStudent(student));
        } catch (FacultyNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload avatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id,
                                               @RequestParam MultipartFile file) throws IOException {
        try {
            avatarService.uploadAvatar(id, file);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

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
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/faculty", params = "id")
    @Operation(summary = "Get student faculty")
    public ResponseEntity<Faculty> getFaculty(@RequestParam Long id) throws StudentNotFoundException {
        try {
            return ResponseEntity.ok(studentService.getStudentFaculty(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
