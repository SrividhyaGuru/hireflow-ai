package com.hireflow.auth.service;

import com.hireflow.auth.entity.RefreshToken;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.repository.RefreshTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long tokenExpirationInSeconds;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${refresh-token.expiration-seconds}") long tokenExpirationInSeconds) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenExpirationInSeconds = tokenExpirationInSeconds;
    }

    public String generateAndSaveRefreshToken(User registeredUser) {
        String rawRefreshToken = generateRawTokenString();
        String hashedTokenString = getHashedToken(rawRefreshToken);
        RefreshToken refreshToken = refreshTokenRepository.save(
                mapToRefreshTokenEntity(registeredUser, hashedTokenString));
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
