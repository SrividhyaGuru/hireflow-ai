package com.hireflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private final User user = new User();

        public Builder username(String username){
            user.username = username;
            return this;
        }

        public Builder password(String password){
            user.password = password;
            return this;
        }
        public Builder email(String email){
            user.email = email;
            return this;
        }
        public Builder role(Role role){
            user.role = role;
            return this;
        }

        public User build(){
            return user;
        }
    }

}
