package com.hireflow.auth.domain;

import com.hireflow.auth.entity.User;

public record RefreshSession(User user, String refreshToken) {
}
