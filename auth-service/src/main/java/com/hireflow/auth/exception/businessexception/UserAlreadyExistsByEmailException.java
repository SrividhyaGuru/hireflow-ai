package com.hireflow.auth.exception.businessexception;

public class UserAlreadyExistsByEmailException extends RuntimeException {
    public UserAlreadyExistsByEmailException() {
        super("Email is already registered");
    }
}
