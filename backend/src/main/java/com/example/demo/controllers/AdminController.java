package com.example.demo.controllers;

import com.example.demo.dto.AdminRegistrationRequest;
import com.example.demo.dto.AdminResponse;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.services.AdminService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    
    private final AdminService adminService;
    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateAdmin(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = adminService.validateAdminCredentials(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginAdmin(@Valid @RequestBody Map<String, String> loginRequest) {
        String adminId = loginRequest.get("adminId");
        String password = loginRequest.get("password");
        String otp = loginRequest.get("otp");
        
        JwtResponse jwtResponse = adminService.authenticateAdminWithOtp(adminId, password, otp);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        String adminId = request.get("adminId");
        String otp = request.get("otp");
        
        Map<String, Object> response = adminService.verifyOtp(adminId, otp);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminResponse> registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        AdminResponse admin = adminService.registerAdmin(request);
        return ResponseEntity.ok(admin);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminResponse> getAdminById(@PathVariable String id) {
        AdminResponse admin = adminService.getAdminById(id);
        return ResponseEntity.ok(admin);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminResponse> getAdminByEmail(@PathVariable String email) {
        AdminResponse admin = adminService.getAdminByEmail(email);
        return ResponseEntity.ok(admin);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<AdminResponse>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        
        Page<AdminResponse> admins = adminService.getAllAdmins(pageable);
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<AdminResponse>> getActiveAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name").ascending());
        Page<AdminResponse> admins = adminService.getActiveAdmins(pageable);
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<AdminResponse>> getAdminsByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name").ascending());
        Page<AdminResponse> admins = adminService.getAdminsByRole(role, pageable);
        return ResponseEntity.ok(admins);
    }
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        long activeAdminCount = adminService.getActiveAdminCount();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeAdminCount", activeAdminCount);
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(stats);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminResponse> updateAdmin(
            @PathVariable String id,
            @Valid @RequestBody AdminRegistrationRequest request) {
        AdminResponse admin = adminService.updateAdmin(id, request);
        return ResponseEntity.ok(admin);
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deactivateAdmin(@PathVariable String id) {
        adminService.deactivateAdmin(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> activateAdmin(@PathVariable String id) {
        adminService.activateAdmin(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/login-update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateLastLogin(
            @RequestParam String email,
            HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        adminService.updateLastLogin(email, ipAddress);
        return ResponseEntity.ok().build();
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}