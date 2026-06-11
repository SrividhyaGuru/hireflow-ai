package com.hireflow.auth.dto;

import com.hireflow.auth.entity.Role;

import java.util.UUID;

public record LoginResponse(UUID userId, String username, String email, Role role) {
}
