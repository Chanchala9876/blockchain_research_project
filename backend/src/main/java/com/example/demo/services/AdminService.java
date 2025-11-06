package com.example.demo.services;

import com.example.demo.dto.AdminRegistrationRequest;
import com.example.demo.dto.AdminResponse;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Admin;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.security.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final OtpService otpService;
    
    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, 
                       JwtUtils jwtUtils, OtpService otpService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.otpService = otpService;
    }
    
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    public JwtResponse authenticateAdmin(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        Admin admin = (Admin) authentication.getPrincipal();
        
        return JwtResponse.builder()
                .token(jwt)
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }
    
    public AdminResponse registerAdmin(AdminRegistrationRequest request) {
        // Check if admin already exists
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Admin with this email already exists");
        }
        
        // Create new admin
        Admin admin = Admin.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName() != null ? request.getName() : "System Admin")
                .role(request.getRole() != null ? request.getRole() : "ADMIN")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin registered successfully with email: {}", request.getEmail());
        
        return mapAdminToResponse(savedAdmin);
    }
    
    public AdminResponse getAdminById(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapAdminToResponse(admin);
    }
    
    public AdminResponse getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapAdminToResponse(admin);
    }
    
    public Page<AdminResponse> getAllAdmins(Pageable pageable) {
        Page<Admin> admins = adminRepository.findAll(pageable);
        return admins.map(this::mapAdminToResponse);
    }
    
    public Page<AdminResponse> getActiveAdmins(Pageable pageable) {
        Page<Admin> admins = adminRepository.findByIsActive(true, pageable);
        return admins.map(this::mapAdminToResponse);
    }
    
    public Page<AdminResponse> getAdminsByRole(String role, Pageable pageable) {
        Page<Admin> admins = adminRepository.findByRole(role, pageable);
        return admins.map(this::mapAdminToResponse);
    }
    
    public AdminResponse updateAdmin(String id, AdminRegistrationRequest request) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        // Check if email is being changed and if new email already exists
        if (!admin.getEmail().equals(request.getEmail()) && 
            adminRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Admin with this email already exists");
        }
        
        // Update admin fields
        admin.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getName() != null) {
            admin.setName(request.getName());
        }
        if (request.getRole() != null) {
            admin.setRole(request.getRole());
        }
        admin.setUpdatedAt(LocalDateTime.now());
        
        Admin updatedAdmin = adminRepository.save(admin);
        log.info("Admin updated successfully with ID: {}", id);
        
        return mapAdminToResponse(updatedAdmin);
    }
    
    public void deactivateAdmin(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        admin.setActive(false);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        log.info("Admin deactivated successfully with ID: {}", id);
    }
    
    public void activateAdmin(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        admin.setActive(true);
        admin.setUpdatedAt(LocalDateTime.now());
        adminRepository.save(admin);
        
        log.info("Admin activated successfully with ID: {}", id);
    }
    
    public void updateLastLogin(String email, String ipAddress) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        admin.setLastLoginIp(ipAddress);
        admin.setLastLoginTime(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        
        adminRepository.save(admin);
        log.info("Admin last login updated for email: {}", email);
    }
    
    public long getActiveAdminCount() {
        return adminRepository.countByIsActive(true);
    }
    
    // OTP-related methods for two-factor authentication
    public Map<String, Object> validateAdminCredentials(LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find admin by email
            Admin admin = adminRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            
            // Check if admin is active
            if (!admin.isActive()) {
                throw new RuntimeException("Admin account is deactivated");
            }
            
            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            
            // Generate and send OTP
            String adminId = admin.getEmail().split("@")[0];
            String otp = otpService.generateAndSendOtp(adminId);
            
            response.put("valid", true);
            response.put("message", "OTP sent to registered mobile number");
            response.put("adminId", adminId);
            response.put("expiryTime", otpService.getOtpExpiry(adminId));
            
            // For demo purposes only - remove in production
            response.put("demoOtp", otp);
            
            log.info("OTP sent for admin: {}", admin.getEmail());
            
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", e.getMessage());
            log.error("Admin credential validation failed: {}", e.getMessage());
        }
        
        return response;
    }
    
    public JwtResponse authenticateAdminWithOtp(String adminId, String password, String otp) {
        // First validate the OTP
        if (!otpService.validateOtp(adminId, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        // Then authenticate normally
        String email = adminId + "@jnu.ac.in";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        // Get UserDetails and ensure it's an Admin
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!(userDetails instanceof Admin)) {
            throw new RuntimeException("Authentication failed: Not an admin user");
        }
        
        Admin admin = (Admin) userDetails;
        
        log.info("Admin authenticated successfully with OTP: {}", admin.getEmail());
        
        return JwtResponse.builder()
                .token(jwt)
                .id(admin.getId())
                .name(admin.getName())
                .email(admin.getEmail())
                .role(admin.getRole())
                .build();
    }
    
    public Map<String, Object> verifyOtp(String adminId, String otp) {
        Map<String, Object> response = new HashMap<>();
        
        boolean isValid = otpService.validateOtp(adminId, otp);
        
        response.put("valid", isValid);
        response.put("message", isValid ? "OTP verified successfully" : "Invalid or expired OTP");
        
        return response;
    }
    
    private AdminResponse mapAdminToResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .name(admin.getName())
                .role(admin.getRole())
                .isActive(admin.isActive())
                .lastLoginIp(admin.getLastLoginIp())
                .lastLoginTime(admin.getLastLoginTime())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }
}