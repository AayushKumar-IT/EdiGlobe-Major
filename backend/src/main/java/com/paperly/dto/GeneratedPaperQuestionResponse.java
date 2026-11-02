package com.paperly.dto;

import com.paperly.entity.DifficultyLevel;

public record GeneratedPaperQuestionResponse(
    Long id,
    String questionText,
    String topic,
    DifficultyLevel difficultyLevel,
    Integer marks,
    Integer displayOrder
) {
}
