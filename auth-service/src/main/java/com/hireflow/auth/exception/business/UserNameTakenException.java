package com.hireflow.auth.exception.business;

public class UserNameTakenException extends RuntimeException {
    public UserNameTakenException() {
        super("Username is already taken");
    }
}
