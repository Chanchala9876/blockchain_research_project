package com.example.demo.controllers;

import com.example.demo.models.ResearchPaper;
import com.example.demo.services.ResearchPaperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/research-papers")
public class ResearchPaperController {
    
    private static final Logger log = LoggerFactory.getLogger(ResearchPaperController.class);
    
    @Autowired
    private ResearchPaperService researchPaperService;
    
    @Autowired
    private com.example.demo.services.ThesisVerificationService thesisVerificationService;
    
    @Autowired
    private com.example.demo.services.PendingThesisService pendingThesisService;
    
    /**
     * Upload a new research paper for multi-admin approval (Admin only)
     * - Runs AI verification to detect duplicates
     * - If unique, stores in pending_thesis (NOT blockchain yet)
     * - Requires approval from other admins before blockchain submission
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadResearchPaper(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("department") String department,
            @RequestParam("institution") String institution,
            @RequestParam("supervisor") String supervisor,
            @RequestParam(value = "coSupervisor", required = false) String coSupervisor,
            @RequestParam("abstractText") String abstractText,
            @RequestParam("keywords") String keywords,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "validationDocument", required = true) MultipartFile validationDocument,
            @RequestParam("uploadedBy") String uploadedBy) {
        
        try {
            log.info("📝 Received research paper upload request for title: {}", title);
            log.info("👤 Uploaded by admin: {}", uploadedBy);
            
            // Parse keywords
            List<String> keywordList = Arrays.asList(keywords.split(","));
            keywordList.replaceAll(String::trim);
            
            // ===== STEP 1: AI VERIFICATION TO DETECT DUPLICATES =====
            log.info("🤖 Running AI verification to check for duplicates...");
            com.example.demo.dto.ThesisVerificationRequest verifyReq = new com.example.demo.dto.ThesisVerificationRequest();
            verifyReq.setTitle(title);
            verifyReq.setAuthor(author);
            verifyReq.setDepartment(department);
            verifyReq.setInstitution(institution);
            verifyReq.setAbstractText(abstractText);
            verifyReq.setKeywords(keywordList);
            verifyReq.setSupervisor(supervisor);
            verifyReq.setCoSupervisor(coSupervisor);
            verifyReq.setSubmissionYear(java.time.LocalDate.now().getYear());

            try {
                com.example.demo.dto.ThesisVerificationResponse verification = thesisVerificationService.verifyThesis(verifyReq, file, "ADMIN");

                if (verification != null) {
                    String matchType = verification.getMatchType();
                    Double similarityScore = verification.getSimilarityScore();
                    Double plagiarismScore = verification.getPlagiarismScore();
                    
                    log.info("📊 AI Verification Results - Match Type: {}, Similarity: {}%, Plagiarism: {}%", 
                        matchType, similarityScore, plagiarismScore);
                    
                    // Enhanced duplicate detection
                    boolean isDuplicate = "EXACT_MATCH".equals(matchType)
                            || "IDENTICAL_CONTENT".equals(matchType)
                            || "EXACT_TITLE_MATCH".equals(matchType)
                            || (plagiarismScore != null && plagiarismScore >= 80.0)
                            || (similarityScore != null && similarityScore >= 85.0);

                    if (isDuplicate) {
                        log.warn("⚠️ DUPLICATE DETECTED! Similarity: {}%, Plagiarism: {}%", similarityScore, plagiarismScore);
                        Map<String, Object> dupResp = new HashMap<>();
                        dupResp.put("success", false);
                        dupResp.put("message", verification.getMessage() != null ? verification.getMessage() : 
                            String.format("⚠️ DUPLICATE THESIS DETECTED: This thesis appears to be very similar (%.1f%% similarity, %.1f%% plagiarism risk) to an existing paper in our blockchain database. Upload rejected.", 
                                similarityScore != null ? similarityScore : 0.0, 
                                plagiarismScore != null ? plagiarismScore : 0.0));
                        dupResp.put("matchType", verification.getMatchType());
                        dupResp.put("similarityScore", verification.getSimilarityScore());
                        dupResp.put("plagiarismScore", verification.getPlagiarismScore());
                        dupResp.put("aiAnalysis", verification.getAiAnalysis());
                        if (verification.getPaper() != null) {
                            Map<String, Object> existingPaper = new HashMap<>();
                            existingPaper.put("id", verification.getPaper().getId());
                            existingPaper.put("title", verification.getPaper().getTitle());
                            existingPaper.put("author", verification.getPaper().getAuthor());
                            existingPaper.put("department", verification.getPaper().getDepartment());
                            existingPaper.put("institution", verification.getPaper().getInstitution());
                            existingPaper.put("uploadedDate", verification.getPaper().getUploadedDate());
                            dupResp.put("existingPaper", existingPaper);
                        }
                        return ResponseEntity.status(409).body(dupResp);
                    }
                    
                    log.info("✅ AI Verification passed - Thesis is unique, proceeding to pending approval workflow");
                }
            } catch (Exception ex) {
                log.warn("⚠️ AI verification failed, continuing with upload: {}", ex.getMessage());
            }

            // ===== STEP 2: STORE IN PENDING_THESIS (NOT BLOCKCHAIN YET) =====
            log.info("📋 Storing thesis in pending_thesis table for multi-admin approval...");
            
            com.example.demo.models.PendingThesis pendingThesis = pendingThesisService.submitThesisForApproval(
                title, author, department, institution, supervisor, coSupervisor,
                abstractText, keywordList, uploadedBy, file, validationDocument);
            
            log.info("✅ Thesis submitted to pending approval workflow");
            log.info("📊 Approval Status: {} / {} admins approved", 
                pendingThesis.getCurrentApprovals(), pendingThesis.getTotalAdminsRequired());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format(
                "Thesis submitted successfully! Waiting for approval from %d other admin(s). Once all admins approve, it will be added to the blockchain.",
                pendingThesis.getTotalAdminsRequired()));
            response.put("pendingThesisId", pendingThesis.getId());
            response.put("title", pendingThesis.getTitle());
            response.put("author", pendingThesis.getAuthor());
            response.put("status", pendingThesis.getStatus());
            response.put("currentApprovals", pendingThesis.getCurrentApprovals());
            response.put("totalAdminsRequired", pendingThesis.getTotalAdminsRequired());
            response.put("approvalProgress", pendingThesis.getApprovalProgress());
            response.put("submittedAt", pendingThesis.getCreatedAt());
            response.put("storedOnBlockchain", false); // Not yet on blockchain
            response.put("needsApproval", true);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid upload request: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("❌ Error uploading research paper: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to upload research paper");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get all approved blockchain records (Admin only)
     */
    @GetMapping("/blockchain-records")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getBlockchainRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            log.info("📚 Fetching blockchain records - Page: {}, Size: {}", page, size);
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedDate").descending());
            Page<ResearchPaper> papers = researchPaperService.getResearchPapersByStatus("VERIFIED", pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("records", papers.getContent());
            response.put("currentPage", papers.getNumber());
            response.put("totalItems", papers.getTotalElements());
            response.put("totalPages", papers.getTotalPages());
            response.put("message", String.format("Retrieved %d blockchain records", papers.getContent().size()));
            
            log.info("✅ Retrieved {} blockchain records", papers.getContent().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error fetching blockchain records: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch blockchain records");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Upload a new research paper DIRECTLY to blockchain (for backwards compatibility)
     * This bypasses the approval workflow - use with caution!
     */
    @PostMapping("/upload-direct")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadResearchPaperDirect(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("department") String department,
            @RequestParam("institution") String institution,
            @RequestParam("supervisor") String supervisor,
            @RequestParam(value = "coSupervisor", required = false) String coSupervisor,
            @RequestParam("abstractText") String abstractText,
            @RequestParam("keywords") String keywords,
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadedBy") String uploadedBy) {
        
        try {
            log.info("⚠️ DIRECT upload (bypasses approval workflow) for title: {}", title);
            
            List<String> keywordList = Arrays.asList(keywords.split(","));
            keywordList.replaceAll(String::trim);
            
            ResearchPaper savedPaper = researchPaperService.uploadResearchPaper(
                title, author, department, institution, supervisor, coSupervisor,
                abstractText, keywordList, file, uploadedBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Research paper uploaded successfully");
            response.put("paperId", savedPaper.getId());
            response.put("title", savedPaper.getTitle());
            response.put("author", savedPaper.getAuthor());
            response.put("fileHash", savedPaper.getFileHash());
            response.put("status", savedPaper.getStatus());
            response.put("embeddingsGenerated", savedPaper.getDocumentEmbedding() != null);
            response.put("uploadedAt", savedPaper.getUploadedDate());
            response.put("blockchainTxId", savedPaper.getBlockchainTxId());
            response.put("blockchainHash", savedPaper.getBlockchainHash());
            response.put("storedOnBlockchain", savedPaper.getBlockchainTxId() != null);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error uploading research paper: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to upload research paper");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get all research papers with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllResearchPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<ResearchPaper> papers = researchPaperService.getAllResearchPapers(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("papers", papers.getContent());
            response.put("currentPage", papers.getNumber());
            response.put("totalItems", papers.getTotalElements());
            response.put("totalPages", papers.getTotalPages());
            response.put("hasNext", papers.hasNext());
            response.put("hasPrevious", papers.hasPrevious());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching research papers: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch research papers");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get research papers by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getResearchPapersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("uploadedDate").descending());
            Page<ResearchPaper> papers = researchPaperService.getResearchPapersByStatus(status, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("papers", papers.getContent());
            response.put("currentPage", papers.getNumber());
            response.put("totalItems", papers.getTotalElements());
            response.put("totalPages", papers.getTotalPages());
            response.put("status", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching papers by status: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch papers by status");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get research paper by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getResearchPaperById(@PathVariable String id) {
        try {
            ResearchPaper paper = researchPaperService.getResearchPaperById(id)
                    .orElseThrow(() -> new RuntimeException("Research paper not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paper", paper);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching research paper: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Research paper not found");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Search research papers
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchResearchPapers(@RequestParam String query) {
        try {
            List<ResearchPaper> papers = researchPaperService.searchResearchPapers(query);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("papers", papers);
            response.put("query", query);
            response.put("totalResults", papers.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error searching research papers: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search research papers");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Update research paper status (Admin only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePaperStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        try {
            ResearchPaper updatedPaper = researchPaperService.updateStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Paper status updated successfully");
            response.put("paper", updatedPaper);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error updating paper status: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update paper status");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Verify paper on blockchain
     */
    @GetMapping("/{id}/verify-blockchain")
    public ResponseEntity<Map<String, Object>> verifyPaperOnBlockchain(@PathVariable String id) {
        try {
            boolean existsOnBlockchain = researchPaperService.verifyPaperOnBlockchain(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paperExists", existsOnBlockchain);
            response.put("message", existsOnBlockchain ? 
                "Paper verified on blockchain" : "Paper not found on blockchain");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error verifying paper on blockchain: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to verify paper on blockchain");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Update paper viewability (Admin only)
     */
    @PutMapping("/{paperId}/viewability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePaperViewability(
            @PathVariable String paperId,
            @RequestParam boolean viewable) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Admin updating paper {} viewability to: {}", paperId, viewable);
            
            boolean success = researchPaperService.updatePaperViewability(paperId, viewable);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Paper viewability updated successfully");
                response.put("paperId", paperId);
                response.put("viewable", viewable);
                
                log.info("Successfully updated paper {} viewability to: {}", paperId, viewable);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Paper not found");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error updating paper viewability for paper: {}", paperId, e);
            response.put("success", false);
            response.put("message", "Failed to update paper viewability");
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}