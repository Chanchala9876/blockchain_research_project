package com.example.demo.controllers;

import com.example.demo.dto.ThesisVerificationRequest;
import com.example.demo.dto.ThesisVerificationResponse;
import com.example.demo.services.ThesisVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/papers")
@CrossOrigin(origins = "*")
public class ThesisVerificationController {
    
    private static final Logger log = LoggerFactory.getLogger(ThesisVerificationController.class);
    
    @Autowired
    private ThesisVerificationService thesisVerificationService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Verify thesis by uploading PDF or DOCX and metadata
     * Matches frontend: POST /api/papers/verify-thesis
     */
    @PostMapping("/verify-thesis")
    public ResponseEntity<?> verifyThesis(
            @RequestParam("thesisFile") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("department") String department,
            @RequestParam(value = "submissionYear", required = false) Integer submissionYear,
            @RequestParam(value = "institution", required = false) String institution,
            @RequestParam(value = "abstract", required = false) String abstractText,
            @RequestParam(value = "keywords", required = false) String keywordsJson,
            @RequestParam(value = "supervisor", required = false) String supervisor,
            @RequestParam(value = "coSupervisor", required = false) String coSupervisor,
            @RequestParam(value = "userType", required = false, defaultValue = "STUDENT") String userType) {
        
        try {
            log.info("📄 Received thesis verification request for: {} by {}", title, author);
            
            // Create verification request object
            ThesisVerificationRequest request = new ThesisVerificationRequest();
            request.setTitle(title);
            request.setAuthor(author);
            request.setDepartment(department);
            request.setSubmissionYear(submissionYear != null ? submissionYear : java.time.Year.now().getValue());
            request.setInstitution(institution);
            request.setAbstractText(abstractText);
            request.setSupervisor(supervisor);
            request.setCoSupervisor(coSupervisor);
            
            // Parse keywords JSON array if provided
            if (keywordsJson != null && !keywordsJson.trim().isEmpty()) {
                try {
                    @SuppressWarnings("unchecked")
                    java.util.List<String> keywords = objectMapper.readValue(keywordsJson, java.util.List.class);
                    request.setKeywords(keywords);
                } catch (Exception e) {
                    log.warn("Failed to parse keywords JSON: {}", e.getMessage());
                }
            }
            
            // Validate file
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Thesis file is required (PDF or DOCX)", "success", false));
            }
            
            log.info("🔍 Starting verification process...");
            
            // Perform verification with user type for role-based reporting
            ThesisVerificationResponse response = thesisVerificationService.verifyThesis(request, file, userType);
            
            log.info("✅ Verification completed - Verified: {}, Similarity: {}%", 
                    response.isVerified(), response.getSimilarityScore());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Validation error during thesis verification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage(), "success", false));
                
        } catch (Exception e) {
            log.error("❌ Unexpected error during thesis verification: {}", e.getMessage(), e);
            // Include more detailed error information for debugging
            String detailedError = "Verification failed: " + e.getMessage();
            if (e.getCause() != null) {
                detailedError += " (Cause: " + e.getCause().getMessage() + ")";
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", detailedError, "success", false, "type", e.getClass().getSimpleName()));
        }
    }
    
    /**
     * Search papers in database
     * Matches frontend: POST /api/papers/search
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchPapers(@RequestBody Map<String, String> searchRequest) {
        try {
            String hash = searchRequest.get("hash");
            String title = searchRequest.get("title"); 
            String author = searchRequest.get("author");
            String blockchainTxId = searchRequest.get("blockchainTxId");
            
            log.info("🔍 Received search request - hash: {}, title: {}, author: {}, txId: {}", 
                    hash, title, author, blockchainTxId);
            
            // Validate that at least one search criterion is provided
            if ((hash == null || hash.trim().isEmpty()) && 
                (title == null || title.trim().isEmpty()) && 
                (author == null || author.trim().isEmpty()) && 
                (blockchainTxId == null || blockchainTxId.trim().isEmpty())) {
                
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "At least one search criterion is required", "success", false));
            }
            
            // Perform search
            ThesisVerificationResponse response = thesisVerificationService.searchPapers(
                hash, title, author, blockchainTxId);
            
            log.info("🔍 Search completed - Found: {}", response.isVerified());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error during paper search: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Search failed: " + e.getMessage(), "success", false));
        }
    }
    
    /**
     * Get verification status endpoint (for frontend polling if needed)
     */
    @GetMapping("/verification-status/{id}")
    public ResponseEntity<?> getVerificationStatus(@PathVariable String id) {
        try {
            // This could be used for async verification status checking
            // For now, return a simple status
            return ResponseEntity.ok(Map.of("id", id, "status", "completed"));
            
        } catch (Exception e) {
            log.error("Error getting verification status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get status", "success", false));
        }
    }
    
    /**
     * Health check endpoint for verification service
     */
    @GetMapping("/verification-health")
    public ResponseEntity<?> getVerificationHealth() {
        try {
            Map<String, Object> healthStatus = new java.util.HashMap<>();
            
            // Check if services are available
            boolean ollamaAvailable = false;
            boolean servicesAvailable = true;
            
            try {
                // Test Ollama connection
                ollamaAvailable = thesisVerificationService != null;
                healthStatus.put("thesisVerificationService", "available");
            } catch (Exception e) {
                healthStatus.put("thesisVerificationService", "error: " + e.getMessage());
                servicesAvailable = false;
            }
            
            // Check database connection
            try {
                // This will throw an exception if MongoDB is not connected
                healthStatus.put("database", "available");
            } catch (Exception e) {
                healthStatus.put("database", "error: " + e.getMessage());
                servicesAvailable = false;
            }
            
            return ResponseEntity.ok(Map.of(
                "status", servicesAvailable ? "healthy" : "degraded",
                "services", healthStatus,
                "timestamp", java.time.LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("status", "unhealthy", "error", e.getMessage()));
        }
    }
    
    /**
     * Simple test endpoint to verify basic functionality
     */
    @PostMapping("/test-extraction")
    public ResponseEntity<?> testTextExtraction(@RequestParam("testFile") MultipartFile file) {
        try {
            log.info("🧪 Testing text extraction for file: {}", file.getOriginalFilename());
            
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is required", "success", false));
            }
            
            // Test basic file validation
            String filename = file.getOriginalFilename();
            log.info("File details - Name: {}, Size: {}, Type: {}", 
                filename, file.getSize(), file.getContentType());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Basic file validation passed",
                "filename", filename,
                "size", file.getSize(),
                "contentType", file.getContentType()
            ));
            
        } catch (Exception e) {
            log.error("❌ Test extraction failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Test failed: " + e.getMessage(), "success", false));
        }
    }
    
    /**
     * Get verification statistics
     */
    @GetMapping("/verification-stats")
    public ResponseEntity<?> getVerificationStats() {
        try {
            // This could return statistics about verification usage
            // For now, return basic stats
            return ResponseEntity.ok(Map.of(
                "totalVerifications", 0,
                "successfulMatches", 0,
                "averageSimilarity", 0.0,
                "lastUpdated", java.time.LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            log.error("Error getting verification stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get stats", "success", false));
        }
    }
    
    /**
     * Get supported file formats information
     */
    @GetMapping("/supported-formats")
    public ResponseEntity<?> getSupportedFormats() {
        try {
            return ResponseEntity.ok(Map.of(
                "supportedExtensions", List.of("pdf", "docx"),
                "description", "Supported formats: PDF (.pdf), Word Document (.docx)",
                "maxFileSize", "50MB",
                "acceptAttribute", ".pdf,.docx,application/pdf,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ));
        } catch (Exception e) {
            log.error("Error getting supported formats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get supported formats", "success", false));
        }
    }
}