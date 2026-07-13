package com.hireflow.auth.service;

import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.dto.RegistrationResponse;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.business.InvalidRoleException;
import com.hireflow.auth.exception.business.UserAlreadyExistsByEmailException;
import com.hireflow.auth.exception.business.UserNameTakenException;
import com.hireflow.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private RegistrationRequest registrationRequest;

    String validUsername = "username";
    String validPassword = "password";
    String validMail = "random@email.com";
    String encodedPassword = "jhghjghaghdsgajh";
    @Spy
    User stubbedPersistedUser = User.builder()
            .email(validMail)
            .username(validUsername)
            .role(Role.RECRUITER)
            .password(encodedPassword).build();



    @Test
    public void shouldValidateAndSaveNewUserSuccessfully(){
        registrationRequest = new RegistrationRequest(validUsername, validPassword,
                validMail, Role.RECRUITER);
        Mockito.when(userRepository.findByEmail(validMail)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(validPassword)).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(stubbedPersistedUser);
        UUID mockedUserId = UUID.randomUUID();
        Mockito.when(stubbedPersistedUser.getId()).thenReturn(mockedUserId);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);


        RegistrationResponse registrationResponse = userRegistrationService.register(registrationRequest);

        Mockito.verify(userRepository).findByEmail(validMail);
        Mockito.verify(userRepository).findByUsername(validUsername);
        Mockito.verify(passwordEncoder).encode(validPassword);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());

        assertNotNull(registrationResponse);
        assertEquals(mockedUserId, registrationResponse.userId());
        assertEquals(validUsername, registrationResponse.username());
        assertEquals(Role.RECRUITER, registrationResponse.role());
        assertEquals(validMail, registrationResponse.email());

        User persistedUser = userArgumentCaptor.getValue();
        assertNotNull(persistedUser);
        assertEquals(validUsername, persistedUser.getUsername());
        assertEquals(validMail, persistedUser.getEmail());
        assertEquals(Role.RECRUITER, persistedUser.getRole());
        assertEquals(encodedPassword, persistedUser.getPassword());

    }

    @Test
    public void shouldThrowExceptionWhenRoleIsAdmin(){
        registrationRequest = new RegistrationRequest(validUsername, validPassword, validMail, Role.ADMIN);

        InvalidRoleException invalidRoleException = assertThrows(InvalidRoleException.class, () -> userRegistrationService.register(registrationRequest));

        assertEquals("Invalid role", invalidRoleException.getMessage());
        Mockito.verifyNoInteractions(userRepository, passwordEncoder);

    }

    @Test
    public void shouldThrowExceptionWhenUserEmailIsAlreadyUsed(){
        registrationRequest = new RegistrationRequest(validUsername, validPassword, validMail, Role.RECRUITER);

        Mockito.when(userRepository.findByEmail(validMail)).thenReturn(Optional.of(Mockito.mock(User.class)));

        UserAlreadyExistsByEmailException userAlreadyExistsByEmailException = assertThrows(UserAlreadyExistsByEmailException.class,
                () -> userRegistrationService.register(registrationRequest));

        assertEquals("Email is already registered", userAlreadyExistsByEmailException.getMessage());
        Mockito.verify(userRepository, Mockito.never()).findByUsername(validUsername);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void shouldThrowExceptionWhenUserUsernameIsAlreadyUsed(){
        registrationRequest = new RegistrationRequest(validUsername, validPassword, validMail, Role.RECRUITER);

        Mockito.when(userRepository.findByEmail(validMail)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(Mockito.mock(User.class)));

        UserNameTakenException userNameTakenException = assertThrows(UserNameTakenException.class,
                () -> userRegistrationService.register(registrationRequest));

        assertEquals("Username is already taken", userNameTakenException.getMessage());
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(validMail);
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(validUsername);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
        Mockito.verifyNoInteractions(passwordEncoder);
    }
}
