package com.paperly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.paperly.entity.DifficultyLevel;
import com.paperly.entity.Question;
import com.paperly.entity.Subject;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubject(Subject subject);

    @Query("SELECT q FROM Question q WHERE q.subject.id = :subjectId")
    List<Question> findBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT q FROM Question q WHERE q.subject.id = :subjectId AND (:difficulty IS NULL OR q.difficultyLevel = :difficulty)")
    List<Question> findBySubjectIdAndOptionalDifficulty(@Param("subjectId") Long subjectId, @Param("difficulty") DifficultyLevel difficulty);

    List<Question> findByTopicContainingIgnoreCase(String topic);
}
