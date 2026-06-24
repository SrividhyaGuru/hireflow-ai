package com.hireflow.auth.dto;

import com.hireflow.auth.entity.Role;

import java.util.UUID;

public record LoginResponse(UUID userId, String username,
                            String email, Role role,
                            String accessToken, String refreshToken) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private UUID userId;
        private String username;
        private String email;
        private Role role;
        private String accessToken;
        private String refreshToken;


        public Builder withUserId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder withAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder withRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(userId, username, email, role, accessToken, refreshToken);
        }
    }
}
