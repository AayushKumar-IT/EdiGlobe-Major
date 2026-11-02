package com.paperly.dto;

import com.paperly.entity.DifficultyLevel;

public record QuestionResponse(
    Long id,
    String questionText,
    Long subjectId,
    String subjectName,
    String topic,
    DifficultyLevel difficultyLevel,
    Integer marks
) {
}
