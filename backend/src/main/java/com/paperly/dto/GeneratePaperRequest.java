package com.paperly.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GeneratePaperRequest(
    @NotBlank(message = "Paper title is required") @Size(max = 200, message = "Paper title is too long") String title,
    @NotNull(message = "Subject is required") Long subjectId,
    @NotNull(message = "Total marks are required") @Min(value = 1, message = "Marks must be at least 1")
    @Max(value = 1000, message = "Total marks cannot exceed 1000") Integer totalMarks,
    @NotBlank(message = "Difficulty level is required") @Size(max = 20) String difficultyLevel,
    @Size(max = 50, message = "At most 50 topic allocations are allowed")
    List<@Valid TopicAllocationRequest> topicAllocations
) {
}
