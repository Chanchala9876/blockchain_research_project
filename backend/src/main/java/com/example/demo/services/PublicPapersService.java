package com.example.demo.services;

import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.ResearchPaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing public access to viewable research papers
 */
@Service
public class PublicPapersService {
    
    private static final Logger log = LoggerFactory.getLogger(PublicPapersService.class);
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    /**
     * Get all publicly viewable papers with pagination
     */
    public Page<ResearchPaper> getAllViewablePapers(Pageable pageable) {
        log.info("Fetching all viewable papers with pagination");
        return researchPaperRepository.findByViewableTrue(pageable);
    }
    
    /**
     * Search viewable papers by department
     */
    public Page<ResearchPaper> searchByDepartment(String department, Pageable pageable) {
        log.info("Searching viewable papers by department: {}", department);
        return researchPaperRepository.findByViewableTrueAndDepartmentContainingIgnoreCase(department, pageable);
    }
    
    /**
     * Search viewable papers by department and general search term
     */
    public Page<ResearchPaper> searchByDepartmentAndQuery(String department, String query, Pageable pageable) {
        log.info("Searching viewable papers by department: {} and query: {}", department, query);
        return researchPaperRepository.findViewablePapersByDepartmentAndSearchTerm(department, query, pageable);
    }
    
    /**
     * Search viewable papers by general search term (title, author, keywords, abstract)
     */
    public Page<ResearchPaper> searchPapers(String searchTerm, Pageable pageable) {
        log.info("Searching viewable papers with term: {}", searchTerm);
        return researchPaperRepository.findViewablePapersBySearchTerm(searchTerm, pageable);
    }
    
    /**
     * Search viewable papers by institution
     */
    public Page<ResearchPaper> searchByInstitution(String institution, Pageable pageable) {
        log.info("Searching viewable papers by institution: {}", institution);
        return researchPaperRepository.findByViewableTrueAndInstitutionContainingIgnoreCase(institution, pageable);
    }
    
    /**
     * Get a viewable paper by ID (returns null if not viewable)
     */
    public ResearchPaper getViewablePaperById(String paperId) {
        log.info("Fetching viewable paper by ID: {}", paperId);
        
        Optional<ResearchPaper> paperOpt = researchPaperRepository.findById(paperId);
        
        if (paperOpt.isPresent()) {
            ResearchPaper paper = paperOpt.get();
            if (Boolean.TRUE.equals(paper.getViewable())) {
                log.info("Found viewable paper: {}", paper.getTitle());
                return paper;
            } else {
                log.warn("Paper {} exists but is not viewable", paperId);
                return null;
            }
        }
        
        log.warn("Paper with ID {} not found", paperId);
        return null;
    }
    
    /**
     * Get statistics about public papers
     */
    public Map<String, Object> getPublicPaperStatistics() {
        log.info("Fetching public paper statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Count total viewable papers
        long totalViewable = researchPaperRepository.countByViewableTrue();
        stats.put("totalViewablePapers", totalViewable);
        
        // Count total papers for comparison
        long totalPapers = researchPaperRepository.count();
        stats.put("totalPapers", totalPapers);
        
        // Calculate percentage of viewable papers
        double viewablePercentage = totalPapers > 0 ? (double) totalViewable / totalPapers * 100 : 0;
        stats.put("viewablePercentage", Math.round(viewablePercentage * 100.0) / 100.0);
        
        log.info("Public paper statistics - Viewable: {}, Total: {}, Percentage: {}%", 
                totalViewable, totalPapers, viewablePercentage);
        
        return stats;
    }
    
    /**
     * Update paper viewability (admin function - could be moved to admin service)
     */
    public boolean updatePaperViewability(String paperId, boolean viewable) {
        log.info("Updating paper {} viewability to: {}", paperId, viewable);
        
        Optional<ResearchPaper> paperOpt = researchPaperRepository.findById(paperId);
        
        if (paperOpt.isPresent()) {
            ResearchPaper paper = paperOpt.get();
            paper.setViewable(viewable);
            researchPaperRepository.save(paper);
            
            log.info("Successfully updated paper {} viewability to: {}", paperId, viewable);
            return true;
        }
        
        log.warn("Paper with ID {} not found for viewability update", paperId);
        return false;
    }
}