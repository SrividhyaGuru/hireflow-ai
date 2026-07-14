package com.hireflow.auth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserLogoutServiceTest {
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private UserLogoutService userLogoutService;

    @Test
    public void shouldLogoutAndRevokeAllRefreshTokensForUser() {
        UUID userId = UUID.randomUUID();
        userLogoutService.logout(userId);
        Mockito.verify(refreshTokenService).revokeAllRefreshTokensFor(userId);
    }
}
