package com.hireflow.auth.service;

import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.dto.LoginResponse;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.business.UserLoginException;
import com.hireflow.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User registeredUser  = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(UserLoginException::new);
            verifyPassword(loginRequest, registeredUser);
            return new LoginResponse(registeredUser.getId(), registeredUser.getUsername(),
                    registeredUser.getEmail(),registeredUser.getRole());

    }

    private void verifyPassword(LoginRequest loginRequest, User registeredUser) {
         if (!(passwordEncoder.matches(loginRequest.password(), registeredUser.getPassword()))){
             throw new UserLoginException();
         }
    }

}
