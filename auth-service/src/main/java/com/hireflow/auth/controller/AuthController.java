package com.hireflow.auth.controller;

import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.dto.RegistrationResponse;
import com.hireflow.auth.exception.security.InvalidRefreshTokenException;
import com.hireflow.auth.service.AuthService;
import com.hireflow.auth.service.UserLoginService;
import com.hireflow.auth.service.UserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final UserLoginService userLoginService;
    private final AuthService authService;

    public AuthController(UserRegistrationService userRegistrationService, UserLoginService userLoginService,
                          AuthService authService) {
        this.userRegistrationService = userRegistrationService;
        this.userLoginService = userLoginService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        RegistrationResponse response = userRegistrationService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        AuthResponse authResponse = userLoginService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("X-Refresh-Token") String token) {
        if (token.isBlank()) {
            throw new InvalidRefreshTokenException();
        }
        AuthResponse authResponse = authService.refresh(token);
        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }

    @GetMapping("/test")
    public Object test() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getPrincipal();
    }
}
