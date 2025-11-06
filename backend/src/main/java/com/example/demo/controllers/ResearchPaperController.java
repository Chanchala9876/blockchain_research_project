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
    
    /**
     * Upload a new research paper (Admin only) - Supports PDF and DOCX formats
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
            @RequestParam("uploadedBy") String uploadedBy) {
        
        try {
            log.info("Received research paper upload request for title: {}", title);
            
            // Parse keywords
            List<String> keywordList = Arrays.asList(keywords.split(","));
            keywordList.replaceAll(String::trim);
            
            // Upload and process the paper
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
}