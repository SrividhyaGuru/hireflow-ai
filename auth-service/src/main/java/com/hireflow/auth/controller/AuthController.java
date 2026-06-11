package com.hireflow.auth.controller;

import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.dto.LoginResponse;
import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.dto.RegistrationResponse;
import com.hireflow.auth.service.UserLoginService;
import com.hireflow.auth.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;

    public AuthController(UserRegistrationService userRegistrationService, UserLoginService userLoginService) {
        this.userRegistrationService = userRegistrationService;
        this.userLoginService = userLoginService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        RegistrationResponse response = userRegistrationService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = userLoginService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
