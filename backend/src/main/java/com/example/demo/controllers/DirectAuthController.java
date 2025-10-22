package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.services.AuthService;
import com.example.demo.utils.DatabaseMigrationUtil;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller to handle direct authentication requests from React frontend
 * This provides endpoints without the /api/auth prefix for compatibility
 */
@RestController
public class DirectAuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DatabaseMigrationUtil migrationUtil;
    
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        UserResponse user = authService.registerUser(signupRequest);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/login")
    public ResponseEntity<String> loginInfo() {
        return ResponseEntity.ok("Login endpoint - use POST method with email and password");
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody Map<String, String> loginData) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            // Ignore department and institute - only use email and password for authentication
            
            System.out.println("=== DIRECT LOGIN ATTEMPT ===");
            System.out.println("Email: " + email);
            System.out.println("Password provided: " + (password != null && !password.isEmpty()));
            
            if (email == null || password == null) {
                System.out.println("❌ Email or password is null");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Email and password are required");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // First, try to find in User collection
            Optional<User> userOpt = userRepository.findByEmail(email);
            System.out.println("User found: " + userOpt.isPresent());
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("Found user: " + user.getName() + " (" + user.getEmail() + ")");
                
                boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
                System.out.println("Password matches: " + passwordMatches);
                
                if (passwordMatches) {
                    // Generate a simple JWT token
                    String token = "jwt_token_" + System.currentTimeMillis() + "_" + user.getId();
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("role", user.getRole());
                    
                    System.out.println("✅ User login successful for: " + email);
                    return ResponseEntity.ok(response);
                    
                } else {
                    System.out.println("❌ Invalid password for user: " + email);
                }
            } else {
                System.out.println("❌ User not found, checking professors: " + email);
                
                // Debug professor repository
                try {
                    System.out.println("Professor repository exists: " + (professorRepository != null));
                    if (professorRepository != null) {
                        long profCount = professorRepository.count();
                        System.out.println("Total professors in database: " + profCount);
                    }
                } catch (Exception e) {
                    System.out.println("Error checking professor count: " + e.getMessage());
                }
                
                // If not found in User collection, try Professor collection
                Optional<com.example.demo.models.Professor> professorOpt = null;
                try {
                    professorOpt = professorRepository.findByEmail(email);
                    System.out.println("Professor found: " + professorOpt.isPresent());
                } catch (Exception e) {
                    System.out.println("Error finding professor: " + e.getMessage());
                    professorOpt = Optional.empty();
                }
                
                if (professorOpt.isPresent()) {
                    com.example.demo.models.Professor professor = professorOpt.get();
                    System.out.println("Found professor: " + professor.getName() + " (" + professor.getEmail() + ")");
                    
                    // Check if professor is active
                    if (!professor.isActive()) {
                        System.out.println("❌ Professor account is deactivated: " + email);
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", true);
                        errorResponse.put("message", "Account is deactivated");
                        return ResponseEntity.status(401).body(errorResponse);
                    }
                    
                    boolean passwordMatches = passwordEncoder.matches(password, professor.getPassword());
                    System.out.println("Professor password matches: " + passwordMatches);
                    
                    if (passwordMatches) {
                        // Generate a simple JWT token
                        String token = "jwt_token_" + System.currentTimeMillis() + "_" + professor.getId();
                        
                        Map<String, Object> response = new HashMap<>();
                        response.put("token", token);
                        response.put("id", professor.getId());
                        response.put("name", professor.getName());
                        response.put("email", professor.getEmail());
                        response.put("role", "professor");
                        response.put("department", professor.getDepartmentName());
                        response.put("designation", professor.getDesignation());
                        
                        System.out.println("✅ Professor login successful for: " + email);
                        return ResponseEntity.ok(response);
                        
                    } else {
                        System.out.println("❌ Invalid password for professor: " + email);
                    }
                } else {
                    System.out.println("❌ Professor not found: " + email);
                }
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Invalid email or password");
            
            return ResponseEntity.status(401).body(errorResponse);
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Authentication failed: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/signup")
    public ResponseEntity<String> signupInfo() {
        return ResponseEntity.ok("Signup endpoint - use POST method with name, email, password, institute, and subject");
    }
    
    @GetMapping("/debug/professors")
    public ResponseEntity<Map<String, Object>> debugProfessors() {
        try {
            Map<String, Object> debug = new HashMap<>();
            debug.put("professorRepositoryExists", professorRepository != null);
            
            if (professorRepository != null) {
                long count = professorRepository.count();
                debug.put("professorCount", count);
                
                if (count > 0) {
                    var professors = professorRepository.findAll();
                    debug.put("professors", professors.stream()
                        .map(p -> Map.of(
                            "id", p.getId(),
                            "name", p.getName(),
                            "email", p.getEmail(),
                            "isActive", p.isActive(),
                            "hasPassword", p.getPassword() != null && !p.getPassword().isEmpty()
                        ))
                        .toList());
                }
            }
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("class", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/debug/create-professor")
    public ResponseEntity<Map<String, Object>> createTestProfessor() {
        try {
            // Create a simple test professor
            com.example.demo.models.Professor testProf = com.example.demo.models.Professor.builder()
                .name("Dr. Test Professor")
                .email("test.prof@jnu.ac.in")
                .password(passwordEncoder.encode("1234"))
                .departmentName("Computer Science")
                .designation("Professor")
                .isActive(true)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
                
            professorRepository.save(testProf);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Test professor created");
            result.put("email", "test.prof@jnu.ac.in");
            result.put("password", "1234");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/debug/plagiarism-test")
    public ResponseEntity<Map<String, Object>> testPlagiarismCalculation() {
        try {
            Map<String, Object> results = new HashMap<>();
            
            // Test plagiarism calculation for different similarity scores
            double[] similarities = {50.0, 70.0, 80.0, 90.0, 95.0, 98.0, 100.0};
            
            for (double sim : similarities) {
                // Simple direct calculation
                double plagiarism = sim >= 95.0 ? 100.0 : 
                                   sim >= 90.0 ? 95.0 + (sim - 90.0) :
                                   sim >= 80.0 ? 85.0 + (sim - 80.0) :
                                   sim >= 70.0 ? 70.0 + (sim - 70.0) :
                                   sim >= 50.0 ? 50.0 + (sim - 50.0) : sim;
                
                results.put("similarity_" + sim + "%", "plagiarism_" + plagiarism + "%");
            }
            
            results.put("explanation", "High similarity should result in high plagiarism score");
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/debug/mock-verification")
    public ResponseEntity<Map<String, Object>> mockVerification(@RequestBody Map<String, Object> request) {
        try {
            // Mock a verification response with high similarity
            double similarity = 98.5; // High similarity
            double plagiarism = similarity; // Direct correlation
            
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("verified", true);
            mockResponse.put("similarityScore", similarity);
            mockResponse.put("plagiarismScore", plagiarism);
            mockResponse.put("matchType", "IDENTICAL_CONTENT");
            mockResponse.put("message", String.format("🚨 SEVERE PLAGIARISM DETECTED: %.1f%% similarity. Plagiarism score: %.1f%%", similarity, plagiarism));
            
            Map<String, Object> paper = new HashMap<>();
            paper.put("id", "test123");
            paper.put("title", "Test Research Paper");
            paper.put("author", "Dr. Test Author");
            paper.put("department", "Computer Science");
            mockResponse.put("paper", paper);
            
            Map<String, Object> aiAnalysis = new HashMap<>();
            aiAnalysis.put("embeddingScore", similarity / 100.0);
            aiAnalysis.put("titleSimilarity", 0.95);
            aiAnalysis.put("contentSimilarity", similarity / 100.0);
            mockResponse.put("aiAnalysis", aiAnalysis);
            
            return ResponseEntity.ok(mockResponse);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/debug/migrate-professors")
    public ResponseEntity<Map<String, Object>> migrateProfessorInstitutes() {
        try {
            migrationUtil.updateProfessorsWithInstitute();
            migrationUtil.logInstituteInfo();
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Professor institute migration completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}