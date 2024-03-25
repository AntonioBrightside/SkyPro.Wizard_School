package com.amam.wizardschool.controller;

import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.AvatarRepository;
import com.amam.wizardschool.repository.FacultyRepository;
import com.amam.wizardschool.repository.StudentRepository;
import com.amam.wizardschool.service.AvatarService;
import com.amam.wizardschool.service.FacultyService;
import com.amam.wizardschool.service.StudentService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class StudentControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private StudentController studentController;

    @MockBean
    private FacultyRepository facultyRepository;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private AvatarRepository avatarRepository;

    @SpyBean
    private FacultyService facultyService;

    @SpyBean
    private StudentService studentService;

    @SpyBean
    private AvatarService avatarService;

    private Faculty faculty;
    private Student student;

    @BeforeEach
    public void setUp() {
        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("test");
        faculty.setColor("black");

        student = new Student();
        student.setAge(15);
        student.setName("student");
        student.setId(11L);
    }

    @Test
    public void whenGetStudentsBetween13And15ShouldReturnStudent() throws Exception {
        when(studentRepository.findByAgeBetween(13, 15)).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student?minAge=13&maxAge=15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(student.getName()));
    }

    @Test
    public void whenGetAllStudentsShouldReturnStudents() throws Exception {
        when(studentRepository.findByAgeBetween(0, 1000)).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(student.getName()));
    }

    @Test
    public void whenGetStudentByIDShouldReturnStudent() throws Exception {
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(student.getName()));
    }

    @Test
    public void whenPostStudentShouldReturnStudent() throws Exception {
        JSONObject studentJSON = new JSONObject();
        studentJSON.put("name", "student");
        studentJSON.put("age", 15);

        when(studentRepository.save(any())).thenReturn(student);

        mockMvc.perform(MockMvcRequestBuilders.post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(studentJSON.toString()))
                .andExpect(jsonPath(".name").value("student"));
    }

    @Test
    public void whenPutStudentShouldReturnStudent() throws Exception {
        JSONObject studentJSON = new JSONObject();
        studentJSON.put("name", "student");
        studentJSON.put("age", 15);
        studentJSON.put("id", 11L);

        when(studentRepository.save(any())).thenReturn(student);
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.put("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(studentJSON.toString()))
                .andExpect(jsonPath(".name").value("student"));
    }

    @Test
    public void whenGetFacultyShouldReturnFaculty() throws Exception {
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value("student"));
    }

    @Test
    public void whenDeleteStudentShouldReturn200Status() throws Exception {
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        mockMvc.perform(MockMvcRequestBuilders.delete("/student/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenPostAvatarShouldReturn200Status() throws Exception {
        when(studentRepository.findById(any())).thenReturn(Optional.ofNullable(student));

        MockMultipartFile file = new MockMultipartFile("file", Files.readAllBytes(Path.of("src/main/resources/for_tests/to_test.jpg")));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/student/15/avatar")
                        .file(file))
                .andExpect(status().isOk());
    }

}