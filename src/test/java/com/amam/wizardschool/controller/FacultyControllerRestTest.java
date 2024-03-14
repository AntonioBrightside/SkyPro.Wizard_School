package com.amam.wizardschool.controller;

import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate testRestTemplate;


    @Test
    public void whenGetAllFacultiesShouldReturn200StatusCode() {
        assertThat(this.testRestTemplate.exchange("http://localhost:" + port + "/faculty",
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Collection.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void whenGetFacultyByNameShouldReturnFaculty() {
        String name = "first";

        assertThat(this.testRestTemplate.exchange("http://localhost:" + port + "/faculty?nameOrColor=" + name,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Collection.class).getBody().toString())
                .isEqualTo("[{id=402, name=first, color=red}]");
    }

    @Test
    public void whenGetFacultyByIDReturnFaculty() {
        long id = 402L;

        Faculty expected = new Faculty();
        expected.setId(402L);
        expected.setName("first");
        expected.setColor("red");

        assertThat(this.testRestTemplate.exchange("http://localhost:" + port + "/faculty/" + id,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Faculty.class).getBody())
                .isEqualTo(expected);
    }

    @Test
    public void whenGetStudentsInFacultyShouldReturnAtLeastOneStudent() {
        long facultyID = 452L;
        ObjectMapper mapper = new ObjectMapper();

        boolean actual = Arrays.stream(
                        this.testRestTemplate.getForObject(String.format("http://localhost:%s/faculty/%s/students", port, facultyID), Collection.class)
                                .toArray())
                .map(o -> mapper.convertValue(o, Student.class))
                .map(Student::getId)
                .filter(o -> o == 354)
                .toList().isEmpty();

        assertFalse(actual);
    }

    @Test
    public void whenPostFacultyShouldReturnFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("test");
        faculty.setColor("black");

        Faculty actual = this.testRestTemplate.postForEntity(String.format("http://localhost:%s/faculty", port), faculty, Faculty.class).getBody();
        faculty.setId(actual.getId());

        this.testRestTemplate.delete(String.format("http://localhost:%s/faculty/%s", port, actual.getId()));

        assertEquals(actual, faculty);
    }

    @Test
    public void whenDeleteNonExistFacultyShouldReturn400() {
        ResponseEntity<?> response = testRestTemplate.exchange(String.format("http://localhost:%s/faculty/%s", port, Long.MAX_VALUE),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void whenDeleteFacultyShouldDeleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("test");
        faculty.setColor("black");

        Faculty expected = this.testRestTemplate
                .postForEntity(String.format("http://localhost:%s/faculty", port), faculty, Faculty.class)
                .getBody();
        faculty.setId(expected.getId());

        testRestTemplate.delete(String.format("http://localhost:%s/faculty/%s", port, faculty.getId()));

        ResponseEntity<?> response = testRestTemplate.exchange(String.format("http://localhost:%s/faculty/%s", port, faculty.getId()),
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void whenPutFacultyShouldReturnFaculty() {
        Faculty faculty = new Faculty();
        faculty.setId(402L);
        faculty.setName("first");
        faculty.setColor("white");

        Faculty facultyOld = new Faculty();
        facultyOld.setId(402L);
        facultyOld.setName("first");
        facultyOld.setColor("red");

        assertThat(this.testRestTemplate.exchange(String.format("http://localhost:%s/faculty", port),
                HttpMethod.PUT,
                new HttpEntity<>(faculty),
                Faculty.class).getBody())
                .isEqualTo(faculty);

        testRestTemplate.exchange(String.format("http://localhost:%s/faculty", port),
                HttpMethod.PUT,
                new HttpEntity<>(facultyOld),
                Faculty.class);
    }

}