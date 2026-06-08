package com.hireflow.auth.businessexception;

public class UserAlreadyExistsByEmailException extends RuntimeException {
    public UserAlreadyExistsByEmailException() {
        super("Email is already registered");
    }
}
