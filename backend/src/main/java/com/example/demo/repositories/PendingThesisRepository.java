package com.example.demo.repositories;

import com.example.demo.models.PendingThesis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PendingThesisRepository extends MongoRepository<PendingThesis, String> {
    
    // Find all pending thesis by status
    List<PendingThesis> findByStatus(String status);
    
    // Find pending thesis uploaded by specific admin
    List<PendingThesis> findByUploadedBy(String adminId);
    
    // Find pending thesis that a specific admin can approve (excluding own uploads)
    @Query("{ 'status': ?0, 'uploadedBy': { $ne: ?1 }, 'approvals': { $ne: ?1 } }")
    List<PendingThesis> findPendingForAdmin(String status, String adminId);
    
    // Find all pending thesis visible to an admin (including their own uploads)
    @Query("{ 'status': ?0 }")
    List<PendingThesis> findAllPendingThesis(String status);
    
    // Find thesis by file hash (to prevent duplicates)
    Optional<PendingThesis> findByFileHash(String fileHash);
    
    // Find thesis that have been approved by a specific admin
    @Query("{ 'approvals': ?0 }")
    List<PendingThesis> findApprovedByAdmin(String adminId);
    
    // Count pending thesis by uploader
    long countByUploadedBy(String adminId);
    
    // Count pending thesis by status
    long countByStatus(String status);
    
    // Find thesis needing approval (not uploaded by admin, not yet approved by admin)
    @Query("{ 'status': 'PENDING_APPROVAL', 'uploadedBy': { $ne: ?0 }, 'approvals': { $ne: ?0 } }")
    List<PendingThesis> findThesisAwaitingApprovalByAdmin(String adminId);
    
    // Find thesis by title (partial match, case insensitive)
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<PendingThesis> findByTitleContainingIgnoreCase(String title);
    
    // Find thesis by author (partial match, case insensitive)
    @Query("{ 'author': { $regex: ?0, $options: 'i' } }")
    List<PendingThesis> findByAuthorContainingIgnoreCase(String author);
    
    // Find thesis by institution
    List<PendingThesis> findByInstitution(String institution);
    
    // Find thesis by department
    List<PendingThesis> findByDepartment(String department);
    
    // Find fully approved thesis ready for blockchain submission
    @Query("{ 'status': 'PENDING_APPROVAL', 'currentApprovals': { $gte: ?0 } }")
    List<PendingThesis> findFullyApprovedThesis(int requiredApprovals);
    
    // Statistics queries
    @Query(value = "{ 'uploadedBy': ?0 }", count = true)
    long countThesisUploadedByAdmin(String adminId);
    
    @Query(value = "{ 'approvals': ?0 }", count = true)
    long countThesisApprovedByAdmin(String adminId);
    
    // Custom aggregation to get approval statistics
    @Query("{ $group: { _id: '$status', count: { $sum: 1 } } }")
    List<Object> getStatusStatistics();
}