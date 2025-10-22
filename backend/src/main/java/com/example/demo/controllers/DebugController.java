package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/user/{email}")
    public ResponseEntity<Map<String, Object>> checkUser(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("exists", true);
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("hasPassword", user.getPassword() != null && !user.getPassword().isEmpty());
            
            // Test password matching
            boolean passwordMatches = passwordEncoder.matches("1234", user.getPassword());
            response.put("passwordMatches", passwordMatches);
            response.put("encodedPassword", user.getPassword().substring(0, 20) + "...");
            
        } else {
            response.put("exists", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", userRepository.count());
        response.put("message", "Total users in database");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-login/{email}")
    public ResponseEntity<Map<String, Object>> testDirectLogin(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Test password verification
                boolean passwordMatches = passwordEncoder.matches("1234", user.getPassword());
                
                response.put("userExists", true);
                response.put("passwordMatches", passwordMatches);
                response.put("userDetails", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                ));
                
                if (passwordMatches) {
                    response.put("authenticationSuccess", true);
                    response.put("message", "Direct authentication would succeed");
                } else {
                    response.put("authenticationSuccess", false);
                    response.put("message", "Password mismatch");
                }
                
            } else {
                response.put("userExists", false);
                response.put("message", "User not found");
            }
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/direct-login")
    public ResponseEntity<Map<String, Object>> directLogin(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                if (passwordEncoder.matches(password, user.getPassword())) {
                    // Create a mock JWT token for testing
                    String mockToken = "mock_jwt_token_" + System.currentTimeMillis();
                    
                    response.put("success", true);
                    response.put("token", mockToken);
                    response.put("id", user.getId());
                    response.put("name", user.getName());
                    response.put("email", user.getEmail());
                    response.put("role", user.getRole());
                    response.put("message", "Direct login successful (bypass mode)");
                    
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid password");
                }
            } else {
                response.put("success", false);
                response.put("message", "User not found");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}