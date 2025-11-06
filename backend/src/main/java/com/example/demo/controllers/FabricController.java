package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.models.FabricPaperRecord;
import com.example.demo.services.FabricGatewayService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fabric")
public class FabricController {
    
    private static final Logger log = LoggerFactory.getLogger(FabricController.class);
    
    private final FabricGatewayService fabricGatewayService;
    
    public FabricController(FabricGatewayService fabricGatewayService) {
        this.fabricGatewayService = fabricGatewayService;
    }
    
    @PostMapping("/papers/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createPaperRecord(
            @RequestParam("studentId") String studentId,
            @RequestParam("author") String author,
            @RequestParam("authorId") String authorId,
            @RequestParam("paperDate") String paperDate,
            @RequestParam("paperFile") MultipartFile paperFile) {
        
        try {
            // Generate paper hash from file content
            String paperHash = generatePaperHash(paperFile.getBytes(), author, studentId);
            
            // Create record on Hyperledger Fabric
            String transactionId = fabricGatewayService.createPaperRecord(
                    studentId, paperHash, author, authorId, paperDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paper record created successfully on Hyperledger Fabric");
            response.put("transactionId", transactionId);
            response.put("paperHash", paperHash);
            response.put("studentId", studentId);
            response.put("author", author);
            response.put("authorId", authorId);
            response.put("paperDate", paperDate);
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create paper record on Hyperledger Fabric", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create paper record: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/papers/{paperHash}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FabricPaperRecord> getPaperRecord(@PathVariable String paperHash) {
        try {
            FabricPaperRecord record = fabricGatewayService.getPaperRecord(paperHash);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            log.error("Failed to get paper record from Hyperledger Fabric", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/papers/verify/{paperHash}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> verifyPaperRecord(@PathVariable String paperHash) {
        try {
            boolean exists = fabricGatewayService.verifyPaperRecord(paperHash);
            
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("paperHash", paperHash);
            response.put("verified", exists);
            response.put("timestamp", LocalDateTime.now().toString());
            
            if (exists) {
                FabricPaperRecord record = fabricGatewayService.getPaperRecord(paperHash);
                response.put("record", record);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to verify paper record on Hyperledger Fabric", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("exists", false);
            errorResponse.put("verified", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/papers/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> getAllPaperRecords() {
        try {
            String allRecords = fabricGatewayService.getAllPaperRecords();
            return ResponseEntity.ok(allRecords);
        } catch (Exception e) {
            log.error("Failed to get all paper records from Hyperledger Fabric", e);
            return ResponseEntity.internalServerError().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
    
    private String generatePaperHash(byte[] fileContent, String author, String studentId) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            String combinedData = author + studentId + LocalDateTime.now().toString();
            byte[] combinedBytes = combinedData.getBytes();
            
            // Combine paper content with metadata for hash
            byte[] allData = new byte[combinedBytes.length + fileContent.length];
            System.arraycopy(combinedBytes, 0, allData, 0, combinedBytes.length);
            System.arraycopy(fileContent, 0, allData, combinedBytes.length, fileContent.length);
            
            byte[] digest = md.digest(allData);
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating paper hash: {}", e.getMessage());
            throw new RuntimeException("Failed to generate paper hash", e);
        }
    }
}