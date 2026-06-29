package com.hireflow.auth.service;

import com.hireflow.auth.domain.RefreshSession;
import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.security.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthService(RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    public AuthResponse refresh(String clientRawRefreshToken) {
        RefreshSession refreshSession = refreshTokenService.rotateRefreshToken(clientRawRefreshToken);
        User user = refreshSession.user();
        String newAccessToken = jwtService.generateToken(user.getId(),
                user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .withUserId(user.getId())
                .withUsername(user.getUsername())
                .withEmail(user.getEmail())
                .withRole(user.getRole())
                .withRefreshToken(refreshSession.refreshToken())
                .withAccessToken(newAccessToken)
                .build();

    }
}
