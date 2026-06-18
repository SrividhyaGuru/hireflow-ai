package com.hireflow.auth.service;

import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.dto.LoginResponse;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.business.UserLoginException;
import com.hireflow.auth.repository.UserRepository;
import com.hireflow.auth.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User registeredUser  = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(UserLoginException::new);
            verifyPassword(loginRequest, registeredUser);
            return LoginResponse.builder()
                    .withUserId(registeredUser.getId())
                    .withUsername(registeredUser.getUsername())
                    .withEmail(registeredUser.getEmail())
                    .withRole(registeredUser.getRole())
                    .withAccessToken(jwtService.generateToken(registeredUser.getId(),
                            registeredUser.getEmail(), registeredUser.getRole().name()))
                    .build();

    }

    private void verifyPassword(LoginRequest loginRequest, User registeredUser) {
         if (!(passwordEncoder.matches(loginRequest.password(), registeredUser.getPassword()))){
             throw new UserLoginException();
         }
    }

}
