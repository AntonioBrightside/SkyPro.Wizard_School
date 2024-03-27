package com.amam.wizardschool.repository;

import com.amam.wizardschool.model.Avatar;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

}
