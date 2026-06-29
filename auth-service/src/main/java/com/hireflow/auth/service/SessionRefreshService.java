package com.hireflow.auth.service;

import com.hireflow.auth.domain.RefreshSession;
import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.security.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service
public class SessionRefreshService {
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public SessionRefreshService(RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    public AuthResponse refresh(String clientRawRefreshToken) {
        RefreshSession session = refreshTokenService.rotateToken(clientRawRefreshToken);
        User user = session.user();
        String newAccessToken = jwtService.generateToken(user.getId(),
                user.getEmail(), user.getRole().name());
        return AuthResponse.builder()
                .withUserId(user.getId())
                .withUsername(user.getUsername())
                .withEmail(user.getEmail())
                .withRole(user.getRole())
                .withRefreshToken(session.refreshToken())
                .withAccessToken(newAccessToken)
                .build();

    }
}
