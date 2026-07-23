package com.hireflow.auth.integration;

import com.hireflow.auth.dto.RegistrationRequest;
import com.hireflow.auth.entity.Role;
import com.hireflow.auth.entity.User;
import com.hireflow.auth.integration.util.TestDataFactory;
import com.hireflow.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.hireflow.auth.integration.util.JsonUtil.toJson;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class RegistrationIntegrationTest extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegistrationRequest request = TestDataFactory.validRegistrationRequest();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value(request.email()))
                .andExpect(jsonPath("$.username").value(request.username()))
                .andExpect(jsonPath("$.role").value(request.role().name()));

        User registeredUser = userRepository.findByEmail(request.email()).orElseThrow(
                () -> new AssertionError("Registered user was not persisted"));

        assertEquals(request.email(), registeredUser.getEmail());
        assertEquals(request.username(), registeredUser.getUsername());
        assertEquals(request.role(), registeredUser.getRole());
        assertNotEquals(request.password(), registeredUser.getPassword());
        assertTrue(passwordEncoder.matches(request.password(), registeredUser.getPassword()));

    }

    @Test
    public void shouldRejectUserRegistrationIfDuplicateEmail() throws Exception {
        RegistrationRequest duplicateEmailUserRequest = TestDataFactory.validRegistrationRequest();
        User existingUser = User.builder().email(duplicateEmailUserRequest.email())
                .username("different_username")
                .password(passwordEncoder.encode("different_password"))
                .role(Role.RECRUITER)
                .build();
        userRepository.save(existingUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(duplicateEmailUserRequest))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors").value("Email is already registered"));

        assertTrue(userRepository.findByUsername(duplicateEmailUserRequest.username()).isEmpty());
    }

    @Test
    public void shouldRejectUserRegistrationIfDuplicateUsername() throws Exception {
        RegistrationRequest duplicateUsernameUserRequest = TestDataFactory.validRegistrationRequest();
        User existingUser = User.builder().email("different.email@hireflow.com")
                .username(duplicateUsernameUserRequest.username())
                .password(passwordEncoder.encode("different_password"))
                .role(Role.RECRUITER)
                .build();
        userRepository.save(existingUser);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(duplicateUsernameUserRequest))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors").value("Username is already taken"));
        ;

        assertTrue(userRepository.findByEmail(duplicateUsernameUserRequest.email()).isEmpty());
    }

    @Test
    public void shouldRejectUserRegistrationWithAdminRole() throws Exception {
        RegistrationRequest registrationRequest = TestDataFactory.adminRegistrationRequest();


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(registrationRequest))).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").value("Invalid role"));
        ;

        assertTrue(userRepository.findByEmail(registrationRequest.email()).isEmpty());
    }
}
