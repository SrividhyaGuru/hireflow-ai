package com.hireflow.auth.domain;

import com.hireflow.auth.entity.Role;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String email, Role role) {

    public static Builder builder() {
        return new Builder();
    }

     public static class Builder{
         private UUID uuid;
         private String email;
         private Role role;

         public Builder userId(UUID userId) {
             this.uuid = userId;
             return this;
         }

         public Builder email(String email){
             this.email=email;
             return this;
         }

         public Builder role(String role){
             this.role=Role.valueOf(role);
             return this;
         }

         public AuthenticatedUser build(){
             return new AuthenticatedUser(uuid, email, role);
         }
     }
}
