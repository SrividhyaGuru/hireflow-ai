package com.hireflow.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false, name = "token_hash")
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false, name = "issued_at")
    private Instant issuedAt;

    @Column(nullable = false, updatable = false, name = "expires_at")
    private Instant expiresAt;

    @Column(nullable = false, name = "revoked")
    private boolean revoked;

    public void revoke() {
        this.revoked = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RefreshToken refreshToken = new RefreshToken();

        public Builder tokenHash(String tokenHash) {
            this.refreshToken.tokenHash = tokenHash;
            return this;
        }
        public Builder user(User user) {
            this.refreshToken.user = user;
            return this;
        }
        public Builder issuedAt(Instant issuedAt) {
            this.refreshToken.issuedAt = issuedAt;
            return this;
        }
        public Builder expiresAt(Instant expiresAt) {
            this.refreshToken.expiresAt = expiresAt;
            return this;
        }
        public Builder revoked(boolean revoked) {
            this.refreshToken.revoked = revoked;
            return this;
        }
        public RefreshToken build() {
            return this.refreshToken;
        }
    }
}
