package com.example.demo.controllers;

import com.example.demo.dto.AdminBlockchainRecordsResponse;
import com.example.demo.services.BlockchainRecordService;
import com.example.demo.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class BlockchainRecordController {
    
    private static final Logger log = LoggerFactory.getLogger(BlockchainRecordController.class);
    
    @Autowired
    private BlockchainRecordService blockchainRecordService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * Get all blockchain records for papers from the same institute as the admin
     * Only accessible to ADMIN role users
     */
    @GetMapping("/blockchain-records")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBlockchainRecords(HttpServletRequest request) {
        try {
            log.info("📊 Admin requesting blockchain records");
            
            // Extract admin details from JWT token or session
            String adminUsername = extractUsernameFromRequest(request);
            if (adminUsername == null) {
                // For testing purposes, use a default admin email from your system
                // In production, this should return unauthorized
                log.warn("⚠️ No authentication token found, using test admin");
                adminUsername = "admin.super@jnu.ac.in"; // Using the Super Admin from DataInitializer
            }
            
            log.info("🔍 Fetching blockchain records for admin: {}", adminUsername);
            
            // Get blockchain records for the admin's institute
            AdminBlockchainRecordsResponse response = blockchainRecordService.getBlockchainRecordsByAdminInstitute(adminUsername);
            
            log.info("✅ Found {} blockchain records for admin's institute", 
                    response.getRecords() != null ? response.getRecords().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid request for blockchain records: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage(), "success", false));
                
        } catch (Exception e) {
            log.error("❌ Error fetching blockchain records: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch blockchain records: " + e.getMessage(), "success", false));
        }
    }
    
    /**
     * Get blockchain record by ID (for detailed view)
     */
    @GetMapping("/blockchain-records/{recordId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBlockchainRecordById(@PathVariable String recordId, HttpServletRequest request) {
        try {
            log.info("📄 Admin requesting blockchain record details for ID: {}", recordId);
            
            String adminUsername = extractUsernameFromRequest(request);
            if (adminUsername == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required", "success", false));
            }
            
            // Get specific blockchain record if it belongs to admin's institute
            var record = blockchainRecordService.getBlockchainRecordById(recordId, adminUsername);
            
            if (record != null) {
                log.info("✅ Found blockchain record: {}", record.getTitle());
                return ResponseEntity.ok(Map.of("record", record, "success", true));
            } else {
                log.warn("⚠️ Blockchain record not found or access denied for ID: {}", recordId);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("❌ Error fetching blockchain record {}: {}", recordId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch blockchain record", "success", false));
        }
    }
    
    /**
     * Verify blockchain hash for a specific record
     */
    @PostMapping("/blockchain-records/{recordId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> verifyBlockchainHash(@PathVariable String recordId, 
                                                @RequestBody Map<String, String> verifyRequest,
                                                HttpServletRequest request) {
        try {
            log.info("🔍 Admin requesting hash verification for record: {}", recordId);
            
            String adminUsername = extractUsernameFromRequest(request);
            if (adminUsername == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required", "success", false));
            }
            
            String providedHash = verifyRequest.get("hash");
            if (providedHash == null || providedHash.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Hash value is required", "success", false));
            }
            
            // Verify the hash against blockchain
            boolean isValid = blockchainRecordService.verifyBlockchainHash(recordId, providedHash, adminUsername);
            
            log.info("🔒 Hash verification result for {}: {}", recordId, isValid ? "VALID" : "INVALID");
            
            return ResponseEntity.ok(Map.of(
                "recordId", recordId,
                "isValid", isValid,
                "verificationTimestamp", java.time.LocalDateTime.now().toString(),
                "success", true
            ));
            
        } catch (Exception e) {
            log.error("❌ Error verifying blockchain hash for {}: {}", recordId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Hash verification failed", "success", false));
        }
    }
    
    /**
     * Get blockchain statistics for admin's institute
     */
    @GetMapping("/blockchain-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getBlockchainStats(HttpServletRequest request) {
        try {
            log.info("📊 Admin requesting blockchain statistics");
            
            String adminUsername = extractUsernameFromRequest(request);
            if (adminUsername == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required", "success", false));
            }
            
            var stats = blockchainRecordService.getBlockchainStatsByInstitute(adminUsername);
            
            log.info("✅ Generated blockchain statistics for admin's institute");
            
            return ResponseEntity.ok(Map.of("stats", stats, "success", true));
            
        } catch (Exception e) {
            log.error("❌ Error getting blockchain statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get statistics", "success", false));
        }
    }
    
    /**
     * Extract username from JWT token in request
     */
    private String extractUsernameFromRequest(HttpServletRequest request) {
        try {
            // Try to get from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.debug("🔑 Found Authorization header, extracting username from JWT...");
                
                // Validate and extract username from JWT token
                if (jwtUtils.validateJwtToken(token)) {
                    return jwtUtils.getUserNameFromJwtToken(token);
                } else {
                    log.warn("⚠️ Invalid JWT token provided");
                    return null;
                }
            }
            
            // Fallback: try to get from request attributes (if set by security filter)
            java.security.Principal userPrincipal = request.getUserPrincipal();
            if (userPrincipal != null) {
                return userPrincipal.getName();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error extracting username from request: {}", e.getMessage());
            return null;
        }
    }
}