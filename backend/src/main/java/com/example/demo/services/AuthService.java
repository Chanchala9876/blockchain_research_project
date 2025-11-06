package com.example.demo.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.models.User;
import com.example.demo.models.Professor;
import com.example.demo.models.Admin;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtils;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                      UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Debug logging
            System.out.println("=== AUTHENTICATION ATTEMPT ===");
            System.out.println("Email: " + loginRequest.getEmail());
            System.out.println("AuthenticationManager: " + (authenticationManager != null ? "Available" : "NULL"));
            
            if (authenticationManager == null) {
                throw new RuntimeException("AuthenticationManager is not initialized");
            }
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            
            System.out.println("Authentication successful: " + authentication.isAuthenticated());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println("UserDetails type: " + userDetails.getClass().getSimpleName());
            
            // Handle different user types
            String id, name, email, role;
            
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                id = user.getId();
                name = user.getName();
                email = user.getEmail();
                role = user.getRole();
            } else if (userDetails instanceof Professor) {
                Professor professor = (Professor) userDetails;
                id = professor.getId();
                name = professor.getName();
                email = professor.getEmail();
                role = "ROLE_PROFESSOR";
            } else if (userDetails instanceof Admin) {
                Admin admin = (Admin) userDetails;
                id = admin.getId();
                name = admin.getName();
                email = admin.getEmail();
                role = admin.getRole();
            } else {
                throw new RuntimeException("Unknown user type: " + userDetails.getClass().getSimpleName());
            }
            
            System.out.println("JWT Token generated successfully");
            
            return JwtResponse.builder()
                    .token(jwt)
                    .id(id)
                    .name(name)
                    .email(email)
                    .role(role)
                    .build();
                    
        } catch (Exception e) {
            System.err.println("=== AUTHENTICATION ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public UserResponse registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        
        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .institute(signupRequest.getInstitute())
                .subject(signupRequest.getSubject())
                .role(signupRequest.getRole())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        User savedUser = userRepository.save(user);
        
        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .institute(savedUser.getInstitute())
                .subject(savedUser.getSubject())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
    
    public UserResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .institute(user.getInstitute())
                .subject(user.getSubject())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}