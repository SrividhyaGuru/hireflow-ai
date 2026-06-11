package com.hireflow.auth.exception.businessexception;

public class UserNameTakenException extends RuntimeException {
    public UserNameTakenException() {
        super("Username is already taken");
    }
}
