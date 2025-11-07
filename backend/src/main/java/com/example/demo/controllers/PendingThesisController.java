package com.example.demo.controllers;

import com.example.demo.dto.ApiResponse;
import com.example.demo.models.PendingThesis;
import com.example.demo.services.PendingThesisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing pending thesis approvals in the decentralized approval workflow
 */
@RestController
@RequestMapping("/api/pending-thesis")
@CrossOrigin(origins = "http://localhost:3000")
public class PendingThesisController {

    private static final Logger log = LoggerFactory.getLogger(PendingThesisController.class);
    
    @Autowired
    private PendingThesisService pendingThesisService;

    /**
     * Submit a new thesis for approval by multiple admins
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PendingThesis>> submitThesisForApproval(
            @RequestParam("thesisFile") MultipartFile thesisFile,
            @RequestParam("validationDocument") MultipartFile validationDocument,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("department") String department,
            @RequestParam("institution") String institution,
            @RequestParam("supervisor") String supervisor,
            @RequestParam(value = "coSupervisor", required = false) String coSupervisor,
            @RequestParam(value = "abstractText", required = false) String abstractText,
            @RequestParam(value = "keywords", required = false) String keywords,
            Authentication authentication) {
        
        try {
            log.info("📝 Receiving thesis submission for approval workflow");
            log.info("Thesis details - Title: '{}', Author: '{}', Department: '{}'", title, author, department);
            
            // Get authenticated admin info
            String adminUsername = authentication.getName();
            log.info("📋 Submission by admin: {}", adminUsername);
            
            // Validate file uploads
            if (thesisFile.isEmpty()) {
                log.warn("❌ Thesis file is empty");
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Thesis file is required", null));
            }
            
            if (validationDocument.isEmpty()) {
                log.warn("❌ Validation document is empty");
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Validation document is required", null));
            }
            
            // Parse keywords
            List<String> keywordsList = keywords != null ? List.of(keywords.split(",\\s*")) : List.of();
            
            // Submit thesis for approval
            PendingThesis pendingThesis = pendingThesisService.submitThesisForApproval(
                title, author, department, institution, supervisor, coSupervisor, 
                abstractText, keywordsList, adminUsername, thesisFile, validationDocument);
            
            log.info("✅ Thesis submitted successfully with ID: {}", pendingThesis.getId());
            log.info("📊 Status: {} | Total admins required: {}", 
                pendingThesis.getStatus(), pendingThesis.getTotalAdminsRequired());
            
            return ResponseEntity.ok(new ApiResponse<>(true, 
                "Thesis submitted for approval. Waiting for approval from " + 
                pendingThesis.getTotalAdminsRequired() + " admin(s).", pendingThesis));
                
        } catch (Exception e) {
            log.error("❌ Failed to submit thesis for approval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to submit thesis: " + e.getMessage(), null));
        }
    }
    
    /**
     * Get all pending thesis approvals for the authenticated admin
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PendingThesis>>> getPendingApprovals(Authentication authentication) {
        
        try {
            String adminUsername = authentication.getName();
            log.info("📋 Fetching pending approvals for admin: {}", adminUsername);
            
            // Get all pending thesis for debugging
            List<PendingThesis> allPending = pendingThesisService.getAllPendingThesis();
            log.info("📊 Total pending theses in database: {}", allPending.size());
            allPending.forEach(thesis -> {
                log.info("   - Thesis: '{}' by {} | Status: {} | Uploaded by: {} | Approvals: {}/{}", 
                    thesis.getTitle(), thesis.getAuthor(), thesis.getStatus(), 
                    thesis.getUploadedBy(), thesis.getCurrentApprovals(), thesis.getTotalAdminsRequired());
                log.info("   - Validation Doc: {} | Name: {} | Size: {}", 
                    thesis.getValidationDocumentPath() != null ? "EXISTS" : "NULL",
                    thesis.getValidationDocumentName(),
                    thesis.getValidationDocumentSize());
            });
            
            List<PendingThesis> pendingApprovals = pendingThesisService.getThesisAwaitingApproval(adminUsername);
            
            log.info("📊 Found {} pending approvals for admin {} (excluding own uploads and already approved)", 
                pendingApprovals.size(), adminUsername);
            
            return ResponseEntity.ok(new ApiResponse<>(true, 
                "Retrieved pending approvals", pendingApprovals));
                
        } catch (Exception e) {
            log.error("❌ Failed to fetch pending approvals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to fetch pending approvals: " + e.getMessage(), null));
        }
    }
    
    /**
     * Approve a pending thesis
     */
    @PostMapping("/{thesisId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approveThesis(
            @PathVariable String thesisId,
            @RequestParam(value = "comment", required = false) String comment,
            Authentication authentication) {
        
        try {
            String adminUsername = authentication.getName();
            log.info("✅ Admin {} approving thesis: {}", adminUsername, thesisId);
            
            pendingThesisService.approveThesis(thesisId, adminUsername);
            
            String message = "Thesis approved successfully";
            log.info("📈 Approval result: {}", message);
            
            return ResponseEntity.ok(new ApiResponse<>(true, message, message));
                
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid approval request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("❌ Failed to approve thesis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to approve thesis: " + e.getMessage(), null));
        }
    }
    
    /**
     * Reject a pending thesis
     */
    @PostMapping("/{thesisId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> rejectThesis(
            @PathVariable String thesisId,
            @RequestParam("reason") String reason,
            Authentication authentication) {
        
        try {
            String adminUsername = authentication.getName();
            log.info("❌ Admin {} rejecting thesis: {} with reason: {}", adminUsername, thesisId, reason);
            
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Rejection reason is required", null));
            }
            
            pendingThesisService.rejectThesis(thesisId, adminUsername, reason);
            
            String message = "Thesis rejected successfully";
            log.info("📉 Rejection result: {}", message);
            
            return ResponseEntity.ok(new ApiResponse<>(true, message, message));
                
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Invalid rejection request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("❌ Failed to reject thesis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to reject thesis: " + e.getMessage(), null));
        }
    }
    
    /**
     * Get detailed thesis information by ID
     */
    @GetMapping("/{thesisId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PendingThesis>> getThesisDetails(
            @PathVariable String thesisId,
            Authentication authentication) {
        
        try {
            PendingThesis thesis = pendingThesisService.getThesisById(thesisId);
            
            if (thesis == null) {
                return ResponseEntity.notFound().build();
            }
            
            log.info("📄 Retrieved thesis details: {} by {}", thesis.getTitle(), thesis.getAuthor());
            
            return ResponseEntity.ok(new ApiResponse<>(true, 
                "Retrieved thesis details", thesis));
                
        } catch (Exception e) {
            log.error("❌ Failed to get thesis details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Failed to get thesis details: " + e.getMessage(), null));
        }
    }
    
    /**
     * Download/View validation document for a pending thesis
     */
    @GetMapping("/{thesisId}/validation-document")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<org.springframework.core.io.Resource> getValidationDocument(
            @PathVariable String thesisId,
            @RequestParam(value = "download", defaultValue = "false") boolean download,
            Authentication authentication) {
        
        try {
            log.info("📄 Admin {} requesting validation document for thesis {}", 
                authentication.getName(), thesisId);
            
            PendingThesis thesis = pendingThesisService.getThesisById(thesisId);
            
            if (thesis == null) {
                log.warn("⚠️ Thesis not found: {}", thesisId);
                return ResponseEntity.notFound().build();
            }
            
            log.info("📋 Thesis found: '{}' by {}", thesis.getTitle(), thesis.getAuthor());
            log.info("📁 Validation document path: {}", thesis.getValidationDocumentPath());
            log.info("📝 Validation document name: {}", thesis.getValidationDocumentName());
            
            if (thesis.getValidationDocumentPath() == null || thesis.getValidationDocumentPath().isEmpty()) {
                log.warn("⚠️ No validation document path for thesis: {}", thesisId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }
            
            java.nio.file.Path filePath = java.nio.file.Paths.get(thesis.getValidationDocumentPath());
            log.info("🔍 Looking for file at: {}", filePath.toAbsolutePath());
            log.info("📂 File exists: {}", java.nio.file.Files.exists(filePath));
            log.info("📖 File readable: {}", java.nio.file.Files.isReadable(filePath));
            
            if (!java.nio.file.Files.exists(filePath)) {
                log.error("❌ File does not exist: {}", filePath.toAbsolutePath());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }
            
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                log.error("❌ Validation document not readable: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            String contentType = "application/pdf"; // Validation documents are typically PDFs
            String headerValue = download ? "attachment" : "inline";
            headerValue += "; filename=\"" + (thesis.getValidationDocumentName() != null 
                ? thesis.getValidationDocumentName() : "validation_document.pdf") + "\"";
            
            log.info("✅ Serving validation document: {} ({})", 
                thesis.getValidationDocumentName(), download ? "download" : "view");
            
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("❌ Failed to retrieve validation document: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}