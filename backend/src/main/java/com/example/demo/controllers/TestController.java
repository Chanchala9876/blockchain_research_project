package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.services.DocumentTextExtractorService;
import com.example.demo.services.OllamaEmbeddingService;
import com.example.demo.repositories.ResearchPaperRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple test controller to isolate issues with thesis verification
 */
@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    
    @Autowired(required = false)
    private DocumentTextExtractorService documentTextExtractorService;
    
    @Autowired(required = false)
    private OllamaEmbeddingService ollamaEmbeddingService;
    
    @Autowired(required = false)
    private ResearchPaperRepository researchPaperRepository;
    
    /**
     * Basic health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            log.info("🏥 Health check requested");
            
            return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "timestamp", System.currentTimeMillis(),
                "message", "Test controller is working"
            ));
            
        } catch (Exception e) {
            log.error("❌ Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Test service dependencies
     */
    @GetMapping("/check-dependencies")
    public ResponseEntity<?> checkDependencies() {
        try {
            log.info("🔍 Checking service dependencies");
            
            Map<String, Object> status = new HashMap<>();
            
            // Check DocumentTextExtractorService
            if (documentTextExtractorService != null) {
                status.put("documentTextExtractorService", "available");
                log.info("✅ DocumentTextExtractorService is available");
            } else {
                status.put("documentTextExtractorService", "NOT AVAILABLE");
                log.warn("❌ DocumentTextExtractorService is NOT available");
            }
            
            // Check OllamaEmbeddingService
            if (ollamaEmbeddingService != null) {
                status.put("ollamaEmbeddingService", "available");
                try {
                    boolean ollamaHealthy = ollamaEmbeddingService.isOllamaAvailable();
                    status.put("ollamaConnection", ollamaHealthy ? "connected" : "disconnected");
                    log.info("✅ OllamaEmbeddingService is available, connection: {}", ollamaHealthy);
                } catch (Exception e) {
                    status.put("ollamaConnection", "error: " + e.getMessage());
                    log.warn("⚠️ OllamaEmbeddingService available but connection test failed: {}", e.getMessage());
                }
            } else {
                status.put("ollamaEmbeddingService", "NOT AVAILABLE");
                log.warn("❌ OllamaEmbeddingService is NOT available");
            }
            
            // Check ResearchPaperRepository
            if (researchPaperRepository != null) {
                status.put("researchPaperRepository", "available");
                try {
                    long count = researchPaperRepository.count();
                    status.put("researchPaperCount", count);
                    log.info("✅ ResearchPaperRepository is available, {} papers in database", count);
                } catch (Exception e) {
                    status.put("researchPaperRepository", "error: " + e.getMessage());
                    log.warn("⚠️ ResearchPaperRepository available but query failed: {}", e.getMessage());
                }
            } else {
                status.put("researchPaperRepository", "NOT AVAILABLE");
                log.warn("❌ ResearchPaperRepository is NOT available");
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("❌ Dependency check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Test file upload and basic processing
     */
    @PostMapping("/test-file-upload")
    public ResponseEntity<?> testFileUpload(@RequestParam("testFile") MultipartFile file) {
        try {
            log.info("📁 Testing file upload: {}", file != null ? file.getOriginalFilename() : "null");
            
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is required"));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("filename", file.getOriginalFilename());
            result.put("size", file.getSize());
            result.put("contentType", file.getContentType());
            
            // Test document text extraction if service is available
            if (documentTextExtractorService != null) {
                try {
                    log.info("🔍 Testing text extraction...");
                    String extractedText = documentTextExtractorService.extractTextFromDocument(file);
                    result.put("textExtracted", true);
                    result.put("textLength", extractedText.length());
                    result.put("textPreview", extractedText.length() > 100 ? 
                        extractedText.substring(0, 100) + "..." : extractedText);
                    log.info("✅ Text extraction successful: {} characters", extractedText.length());
                } catch (Exception e) {
                    result.put("textExtracted", false);
                    result.put("textExtractionError", e.getMessage());
                    log.error("❌ Text extraction failed: {}", e.getMessage(), e);
                }
            } else {
                result.put("textExtracted", false);
                result.put("textExtractionError", "DocumentTextExtractorService not available");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("❌ File upload test failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
}