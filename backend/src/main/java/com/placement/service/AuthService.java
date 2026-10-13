package com.placement.service;

import com.placement.dto.AuthDTO;
import com.placement.entity.*;
import com.placement.repository.CompanyRepository;
import com.placement.repository.RecruiterProfileRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.repository.UserRepository;
import com.placement.security.JwtUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       CompanyRepository companyRepository,
                       RecruiterProfileRepository recruiterProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public AuthDTO.JwtResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new AuthDTO.JwtResponse(jwt, user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional
    public User register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already registered!");
        }

        Role assignedRole;
        if ("RECRUITER".equalsIgnoreCase(request.getRole())) {
            assignedRole = Role.RECRUITER;
        } else {
            // Default to STUDENT. Disallow public ADMIN registration
            assignedRole = Role.STUDENT;
        }

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhoneNumber(),
                assignedRole
        );
        user = userRepository.save(user);

        if (assignedRole == Role.STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(user);
            profile.setRegistrationNumber("REG-" + System.currentTimeMillis() % 1000000);
            profile.setCollege("University Institute of Technology");
            profile.setDepartment("Computer Science and Engineering");
            profile.setDegree("B.Tech");
            profile.setGraduationYear(2026);
            profile.setCgpa(BigDecimal.valueOf(8.00));
            profile.setProfileCompletionPercentage(25);
            studentProfileRepository.save(profile);
        } else if (assignedRole == Role.RECRUITER) {
            Company company;
            if (request.getCompanyName() != null && !request.getCompanyName().isBlank()) {
                company = companyRepository.findByName(request.getCompanyName())
                        .orElseGet(() -> companyRepository.save(new Company(
                                request.getCompanyName(), "", "Technology", "India", "Registered Partner", "")));
            } else {
                company = companyRepository.findByName("TechCorp Solutions")
                        .orElseGet(() -> companyRepository.save(new Company(
                                "TechCorp Solutions", "", "IT Services", "India", "Technology Services", "")));
            }
            RecruiterProfile recruiterProfile = new RecruiterProfile(
                    user, company,
                    request.getDesignation() != null ? request.getDesignation() : "Talent Acquisition",
                    request.getPhoneNumber());
            recruiterProfileRepository.save(recruiterProfile);
        }

        return user;
    }

    @Transactional
    public void changePassword(Long userId, AuthDTO.ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Initialize Default Admin securely if not present
    @Bean
    public CommandLineRunner initDefaultAdmin() {
        return args -> {
            if (userRepository.countByRole(Role.ADMIN) == 0) {
                User admin = new User(
                        "admin@placement.edu",
                        passwordEncoder.encode("Admin@123"),
                        "Chief Placement Officer",
                        "+91-9876543210",
                        Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println(">>> [SECURITY INITIALIZED] Default Administrator created: admin@placement.edu / Admin@123");
            }
        };
    }
}
