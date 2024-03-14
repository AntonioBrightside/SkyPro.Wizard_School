package com.amam.wizardschool.controller;

import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private String URL;

    @BeforeEach
    public void setUp() {
        URL = String.format("http://localhost:%s/student", port);
    }

    @Test
    public void whenGetAllStudentsShouldReturnOneOfTheStudent() {
        ObjectMapper mapper = new ObjectMapper();
        boolean actual = Arrays.stream(
                        this.testRestTemplate.getForObject(
                                        URL, Collection.class)
                                .toArray())
                .map(o -> mapper.convertValue(o, Student.class))
                .map(Student::getId)
                .filter(o -> o == 352)
                .toList().isEmpty();

        assertFalse(actual);
    }

    @Test
    public void whenGetStudentByAge15ShouldReturnListOfStudents() {
        boolean actual = Arrays.stream(
                        this.testRestTemplate.getForObject(
                                        URL + "?age=15", Collection.class)
                                .toArray())
                .map(o -> mapper.convertValue(o, Student.class))
                .map(Student::getAge)
                .filter(o -> o != 15)
                .toList().isEmpty();

        assertTrue(actual);
    }

    @Test
    public void whenGetStudentsBetween13and14ShouldReturnListOfStudents() {
        boolean actual = Arrays.stream(
                        this.testRestTemplate.getForObject(
                                        URL + "?minAge=13&maxAge=14", Collection.class)
                                .toArray())
                .map(o -> mapper.convertValue(o, Student.class))
                .map(Student::getAge)
                .filter(o -> (o != 13) && (o != 14))
                .toList().isEmpty();

        assertTrue(actual);
    }

    @Test
    public void whenGetStudentByIDShouldReturnStudent() {
        long studentID = 352L;

        assertThat(this.testRestTemplate.getForObject(
                        URL + "/" + studentID, Student.class)
                .getId()).isEqualTo(studentID);
    }

    @Test
    public void whenGetAvatarShouldReturnAvatarFromDisk() {
        long studentID = 353L;
        assertThat(testRestTemplate.exchange(URL + "/" + studentID + "/avatar",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                byte[].class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void whenGetWrongAvatarShouldThrow404Exception() {
        long studentID = 402L;
        assertThat(testRestTemplate.exchange(URL + "/" + studentID + "/avatar",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                byte[].class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void whenGetAvatarShouldReturnAvatarFromDB() {
        long studentID = 353L;
        assertThat(testRestTemplate.exchange(URL + "/" + studentID + "/avatar?smallAvatar=true",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                byte[].class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void whenPostAvatarShouldReturn200Response() throws FileNotFoundException {
        long studentID = 352L;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource("src/main/resources/for_tests/to_test.jpg"));

        assertThat(testRestTemplate.exchange(URL + "/" + studentID + "/avatar",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void whenPostStudentShouldReturnStudent() {
        Faculty faculty = new Faculty();
        faculty.setName("test");
        faculty.setColor("black");
        faculty.setId(452L);

        Student student = new Student();
        student.setFaculty(faculty);
        student.setAge(20);
        student.setName("Joseph");

        Student actual = testRestTemplate.postForEntity(URL,
                student, Student.class).getBody();
        student.setId(actual.getId());
        testRestTemplate.delete(String.format("http://localhost:%s/student/%s", port, actual.getId()));

        assertEquals(actual, student);
    }

    @Test
    public void whenPutStudentReturnNewStudent() {
        long studentID = 352L;
        Student newStudent = new Student();
        newStudent.setName("Test");
        newStudent.setAge(20);
        newStudent.setId(352L);

        Student oldStudent = testRestTemplate.getForObject(
                URL + "/" + studentID,
                Student.class);


        assertThat(testRestTemplate.exchange(URL,
                HttpMethod.PUT,
                new HttpEntity<>(newStudent),
                Student.class).getBody())
                .isEqualTo(newStudent);

        testRestTemplate.exchange(URL,
                HttpMethod.PUT,
                new HttpEntity<>(oldStudent),
                Student.class);
    }

    @Test
    public void whenDeleteStudentShouldReturnNotFoundGetQuery() {
        Student newStudent = new Student();
        newStudent.setName("Test");
        newStudent.setAge(20);

        Student expected = testRestTemplate
                .postForObject(URL, newStudent, Student.class);
        newStudent.setId(expected.getId());

        testRestTemplate.delete(URL + "/" + newStudent.getId());

        ResponseEntity<?> response = testRestTemplate.exchange(URL + "/" + newStudent.getId(),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}