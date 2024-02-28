package com.amam.wizardschool.repository;

import com.amam.wizardschool.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(
            value = "SELECT * FROM students WHERE age = :age",
            nativeQuery = true
    )
    Collection<Student> findByAgeLike(int age);
}
