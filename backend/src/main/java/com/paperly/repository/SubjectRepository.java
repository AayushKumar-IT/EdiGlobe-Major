package com.paperly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paperly.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);

    Optional<Subject> findByNameIgnoreCase(String name);
}
