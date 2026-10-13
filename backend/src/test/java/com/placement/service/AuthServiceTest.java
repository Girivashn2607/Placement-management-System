package com.placement.service;

import com.placement.dto.AuthDTO;
import com.placement.entity.Role;
import com.placement.entity.User;
import com.placement.repository.CompanyRepository;
import com.placement.repository.RecruiterProfileRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.repository.UserRepository;
import com.placement.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    private UserRepository userRepository;
    private StudentProfileRepository studentProfileRepository;
    private CompanyRepository companyRepository;
    private RecruiterProfileRepository recruiterProfileRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private AuthService authService;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        studentProfileRepository = Mockito.mock(StudentProfileRepository.class);
        companyRepository = Mockito.mock(CompanyRepository.class);
        recruiterProfileRepository = Mockito.mock(RecruiterProfileRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        jwtUtils = null; // not invoked during register or password validation

        authService = new AuthService(
                userRepository, studentProfileRepository, companyRepository,
                recruiterProfileRepository, passwordEncoder, authenticationManager, jwtUtils);
    }

    @Test
    public void testPublicRegistrationDisallowsAdmin() {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setEmail("hacker@example.com");
        request.setPassword("SecretPass");
        request.setFullName("Malicious User");
        request.setRole("ADMIN"); // Attempting to register as ADMIN publicly!

        when(userRepository.existsByEmail("hacker@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = authService.register(request);

        // Security check: Must NOT be ADMIN, defaults safely to STUDENT
        assertEquals(Role.STUDENT, registered.getRole(), "Public registration must never allow ADMIN role");
    }

    @Test
    public void testDuplicateEmailThrowsException() {
        AuthDTO.RegisterRequest request = new AuthDTO.RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }
}
