package com.amam.wizardschool.controller;

import com.amam.wizardschool.dto.AvatarDto;
import com.amam.wizardschool.exception.AvatarNotFoundException;
import com.amam.wizardschool.exception.FacultyNotFoundException;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Avatar;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.service.AvatarService;
import com.amam.wizardschool.service.StudentService;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@Validated
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

//    @GetMapping
//    @Operation(summary = "Get all students / Get students by Age / Get students by Age between two values / Get amount of students / Get average students age / Get last 5 students")
//    public ResponseEntity<?> getStudents(@RequestParam(required = false) @Parameter(description = "Return students by definite age") Integer age,
//                                         @RequestParam(required = false, defaultValue = "0") @Parameter(description = "MIN value of age to return") int minAge,
//                                         @RequestParam(required = false, defaultValue = "1000") @Parameter(description = "MAX value of age to return") int maxAge,
//                                         @RequestParam(required = false, defaultValue = "false") @Parameter(description = "TRUE - return amount of students") boolean countStudents,
//                                         @RequestParam(required = false, defaultValue = "false") @Parameter(description = "TRUE - return average age of students") boolean avAge,
//                                         @RequestParam(required = false, defaultValue = "false") @Parameter(description = "TRUE - return last five students") boolean lastFive) {
//
//        if (age != null) {
//            return ResponseEntity.ok(studentService.getStudentsByAge(age));
//        }
//
//        if (countStudents) {
//            return ResponseEntity.ok(studentService.getStudentsAmount());
//        }
//
//        if (avAge) {
//            return ResponseEntity.ok(studentService.getAverageAge());
//        }
//
//        if (lastFive) {
//            return ResponseEntity.ok(studentService.getLastFive());
//        }
//
//        return ResponseEntity.ok(studentService.getStudentsByAgeBetween(minAge, maxAge));
//    }


    @GetMapping(params = "age")
    @Operation(summary = "Get students by Age / Get students by Age between two values / Get last 5 students")
    public ResponseEntity<Collection<Student>> getStudentsByAge(@RequestParam @Parameter(description = "Return students by definite age") Integer age) {
        return ResponseEntity.ok(studentService.getStudentsByAge(age));
    }

    @GetMapping(params = {"minAge", "maxAge"})
    @Operation(summary = "Get all students / Get students by Age / Get students by Age between two values / Get last 5 students")
    public ResponseEntity<Collection<Student>> getStudentsByAgeBetween(@RequestParam @Parameter(description = "MIN value of age to return") int minAge,
                                                                       @RequestParam @Parameter(description = "MAX value of age to return") int maxAge) {
        return ResponseEntity.ok(studentService.getStudentsByAgeBetween(minAge, maxAge));
    }

    @GetMapping(params = "lastFive")
    @Operation(summary = "Get students by Age / Get students by Age between two values / Get last 5 students")
    public ResponseEntity<Collection<Student>> getLastFiveStudents(@RequestParam @Parameter(description = "TRUE - return last five students") boolean lastFive) {
        return ResponseEntity.ok(studentService.getLastFive());
    }

    @GetMapping(params = "countStudents")
    @Operation(summary = "Get amount of students")
    public ResponseEntity<Integer> countStudents(@RequestParam(defaultValue = "false") @Parameter(description = "TRUE - return amount of students") boolean countStudents) {
        return ResponseEntity.ok(studentService.getStudentsAmount());
    }

    @GetMapping(params = "avAge")
    @Operation(summary = "Get average students age")
    public ResponseEntity<Float> studentsAverageAge(@RequestParam(defaultValue = "false") @Parameter(description = "TRUE - return average age of students") boolean avAge) {
        return ResponseEntity.ok(studentService.getAverageAge());
    }

    @GetMapping("{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<Student> getStudent(@PathVariable("id") Long id) {
        return studentService.findStudent(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    //TODO: Вынести в AvatarController. Разнести эндпоинты
    @GetMapping("{id}/avatar")
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

    @GetMapping("/avatar")
    @Operation(summary = "Get all avatars by pages")
    public ResponseEntity<Collection<AvatarDto>> getAllAvatars(@RequestParam @Min(value = 1, message = "MIN page number is 1") int pages,
                                                               @RequestParam @Min(value = 1, message = "MIN page size is 1") int size) {
        return ResponseEntity.ok(avatarService.getAllAvatars(pages, size));
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
    public ResponseEntity<?> deleteStudent(@PathVariable("id") Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/faculty", params = "id")
    @Operation(summary = "Get student faculty")
    public ResponseEntity<Faculty> getFaculty(@RequestParam Long id) {
        try {
            return ResponseEntity.ok(studentService.getStudentFaculty(id));
        } catch (StudentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
