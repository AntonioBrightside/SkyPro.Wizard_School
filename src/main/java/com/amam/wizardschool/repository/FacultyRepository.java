package com.amam.wizardschool.repository;

import com.amam.wizardschool.model.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Collection<Faculty> getFacultyByColorIgnoreCase(String color);

    @Query(value = "SELECT * FROM faculties WHERE color = :nameOrColor OR name = :nameOrColor",
           nativeQuery = true)
    Collection<Faculty> findFacultyByNameIgnoreCaseOrColorIgnoreCase(String nameOrColor);

}
