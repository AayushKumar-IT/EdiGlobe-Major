package com.paperly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(
    @NotBlank(message = "Subject name is required") @Size(max = 100, message = "Subject name must be below 100 characters") String name,
    @Size(max = 1000, message = "Description must be below 1000 characters") String description
) {
}
