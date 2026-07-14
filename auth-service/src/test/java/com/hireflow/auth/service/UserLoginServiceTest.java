package com.hireflow.auth.service;

import com.hireflow.auth.dto.AuthResponse;
import com.hireflow.auth.dto.LoginRequest;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.exception.business.UserLoginException;
import com.hireflow.auth.repository.UserRepository;
import com.hireflow.auth.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private UserLoginService userLoginService;

    private String validEmail = "test@hireflow.com";
    private String invalidEmail = "testInvalid@hireflow.com";
    private String validPassword = "testValidPassword";
    private String invalidPassword = "testInvalidPassword";
    private String hashedPassword = "fssd##E#FF";
    private String validUsername = "testValidUsername";
    private UUID userId = UUID.randomUUID();
    private String validJwtAccessToken = "jghj678gjb7fvcgcjhjgjhgj";
    private String validRefreshToken = "hjgj78tgj87323cqahj78";

    @Spy
    private User user = new User.Builder()
            .email(validEmail)
            .password(hashedPassword)
            .role(Role.RECRUITER)
            .username(validUsername).build();

    @Test
    public void shouldLoginUserSuccessfully() {
        LoginRequest loginRequest = new LoginRequest(validEmail, validPassword);

        Mockito.when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(validPassword, hashedPassword)).thenReturn(true);
        Mockito.when(user.getId()).thenReturn(userId);
        Mockito.when(jwtService.generateToken(userId,user.getEmail(), user.getRole().name())).thenReturn(validJwtAccessToken);
        Mockito.when(refreshTokenService.generateAndSaveRefreshToken(user)).thenReturn(validRefreshToken);

        AuthResponse authResponse = userLoginService.login(loginRequest);

        Mockito.verify(userRepository).findByEmail(validEmail);
        Mockito.verify(passwordEncoder).matches(validPassword, hashedPassword);
        Mockito.verify(jwtService).generateToken(userId,user.getEmail(), user.getRole().name());
        Mockito.verify(refreshTokenService).generateAndSaveRefreshToken(user);
        Mockito.verifyNoMoreInteractions(userRepository, passwordEncoder);

        assertEquals(userId, authResponse.userId());
        assertEquals(user.getUsername(), authResponse.username());
        assertEquals(user.getEmail(), authResponse.email());
        assertEquals(user.getRole(), authResponse.role());
        assertEquals(validJwtAccessToken, authResponse.accessToken());
        assertEquals(validRefreshToken, authResponse.refreshToken());

    }

    @Test
    public void shouldThrowUserLoginExceptionWhenInvalidEmail() {
        LoginRequest loginRequest = new LoginRequest(invalidEmail, validPassword);

        Mockito.when(userRepository.findByEmail(invalidEmail)).thenReturn(Optional.empty());

        UserLoginException userLoginException = assertThrows(UserLoginException.class, () -> userLoginService.login(loginRequest));

        assertEquals("User Credentials are invalid", userLoginException.getMessage());
        Mockito.verify(userRepository).findByEmail(invalidEmail);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(passwordEncoder, jwtService, refreshTokenService);

    }

    @Test
    public void shouldThrowUserLoginExceptionWhenValidEmailAndInvalidPassword() {
        LoginRequest loginRequest = new LoginRequest(validEmail, invalidPassword);

        Mockito.when(userRepository.findByEmail(validEmail)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(loginRequest.password(), hashedPassword)).thenReturn(false);

        UserLoginException userLoginException = assertThrows(UserLoginException.class, () -> userLoginService.login(loginRequest));

        assertEquals("User Credentials are invalid", userLoginException.getMessage());
        Mockito.verify(userRepository).findByEmail(validEmail);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verify(passwordEncoder).matches(loginRequest.password(), hashedPassword);
        Mockito.verifyNoInteractions(jwtService, refreshTokenService);
    }

}
