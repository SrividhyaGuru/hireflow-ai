package com.hireflow.auth.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private final String jwtSecret;
    private final long jwtExpirationInMs;

    public JwtService(@Value("${jwt.secret}") String jwtSecret,
                      @Value("${jwt.expiration-ms}") long jwtExpirationInMs) {
        this.jwtSecret = jwtSecret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(UUID userId, String email, String role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public JwtClaims extractJwtClaims(String token) {
        Claims claims = parseClaims(token);
        return JwtClaims.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .email(claims.get("email", String.class))
                .role(claims.get("role", String.class))
                .build();
    }

    private Claims parseClaims(String token) {
        JwtParser jwtParser = Jwts.parser().verifyWith(getSigningKey()).build();
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.jwtSecret.getBytes());
    }


}
