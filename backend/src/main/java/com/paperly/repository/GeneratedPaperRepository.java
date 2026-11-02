package com.paperly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paperly.entity.GeneratedPaper;
import com.paperly.entity.Subject;
import com.paperly.entity.User;

public interface GeneratedPaperRepository extends JpaRepository<GeneratedPaper, Long> {
    List<GeneratedPaper> findByGeneratedByOrderByGeneratedDateDesc(User generatedBy);

    boolean existsByGeneratedBy(User generatedBy);

    boolean existsBySubject(Subject subject);
}
