package com.hireflow.auth.security.jwt;

import com.hireflow.auth.domain.AuthenticatedUser;
import com.hireflow.auth.entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private String testJwtSecret = "00dee8b1d4ab9aa50d3904cf1080344aabaf3c0065ef8878053e512e83e6ab42";
    private long testJwtExpirationInMs = 20000;
    private JwtService jwtService;

    @Test
    public void shouldGenerateJwtTokenWithUserIdEmailandRole() {
        jwtService = new JwtService(testJwtSecret, testJwtExpirationInMs);
        UUID userId = UUID.randomUUID();
        String email = "testuser@hireflow.com";
        String jwtToken = jwtService.generateToken(userId, email, Role.RECRUITER.name());
        assertNotNull(jwtToken);
        AuthenticatedUser authenticatedUser = jwtService.extractAuthenticatedUser(jwtToken);
        assertNotNull(authenticatedUser);
        assertEquals(userId, authenticatedUser.userId());
        assertEquals(email, authenticatedUser.email());
        assertEquals(Role.RECRUITER, authenticatedUser.role());
    }

    @Test
    public void shouldThrowJwtExceptionWhenJwtTokenIsExpired() {
        jwtService = new JwtService(testJwtSecret, -3000);
        UUID userId = UUID.randomUUID();
        String email = "testuser@hireflow.com";
        String token = jwtService.generateToken(userId, email, Role.RECRUITER.name());
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractAuthenticatedUser(token));
    }

    @Test
    public void shouldThrowJwtExceptionWhenJwtTokenIsTampered() {
        jwtService = new JwtService(testJwtSecret, testJwtExpirationInMs);
        UUID userId = UUID.randomUUID();
        String email = "testuser@hireflow.com";
        String token = jwtService.generateToken(userId, email, Role.RECRUITER.name());
        String tamperedToken = token + "kjghjfjsd";
        assertThrows(SignatureException.class, () -> jwtService.extractAuthenticatedUser(tamperedToken));
    }
}
