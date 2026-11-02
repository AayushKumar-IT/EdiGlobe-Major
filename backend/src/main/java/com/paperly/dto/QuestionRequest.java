package com.paperly.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionRequest(
    @NotBlank(message = "Question text is required") @Size(max = 5000, message = "Question text is too long") String questionText,
    @NotNull(message = "Subject is required") Long subjectId,
    @NotBlank(message = "Topic is required") @Size(max = 200, message = "Topic is too long") String topic,
    @NotBlank(message = "Difficulty level is required") String difficultyLevel,
    @NotNull(message = "Marks are required") @Min(value = 1, message = "Marks must be at least 1")
    @Max(value = 1000, message = "Marks cannot exceed 1000") Integer marks
) {
}
