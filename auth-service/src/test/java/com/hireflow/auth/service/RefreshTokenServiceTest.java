package com.hireflow.auth.service;

import com.hireflow.auth.domain.RefreshSession;
import com.hireflow.auth.entity.RefreshToken;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.security.InvalidRefreshTokenException;
import com.hireflow.auth.repository.RefreshTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private long expirationInSeconds = 3600;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, expirationInSeconds);

    }

    private String validRefreshToken = "ValidRefreshToken";
    private String hashedValidRefreshToken = DigestUtils.sha256Hex(validRefreshToken);
    private String invalidRefreshToken = "invalidRefreshToken";
    private String hashedInvalidRefreshToken = DigestUtils.sha256Hex(invalidRefreshToken);
    private User user = User.builder()
            .email("someEmail")
            .role(Role.RECRUITER)
            .username("someUserName")
            .password("RandomPassword")
            .build();
    private RefreshToken activeRefreshToken = new RefreshToken.Builder()
            .tokenHash(hashedValidRefreshToken)
            .issuedAt(Instant.now().minus(Duration.ofMinutes(30)))
            .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
            .user(user)
            .revoked(false)
            .build();



    @Test
    public void shouldRotateTokenForValidRefreshToken() {

        Mockito.when(refreshTokenRepository.findByTokenHashForUpdate(hashedValidRefreshToken))
                .thenReturn(Optional.of(activeRefreshToken));
        ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        RefreshSession refreshSession = refreshTokenService.rotateToken(validRefreshToken);

        Mockito.verify(refreshTokenRepository).findByTokenHashForUpdate(hashedValidRefreshToken);
        Mockito.verify(refreshTokenRepository).save(refreshTokenArgumentCaptor.capture());

        assertTrue(activeRefreshToken.isRevoked());
        RefreshToken generatedRefreshToken = refreshTokenArgumentCaptor.getValue();
        assertNotNull(generatedRefreshToken.getTokenHash());
        assertNotEquals(activeRefreshToken.getTokenHash(), generatedRefreshToken.getTokenHash());
        assertNotNull(generatedRefreshToken.getIssuedAt());
        assertEquals(generatedRefreshToken.getIssuedAt().plus(Duration.ofSeconds(expirationInSeconds)),
                generatedRefreshToken.getExpiresAt());
        assertFalse(generatedRefreshToken.isRevoked());
        assertEquals(user, generatedRefreshToken.getUser());

        assertEquals(user, refreshSession.user());
        assertEquals(generatedRefreshToken.getTokenHash(), DigestUtils.sha256Hex(refreshSession.refreshToken()));
        assertNotEquals(validRefreshToken, refreshSession.refreshToken());

    }

    @Test
    public void shouldThrowInvalidRefreshTokenExceptionWhenRefreshTokenExpired() {
        RefreshToken expiredRefreshToken = new RefreshToken.Builder()
                .tokenHash(hashedInvalidRefreshToken)
                .issuedAt(Instant.now().minus(Duration.ofMinutes(60)))
                .expiresAt(Instant.now().minus(Duration.ofMinutes(30)))
                .user(user)
                .revoked(false)
                .build();
        Mockito.when(refreshTokenRepository.findByTokenHashForUpdate(hashedInvalidRefreshToken))
                .thenReturn(Optional.of(expiredRefreshToken));

        InvalidRefreshTokenException invalidRefreshTokenException = assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.rotateToken(invalidRefreshToken));

        assertEquals("Invalid refresh token", invalidRefreshTokenException.getMessage());
        Mockito.verify(refreshTokenRepository).findByTokenHashForUpdate(hashedInvalidRefreshToken);
        Mockito.verify(refreshTokenRepository, Mockito.never()).save(Mockito.any(RefreshToken.class));

    }

    @Test
    public void shouldThrowInvalidRefreshTokenExceptionWhenRefreshTokenIsRevokedAlready() {
        RefreshToken revokedRefreshToken = new RefreshToken.Builder()
                .tokenHash(hashedInvalidRefreshToken)
                .issuedAt(Instant.now().minus(Duration.ofMinutes(30)))
                .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
                .user(user)
                .revoked(true)
                .build();

        Mockito.when(refreshTokenRepository.findByTokenHashForUpdate(hashedInvalidRefreshToken))
                .thenReturn(Optional.of(revokedRefreshToken));

        InvalidRefreshTokenException invalidRefreshTokenException = assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.rotateToken(invalidRefreshToken));

        assertEquals("Invalid refresh token", invalidRefreshTokenException.getMessage());
        Mockito.verify(refreshTokenRepository, Mockito.never()).save(Mockito.any(RefreshToken.class));
        Mockito.verify(refreshTokenRepository).findByTokenHashForUpdate(hashedInvalidRefreshToken);
    }

    @Test
    public void shouldThrowInvalidRefreshTokenExceptionWhenRefreshTokenNotFound() {

        Mockito.when(refreshTokenRepository.findByTokenHashForUpdate(hashedInvalidRefreshToken))
                .thenReturn(Optional.empty());

        InvalidRefreshTokenException invalidRefreshTokenException = assertThrows(InvalidRefreshTokenException.class,
                () -> refreshTokenService.rotateToken(invalidRefreshToken));

        assertEquals("Invalid refresh token", invalidRefreshTokenException.getMessage());
        Mockito.verify(refreshTokenRepository, Mockito.never()).save(Mockito.any(RefreshToken.class));
        Mockito.verify(refreshTokenRepository).findByTokenHashForUpdate(hashedInvalidRefreshToken);


    }

    @Test
    public void shouldGenerateAndSaveRefreshTokenForUser() {

        ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        String rawRefreshToken = refreshTokenService.generateAndSaveRefreshToken(user);

        Mockito.verify(refreshTokenRepository).save(refreshTokenArgumentCaptor.capture());

        RefreshToken newRefreshTokenEntity = refreshTokenArgumentCaptor.getValue();

        assertNotNull(rawRefreshToken);
        assertFalse(rawRefreshToken.isBlank());
        assertEquals(DigestUtils.sha256Hex(rawRefreshToken), newRefreshTokenEntity.getTokenHash());
        assertEquals(newRefreshTokenEntity.getIssuedAt().plus(Duration.ofSeconds(expirationInSeconds)),
                newRefreshTokenEntity.getExpiresAt());
        assertEquals(user, newRefreshTokenEntity.getUser());
        assertFalse(newRefreshTokenEntity.isRevoked());
    }

    @Test
    public void shouldRevokeAllRefreshTokensForUser() {
        UUID userId = UUID.randomUUID();

        refreshTokenService.revokeAllRefreshTokensFor(userId);

        Mockito.verify(refreshTokenRepository).revokeAllRefreshTokenFor(userId);
        Mockito.verifyNoMoreInteractions(refreshTokenRepository);
    }

}
