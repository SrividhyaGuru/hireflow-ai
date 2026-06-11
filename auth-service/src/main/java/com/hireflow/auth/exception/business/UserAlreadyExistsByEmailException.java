package com.hireflow.auth.exception.business;

public class UserAlreadyExistsByEmailException extends RuntimeException {
    public UserAlreadyExistsByEmailException() {
        super("Email is already registered");
    }
}
