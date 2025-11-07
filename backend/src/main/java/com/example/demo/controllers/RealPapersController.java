package com.example.demo.controllers;

import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.ResearchPaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class RealPapersController {
    
    private static final Logger logger = LoggerFactory.getLogger(RealPapersController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @GetMapping("/working")
    public ResponseEntity<Map<String, Object>> working() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "API is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/papers")
    public ResponseEntity<Map<String, Object>> getPapers() {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Fetching papers from database...");
            
            // Get all papers from database
            List<ResearchPaper> allPapers = researchPaperRepository.findAll();
            logger.info("Found {} total papers in database", allPapers.size());
            
            // Filter viewable papers and convert to simple format
            List<Map<String, Object>> simplePapers = new ArrayList<>();
            int viewableCount = 0;
            
            for (ResearchPaper paper : allPapers) {
                if (paper.getViewable() != null && paper.getViewable()) {
                    viewableCount++;
                    
                    // Convert to simple format to avoid serialization issues
                    Map<String, Object> simplePaper = new HashMap<>();
                    simplePaper.put("id", paper.getId());
                    simplePaper.put("title", paper.getTitle() != null ? paper.getTitle() : "Untitled");
                    simplePaper.put("author", paper.getAuthor() != null ? paper.getAuthor() : "Unknown Author");
                    simplePaper.put("department", paper.getDepartment() != null ? paper.getDepartment() : "N/A");
                    simplePaper.put("institution", paper.getInstitution() != null ? paper.getInstitution() : "N/A");
                    simplePaper.put("supervisor", paper.getSupervisor() != null ? paper.getSupervisor() : "N/A");
                    simplePaper.put("status", paper.getStatus() != null ? paper.getStatus() : "PENDING");
                    simplePaper.put("viewable", true);
                    
                    // Handle abstract safely
                    String abstractText = paper.getAbstractText();
                    if (abstractText != null && abstractText.length() > 300) {
                        abstractText = abstractText.substring(0, 300) + "...";
                    }
                    simplePaper.put("abstractText", abstractText != null ? abstractText : "No abstract available");
                    
                    // Handle keywords safely
                    List<String> keywords = paper.getKeywords();
                    simplePaper.put("keywords", keywords != null ? keywords : new ArrayList<>());
                    
                    // Handle dates safely
                    if (paper.getSubmissionDate() != null) {
                        simplePaper.put("submissionDate", paper.getSubmissionDate().format(DATE_FORMATTER));
                    }
                    
                    simplePapers.add(simplePaper);
                }
            }
            
            logger.info("Converted {} viewable papers to simple format", viewableCount);
            
            // Build response
            response.put("papers", simplePapers);
            response.put("total", allPapers.size());
            response.put("viewable", viewableCount);
            response.put("status", "success");
            response.put("message", "Real papers fetched from database");
            
            logger.info("Successfully returning {} viewable papers out of {} total", viewableCount, allPapers.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching papers from database: ", e);
            response.put("status", "error");
            response.put("message", "Failed to fetch papers: " + e.getMessage());
            response.put("papers", new ArrayList<>());
            response.put("total", 0);
            response.put("viewable", 0);
            return ResponseEntity.status(500).body(response);
        }
    }
}