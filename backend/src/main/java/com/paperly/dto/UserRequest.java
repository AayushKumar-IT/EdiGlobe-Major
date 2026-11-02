package com.paperly.dto;

import com.paperly.entity.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotBlank(message = "Username is required") @Size(min = 3, max = 80, message = "Username must be between 3 and 80 characters") String username,
    @NotBlank(message = "Password is required") @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String password,
    @NotNull(message = "Role is required") UserRole role
) {
}
