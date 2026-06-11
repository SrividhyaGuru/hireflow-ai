package com.hireflow.auth.exception.business;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException() {
        super("Invalid role");
    }
}
