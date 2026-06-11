package com.hireflow.auth.dto;

import com.hireflow.auth.entity.Role;
import jakarta.validation.constraints.*;

public record RegistrationRequest(
        @NotBlank @Size(min = 6, max=30) String username,
        @NotBlank @Size(min=6, max=15) String password,
        @NotBlank @Email String email,
        @NotNull Role role) { }
