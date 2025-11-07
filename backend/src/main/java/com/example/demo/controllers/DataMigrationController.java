package com.example.demo.controllers;

import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.ResearchPaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/migration")
public class DataMigrationController {
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @PostMapping("/make-papers-viewable")
    public ResponseEntity<Map<String, Object>> makePapersViewable() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all papers
            var allPapers = researchPaperRepository.findAll();
            int updatedCount = 0;
            
            // Update all papers to be viewable
            for (ResearchPaper paper : allPapers) {
                paper.setViewable(true);
                researchPaperRepository.save(paper);
                updatedCount++;
            }
            
            // Get statistics
            long totalPapers = researchPaperRepository.count();
            long viewablePapers = researchPaperRepository.countByViewableTrue();
            
            response.put("success", true);
            response.put("message", "Successfully updated papers to be viewable");
            response.put("updatedCount", updatedCount);
            response.put("totalPapers", totalPapers);
            response.put("viewablePapers", viewablePapers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error updating papers: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/papers-stats")
    public ResponseEntity<Map<String, Object>> getPapersStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long totalPapers = researchPaperRepository.count();
            long viewablePapers = researchPaperRepository.countByViewableTrue();
            
            response.put("success", true);
            response.put("totalPapers", totalPapers);
            response.put("viewablePapers", viewablePapers);
            response.put("nonViewablePapers", totalPapers - viewablePapers);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting stats: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}