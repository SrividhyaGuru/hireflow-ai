package com.hireflow.auth.service;

import com.hireflow.auth.domain.RefreshSession;
import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class SessionRefreshServiceTest {
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private SessionRefreshService sessionRefreshService;

    private String validRefreshToken = "validRefreshToken";
    @Spy
    private User user = User.builder()
            .email("someEmail")
            .role(Role.RECRUITER)
            .username("someUserName")
            .password("RandomPassword")
            .build();

    @Test
    public void shouldRefreshSessionGivenTheValidRefreshToken() {

        String newRefreshToken = "hjbj7tiavkjdbai8oipyret76e7677687ygyu";
        RefreshSession refreshSession = new RefreshSession(user, newRefreshToken);
        String newJwtAccessToken = "hfhjahj89jbkjb8bbj";

        UUID userId = UUID.randomUUID();
        Mockito.when(user.getId()).thenReturn(userId);

        Mockito.when(refreshTokenService.rotateToken(validRefreshToken)).thenReturn(refreshSession);
        Mockito.when(jwtService.generateToken(userId, user.getEmail(), user.getRole().name())).thenReturn(newJwtAccessToken);

        AuthResponse refreshAuth = sessionRefreshService.refresh(validRefreshToken);

        Mockito.verify(refreshTokenService).rotateToken(validRefreshToken);
        Mockito.verify(jwtService).generateToken(userId, user.getEmail(), user.getRole().name());

        assertNotNull(refreshAuth);
        assertEquals(userId, refreshAuth.userId());
        assertEquals(user.getUsername(), refreshAuth.username());
        assertEquals(user.getEmail(),refreshAuth.email());
        assertEquals(user.getRole(), refreshAuth.role());
        assertEquals(newRefreshToken,refreshAuth.refreshToken());
        assertEquals(newJwtAccessToken,refreshAuth.accessToken());

    }
}
