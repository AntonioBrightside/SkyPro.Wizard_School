package com.amam.wizardschool.repository;

import com.amam.wizardschool.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeBetween(int min, int max);

//    @Query(
//            value = "SELECT * FROM students WHERE faculty_id = :faculty_id",
//            nativeQuery = true
//    )
    Collection<Student> getStudentsByFaculty_id(Long id);
}
