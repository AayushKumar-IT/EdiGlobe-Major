package com.paperly.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TopicAllocationRequest(
    @NotBlank(message = "Topic is required") @Size(max = 200, message = "Topic is too long") String topic,
    @NotNull(message = "Marks are required") @Min(value = 1, message = "Marks must be at least 1")
    @Max(value = 1000, message = "Topic allocation cannot exceed 1000 marks") Integer marks
) {
}
