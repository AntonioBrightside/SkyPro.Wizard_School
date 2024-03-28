package com.amam.wizardschool.controller;

import com.amam.wizardschool.dto.AvatarDto;
import com.amam.wizardschool.exception.StudentNotFoundException;
import com.amam.wizardschool.model.Faculty;
import com.amam.wizardschool.model.Student;
import com.amam.wizardschool.repository.AvatarRepository;
import com.amam.wizardschool.repository.FacultyRepository;
import com.amam.wizardschool.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.h2.command.ddl.DropTrigger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    private final Faker faker = new Faker();

    private Faculty faculty1;
    private Faculty faculty2;
    String URL;

    @BeforeEach
    public void setUp() {
        faculty1 = createFaculty();
        faculty2 = createFaculty();

        createStudents(faculty1);
        createStudents(faculty2);

        URL = "http://localhost:%s/student".formatted(port);
    }

    @AfterEach
    public void afterEach() {
        avatarRepository.deleteAll();
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private void createStudents(Faculty faculty) {
        studentRepository.saveAll(
                Stream.generate(() -> {
                            Student student = new Student();
                            student.setFaculty(faculty);
                            student.setName(faker.lordOfTheRings().character());
                            student.setAge(faker.random().nextInt(10, 15));
                            return student;
                        })
                        .limit(3)
                        .collect(Collectors.toList())
        );
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());

        return facultyRepository.save(faculty);
    }

    @Test
    public void createStudentShouldReturnStudent() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(10, 15));
        student.setName(faker.lordOfTheRings().character());
        student.setFaculty(faculty1);

        Student createdStudent = testRestTemplate.postForEntity(
                URL,
                student,
                Student.class
        ).getBody();

        assertThat(createdStudent).isNotNull();
        assertThat(createdStudent.getId()).isNotNull();

        Optional<Student> studentFromDB = studentRepository.findById(createdStudent.getId());

        assertThat(studentFromDB).isPresent();
        assertThat(studentFromDB.get()).isEqualTo(createdStudent);
    }

    @Test
    public void createStudentShouldThrowFacultyException() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(10, 15));
        student.setName(faker.lordOfTheRings().character());

        assertThat(testRestTemplate.postForEntity(
                URL,
                student,
                Student.class
        ).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void editStudentShouldReturnStudent() {
        Student oldStudent = studentRepository.findAll().get(0);

        Student newStudent = new Student();
        newStudent.setAge(faker.random().nextInt(10, 15));
        newStudent.setName(faker.lordOfTheRings().character());
        newStudent.setFaculty(faculty2);
        newStudent.setId(oldStudent.getId());

        ResponseEntity<Student> response = testRestTemplate.exchange(
                URL,
                HttpMethod.PUT,
                new HttpEntity<>(newStudent),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(newStudent);
    }

    @Test
    public void editNonExistStudentReturnException() {
        Student newStudent = new Student();
        newStudent.setAge(faker.random().nextInt(10, 15));
        newStudent.setName(faker.lordOfTheRings().character());
        newStudent.setFaculty(faculty2);
        newStudent.setId(Long.MAX_VALUE);

        assertThat(testRestTemplate.exchange(URL,
                HttpMethod.PUT,
                new HttpEntity<>(newStudent),
                Student.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteStudentPositive() {
        Student stToDelete = studentRepository.findAll().get(0);
        String newURL = URL + "/" + stToDelete.getId();

        testRestTemplate.delete(newURL);

        assertThat(testRestTemplate.exchange(newURL,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                Student.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteNonExistStudentThrowNotFoundException() {
        assertThat(testRestTemplate.exchange(URL + "/" + Long.MAX_VALUE,
                HttpMethod.DELETE,
                new HttpEntity<>(new HttpHeaders()),
                String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getStudentFacultyPositive() {
        Student student = studentRepository.findAll().get(0);

        assertThat(testRestTemplate.getForObject(URL + "/" + student.getId(),
                Student.class).getFaculty()).isEqualTo(student.getFaculty());
    }

    @Test
    public void getNonExistStudentFacultyThrowNotFoundException() {
        assertThat(testRestTemplate.getForEntity(URL + "/" + Long.MAX_VALUE,
                String.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getStudentPositive() {
        Student student = studentRepository.findAll().get(0);

        assertThat(testRestTemplate.getForObject(URL + "/" + student.getId(),
                Student.class)).isEqualTo(student);
    }

    @Test
    public void getNonExistStudentThrowNotFoundException() {
        assertThat(testRestTemplate.getForEntity(URL + "/" + Long.MAX_VALUE,
                Student.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void getAllStudents() {
        List<Student> students = studentRepository.findAll();

        assertThat(testRestTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
        }).getBody()).isEqualTo(students);
    }

    @Test
    public void getStudentsByAgeReturnListOfStudents() {
        int age = faker.random().nextInt(10, 15);
        List<Student> expected = studentRepository.findAll()
                .stream()
                .filter(student -> student.getAge() == age)
                .toList();
        assertThat(testRestTemplate.exchange(URL + "?age=" + age,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }).getBody()).isEqualTo(expected);
    }

    @Test
    public void getStudentsBetweenMinAgeAndMaxAgeReturnListOfStudents() {
        int minAge = faker.random().nextInt(10, 14);
        int maxAge = faker.random().nextInt(minAge, 15);

        List<Student> expected = studentRepository.findAll()
                .stream()
                .filter(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .toList();

        assertThat(testRestTemplate.exchange(URL + "?minAge={minAge}&maxAge={maxAge}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                },
                Map.of("minAge", minAge, "maxAge", maxAge)).getBody()).isEqualTo(expected);
    }

//    @Test
//    public void whenPostAvatarShouldReturn200Response() {
//        Student student = studentRepository.findAll().get(0);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource("src/test/resources/for_tests/to_test.jpg"));
//
//
//        assertThat(testRestTemplate.exchange(URL + "/" + student.getId() + "/avatar",
//                HttpMethod.POST,
//                new HttpEntity<>(body, headers),
//                String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        /* Сохраняет в папку Java аватар, нужно удалить или придумать иной способ*/
//    }

    //    @Test
//    public void whenGetAvatarShouldReturnAvatarFromDisk() {
//        Student student = studentRepository.findAll().get(0);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource("src/test/resources/for_tests/to_test.jpg"));
//
//        testRestTemplate.exchange(URL + "/" + student.getId() + "/avatar",
//                HttpMethod.POST,
//                new HttpEntity<>(body, headers),
//                String.class);
//
//        assertThat(testRestTemplate.exchange(URL + "/" + student.getId() + "/avatar",
//                HttpMethod.GET,
//                new HttpEntity<>(new HttpHeaders()),
//                byte[].class).getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
    @Test
    public void getWrongAvatarShouldThrow404Exception() {
        long studentID = -1;
        assertThat(testRestTemplate.exchange(URL + "/" + studentID + "/avatar",
                HttpMethod.GET,
                null,
                byte[].class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

//    @Test
//    public void whenGetAvatarShouldReturnAvatarFromDB() {
//        Student student = studentRepository.findAll().get(0);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new FileSystemResource("src/test/resources/for_tests/to_test.jpg"));

//      /* Сохраняет в папку Java аватар, нужно удалить или придумать иной способ*/
//        testRestTemplate.exchange(URL + "/" + student.getId() + "/avatar",
//                HttpMethod.POST,
//                new HttpEntity<>(body, headers),
//                String.class);
//
//        assertThat(testRestTemplate.exchange(URL + "/" + student.getId() + "/avatar?smallAvatar=true",
//                HttpMethod.GET,
//                null,
//                byte[].class).getStatusCode()).isEqualTo(HttpStatus.OK);
//    }

    @Test
    public void getStudentsAmountPositiveValue() {
        int expected = studentRepository.getStudentsAmount();
        assertThat(testRestTemplate.getForObject("%s?countStudents=true".formatted(URL), Integer.class))
                .isEqualTo(expected);
    }

    @Test
    public void getAverageAgeReturnPositiveValue() {
        float expected = studentRepository.getAverageAge();
        assertThat(testRestTemplate.getForObject("%s?avAge=true".formatted(URL), Float.class))
                .isEqualTo(expected);

    }

    @Test
    public void getLastFiveStudentsPositive() {
        Collection<Student> expectedStudents = studentRepository.getLastFive();

        assertThat(testRestTemplate.exchange("%s?lastFive=true".formatted(URL),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }).getBody()).isEqualTo(expectedStudents);
    }


}