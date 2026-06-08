package com.hireflow.auth.businessexception;

public class UserNameTakenException extends RuntimeException {
    public UserNameTakenException() {
        super("Username is already taken");
    }
}
