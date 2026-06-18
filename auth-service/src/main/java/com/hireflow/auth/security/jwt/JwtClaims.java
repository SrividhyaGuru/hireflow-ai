package com.hireflow.auth.security.jwt;

import java.util.UUID;

public record JwtClaims(UUID userId, String email, String role) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private UUID userId;
        private String email;
        private String role;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public JwtClaims build() {
            return new JwtClaims(userId, email, role);
        }
    }
}
