package com.hireflow.auth.service;

import com.hireflow.auth.exception.business.InvalidRoleException;
import com.hireflow.auth.exception.business.UserAlreadyExistsByEmailException;
import com.hireflow.auth.exception.business.UserNameTakenException;
import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.dto.RegistrationResponse;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) {
        validateUserRegistration(registrationRequest);
        User persistedUser = userRepository.save(mapToUserEntity(registrationRequest));
        return buildRegistrationResponse(persistedUser);
    }

    private RegistrationResponse buildRegistrationResponse(User persistedUser) {
        return new RegistrationResponse(persistedUser.getId(),
                persistedUser.getUsername(),
                persistedUser.getEmail(),
                persistedUser.getRole());
    }

    private User mapToUserEntity(RegistrationRequest registrationRequest) {
        return User.builder()
                .username(registrationRequest.username())
                .password(encodePassword(registrationRequest.password()))
                .email(registrationRequest.email())
                .role(registrationRequest.role())
                .build();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }


    private void validateUserRegistration(RegistrationRequest userRequest) {
        validateRole(userRequest);
        checkForDuplicateEmail(userRequest);
        checkForDuplicateUsername(userRequest);
    }

    private void validateRole(RegistrationRequest userRequest) {
        if(userRequest.role() == Role.ADMIN) {
            throw new InvalidRoleException();
        }
    }

    private void checkForDuplicateEmail(RegistrationRequest userRequest) {
         userRepository.findByEmail(userRequest.email()).ifPresent(existingUser -> {
            throw new UserAlreadyExistsByEmailException();
        });
    }

    private void checkForDuplicateUsername(RegistrationRequest userRequest) {
         userRepository.findByUsername(userRequest.username()).ifPresent(existingUser -> {
            throw new UserNameTakenException();
        });
    }


}
