package com.amam.wizardschool.repository;

import com.amam.wizardschool.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeBetween(int minAge, int maxAge);

    Collection<Student> getStudentsByFaculty_id(Long id);

    @Query(nativeQuery = true,
    value = "SELECT count(id) FROM students ")
    int getStudentsAmount();

    @Query(nativeQuery = true,
    value = "SELECT avg(age) FROM students")
    float getAverageAge();

    @Query(nativeQuery = true,
    value = "SELECT * FROM students ORDER BY id DESC LIMIT 5")
    Collection<Student> getLastFive();
}
