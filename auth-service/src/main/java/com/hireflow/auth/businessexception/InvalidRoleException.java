package com.hireflow.auth.businessexception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Invalid role");
    }
}
