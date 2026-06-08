package com.hireflow.auth.service;

import com.hireflow.auth.businessexception.InvalidRoleException;
import com.hireflow.auth.businessexception.UserAlreadyExistsByEmailException;
import com.hireflow.auth.businessexception.UserNameTakenException;
import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.dto.RegistrationResponse;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationResponse register(RegistrationRequest registrationRequest) {
        validateUserRegistration(registrationRequest);
        return null;
    }


    private void validateUserRegistration(RegistrationRequest userRequest) {
        validateRole(userRequest);
        checkForDuplicateEmail(userRequest);
        checkForDuplicateUserName(userRequest);
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

    private void checkForDuplicateUserName(RegistrationRequest userRequest) {
         userRepository.findByUsername(userRequest.username()).ifPresent(existingUser -> {
            throw new UserNameTakenException();
        });
    }


}
