package com.hireflow.auth.dto;

import java.util.List;

public record ErrorResponse(List<String> errors) {
}
