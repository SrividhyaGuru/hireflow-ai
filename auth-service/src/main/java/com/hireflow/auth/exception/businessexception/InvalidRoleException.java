package com.hireflow.auth.exception.businessexception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Invalid role");
    }
}
