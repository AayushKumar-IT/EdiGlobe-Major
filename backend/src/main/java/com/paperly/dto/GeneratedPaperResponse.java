package com.paperly.dto;

import java.time.LocalDateTime;
import java.util.List;

public record GeneratedPaperResponse(
    Long id,
    String title,
    Long subjectId,
    String subjectName,
    String generatedBy,
    LocalDateTime generatedDate,
    Integer totalMarks,
    List<GeneratedPaperQuestionResponse> questions
) {
}
