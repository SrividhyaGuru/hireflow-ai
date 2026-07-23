package com.hireflow.auth.integration.util;

import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.entity.Role;

public class TestDataFactory {

    public static RegistrationRequest validRegistrationRequest() {
        return new RegistrationRequest(
                "username",
                "password",
                "test@hireflow.com",
                Role.RECRUITER
        );
    }

    public static RegistrationRequest adminRegistrationRequest() {
        return new RegistrationRequest(
                "username",
                "password",
                "test@hireflow.com",
                Role.ADMIN
        );
    }

}
