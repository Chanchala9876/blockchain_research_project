package com.example.demo.repositories;

import com.example.demo.models.ResearchPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResearchPaperRepository extends MongoRepository<ResearchPaper, String> {
    
    /**
     * Find research paper by file hash (to prevent duplicates)
     */
    Optional<ResearchPaper> findByFileHash(String fileHash);
    
    /**
     * Find research papers by status
     */
    Page<ResearchPaper> findByStatus(String status, Pageable pageable);
    
    /**
     * Find research papers by author
     */
    List<ResearchPaper> findByAuthorContainingIgnoreCase(String author);
    
    /**
     * Find research papers by title
     */
    List<ResearchPaper> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Search by title or author
     */
    List<ResearchPaper> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    
    /**
     * Find research papers by department
     */
    List<ResearchPaper> findByDepartment(String department);
    
    /**
     * Find research papers by supervisor
     */
    List<ResearchPaper> findBySupervisor(String supervisor);
    
    /**
     * Find research papers uploaded by specific admin
     */
    List<ResearchPaper> findByUploadedBy(String uploadedBy);
    
    /**
     * Count papers by status
     */
    long countByStatus(String status);
    
    /**
     * Find papers with embeddings
     */
    @Query("{ 'documentEmbedding': { $exists: true, $ne: null } }")
    List<ResearchPaper> findPapersWithEmbeddings();
    
    /**
     * Find papers without embeddings
     */
    @Query("{ $or: [ { 'documentEmbedding': { $exists: false } }, { 'documentEmbedding': null } ] }")
    List<ResearchPaper> findPapersWithoutEmbeddings();
}