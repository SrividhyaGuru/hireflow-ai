package com.hireflow.auth.service;

import com.hireflow.auth.domain.RefreshSession;
import com.hireflow.auth.entity.RefreshToken;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.security.InvalidRefreshTokenException;
import com.hireflow.auth.repository.RefreshTokenRepository;
import com.hireflow.auth.security.jwt.JwtService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final long tokenExpirationInSeconds;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtService jwtService,
                               @Value("${refresh-token.expiration-seconds}") long tokenExpirationInSeconds) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.tokenExpirationInSeconds = tokenExpirationInSeconds;
    }

    @Transactional
    public RefreshSession rotateToken(String clientRawRefreshToken) {
        RefreshToken existingRefreshToken = refreshTokenRepository.findByTokenHashForUpdate(getHashedToken(clientRawRefreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);
        validateRefreshToken(existingRefreshToken);
        existingRefreshToken.revoke();
        User registeredUser = existingRefreshToken.getUser();
        String newRefreshToken = generateAndSaveRefreshToken(registeredUser);
        return new RefreshSession(registeredUser, newRefreshToken);
    }

    public void revokeAllRefreshTokensFor(UUID userId) {
        refreshTokenRepository.revokeAllRefreshTokenFor(userId);
    }

    private void validateRefreshToken(RefreshToken existingToken) {
        if (existingToken.isRevoked() || Instant.now().isAfter(existingToken.getExpiresAt())) {
            throw new InvalidRefreshTokenException();
        }
    }

    public String generateAndSaveRefreshToken(User registeredUser) {
        String rawRefreshToken = generateRawTokenString();
        String hashedTokenString = getHashedToken(rawRefreshToken);
        refreshTokenRepository.save(mapToRefreshTokenEntity(registeredUser, hashedTokenString));
        return rawRefreshToken;

    }

    private RefreshToken mapToRefreshTokenEntity(User registeredUser, String secureTokenString) {
        Instant issueTime = Instant.now();
        return RefreshToken.builder()
                .tokenHash(secureTokenString)
                .issuedAt(issueTime)
                .expiresAt(issueTime.plusSeconds(tokenExpirationInSeconds))
                .user(registeredUser).build();
    }

    private String generateRawTokenString() {
        byte[] randomBytes = new byte[32];
        this.secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String getHashedToken(String token) {
        return DigestUtils.sha256Hex(token);
    }
}
