package com.hireflow.auth.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserLogoutService {
    private final RefreshTokenService refreshTokenService;

    public UserLogoutService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenService.revokeAllRefreshTokensFor(userId);
    }
}
