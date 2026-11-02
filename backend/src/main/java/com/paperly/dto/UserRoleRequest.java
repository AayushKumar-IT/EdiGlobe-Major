package com.paperly.dto;

import com.paperly.entity.UserRole;

import jakarta.validation.constraints.NotNull;

public record UserRoleRequest(@NotNull(message = "Role is required") UserRole role) {
}
