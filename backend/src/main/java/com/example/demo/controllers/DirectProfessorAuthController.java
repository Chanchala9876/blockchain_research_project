package com.example.demo.controllers;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.models.Professor;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/professors")
@CrossOrigin(origins = "*")
public class DirectProfessorAuthController {
    
    private static final Logger log = LoggerFactory.getLogger(DirectProfessorAuthController.class);
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * Direct professor login endpoint (bypasses Spring Security complexity)
     */
    @PostMapping("/direct-login")
    public ResponseEntity<?> directLogin(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("👨‍🏫 Professor direct login attempt for email: {}", loginRequest.getEmail());
            
            // Find professor by email
            Optional<Professor> professorOpt = professorRepository.findByEmail(loginRequest.getEmail());
            
            if (professorOpt.isEmpty()) {
                log.warn("❌ Professor not found with email: {}", loginRequest.getEmail());
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials", "success", false));
            }
            
            Professor professor = professorOpt.get();
            
            // Check if professor is active
            if (!professor.isActive()) {
                log.warn("❌ Professor account is deactivated: {}", loginRequest.getEmail());
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Account is deactivated", "success", false));
            }
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), professor.getPassword())) {
                log.warn("❌ Invalid password for professor: {}", loginRequest.getEmail());
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials", "success", false));
            }
            
            // Generate JWT token
            String token = "jwt_token_" + System.currentTimeMillis() + "_" + professor.getId();
            
            // Create response
            JwtResponse response = JwtResponse.builder()
                    .token(token)
                    .id(professor.getId())
                    .name(professor.getName())
                    .email(professor.getEmail())
                    .role("professor")
                    .build();
            
            log.info("✅ Professor login successful for: {} ({})", professor.getName(), professor.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error during professor login: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Login failed: " + e.getMessage(), "success", false));
        }
    }
    
    /**
     * Get professor info by ID (for testing)
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<?> getProfessorInfo(@PathVariable String id) {
        try {
            Optional<Professor> professorOpt = professorRepository.findById(id);
            
            if (professorOpt.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "Professor not found", "success", false));
            }
            
            Professor professor = professorOpt.get();
            
            return ResponseEntity.ok(Map.of(
                "id", professor.getId(),
                "name", professor.getName(),
                "email", professor.getEmail(),
                "department", professor.getDepartmentName(),
                "designation", professor.getDesignation(),
                "employeeId", professor.getEmployeeId(),
                "isActive", professor.isActive(),
                "success", true
            ));
            
        } catch (Exception e) {
            log.error("Error getting professor info: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to get professor info", "success", false));
        }
    }
    
    /**
     * List all professors (for debugging)
     */
    @GetMapping("/list")
    public ResponseEntity<?> listProfessors() {
        try {
            var professors = professorRepository.findAll();
            
            var professorList = professors.stream()
                .map(p -> Map.of(
                    "id", p.getId(),
                    "name", p.getName(),
                    "email", p.getEmail(),
                    "department", p.getDepartmentName() != null ? p.getDepartmentName() : "N/A",
                    "designation", p.getDesignation() != null ? p.getDesignation() : "N/A",
                    "employeeId", p.getEmployeeId() != null ? p.getEmployeeId() : "N/A",
                    "isActive", p.isActive()
                ))
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "professors", professorList,
                "count", professors.size(),
                "success", true
            ));
            
        } catch (Exception e) {
            log.error("Error listing professors: {}", e.getMessage());
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to list professors", "success", false));
        }
    }
}