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
    
    /**
     * Find viewable papers (public papers)
     */
    Page<ResearchPaper> findByViewableTrue(Pageable pageable);
    List<ResearchPaper> findByViewableTrue();
    
    /**
     * Find viewable papers by department
     */
    Page<ResearchPaper> findByViewableTrueAndDepartmentContainingIgnoreCase(String department, Pageable pageable);
    List<ResearchPaper> findByViewableTrueAndDepartmentContainingIgnoreCase(String department);
    
    /**
     * Find viewable papers by search in title, author, abstract
     */
    List<ResearchPaper> findByViewableTrueAndTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrAbstractTextContainingIgnoreCase(
        String title, String author, String abstractText);
    
    /**
     * Search viewable papers by title, author, or keywords
     */
    @Query("{ 'viewable': true, $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'author': { $regex: ?0, $options: 'i' } }, " +
           "{ 'department': { $regex: ?0, $options: 'i' } }, " +
           "{ 'keywords': { $regex: ?0, $options: 'i' } }, " +
           "{ 'abstractText': { $regex: ?0, $options: 'i' } } ] }")
    Page<ResearchPaper> findViewablePapersBySearchTerm(String searchTerm, Pageable pageable);
    
    /**
     * Search viewable papers by department and search term
     */
    @Query("{ 'viewable': true, 'department': { $regex: ?0, $options: 'i' }, $or: [ " +
           "{ 'title': { $regex: ?1, $options: 'i' } }, " +
           "{ 'author': { $regex: ?1, $options: 'i' } }, " +
           "{ 'keywords': { $regex: ?1, $options: 'i' } }, " +
           "{ 'abstractText': { $regex: ?1, $options: 'i' } } ] }")
    Page<ResearchPaper> findViewablePapersByDepartmentAndSearchTerm(String department, String searchTerm, Pageable pageable);
    
    /**
     * Find viewable papers by institution
     */
    Page<ResearchPaper> findByViewableTrueAndInstitutionContainingIgnoreCase(String institution, Pageable pageable);
    
    /**
     * Count viewable papers
     */
    long countByViewableTrue();
}