package com.hireflow.auth.exception.business;

public class UserLoginException extends RuntimeException {
    public UserLoginException() {
        super("User Credentials are invalid");
    }
}
