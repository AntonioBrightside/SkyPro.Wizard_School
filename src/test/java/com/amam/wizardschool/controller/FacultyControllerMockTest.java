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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FacultyControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private FacultyController facultyController;

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
    public void whenGetAllFacultiesShouldReturnTestFaculty() throws Exception {
        when(facultyRepository.findAll()).thenReturn(List.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(faculty.getName()));
    }

    @Test
    public void whetGetFacultyByNameShouldReturnFaculty() throws Exception {
        when(facultyRepository.findFacultyByNameIgnoreCaseOrColorIgnoreCase(any(String.class))).thenReturn(List.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty?nameOrColor=test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(faculty.getName()));
    }

    @Test
    public void whetGetFacultyByColorShouldReturnFaculty() throws Exception {
        when(facultyRepository.findFacultyByNameIgnoreCaseOrColorIgnoreCase(any(String.class))).thenReturn(List.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty?nameOrColor=black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".color").value(faculty.getColor()));
    }

    @Test
    public void whenGetFacultyByIDShouldReturnFaculty() throws Exception {
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(faculty.getName()));
    }

    @Test
    public void whenGetFacultyByIDShouldReturn404Status() throws Exception {
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenGetStudentsInFacultyShouldReturnStudents() throws Exception {
        when(studentRepository.getStudentsByFaculty_id(any(Long.class))).thenReturn(List.of(student));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".name").value(student.getName()));
    }

    @Test
    public void whenPostFacultyShouldReturnFaculty() throws Exception {
        when(facultyRepository.save(any())).thenReturn(faculty);

        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", "test");
        facultyJSON.put("color", "black");

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(facultyJSON.toString()))
                .andExpect(jsonPath(".name").value(faculty.getName()));
    }

    @Test
    public void whenPutFacultyShouldReturnFaculty() throws Exception {
        String color = "black";

        Faculty newFaculty = new Faculty();
        newFaculty.setId(1L);
        newFaculty.setName("changedTestName");
        newFaculty.setColor(color);

        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", "changedTestName");
        facultyJSON.put("color", color);

        when(facultyRepository.save(any())).thenReturn(newFaculty);
        when(facultyRepository.findById(any())).thenReturn(Optional.of(newFaculty));

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(facultyJSON.toString()))
                .andExpect(jsonPath(".color").value(color));
    }

    @Test
    public void whenDeleteFacultyShouldReturn200Status() throws Exception {
        when(facultyRepository.findById(any())).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}