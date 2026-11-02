package com.paperly.dto;

import com.paperly.entity.UserRole;

public record UserResponse(Long id, String username, UserRole role) {
}
