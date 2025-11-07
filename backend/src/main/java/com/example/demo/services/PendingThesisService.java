package com.example.demo.services;

import com.example.demo.models.Admin;
import com.example.demo.models.PendingThesis;
import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.PendingThesisRepository;
import com.example.demo.repositories.ResearchPaperRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PendingThesisService {
    
    private static final Logger log = LoggerFactory.getLogger(PendingThesisService.class);
    
    @Autowired
    private PendingThesisRepository pendingThesisRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @Autowired
    private FabricGatewayService fabricGatewayService; // Your existing Fabric service
    
    @Value("${file.upload.directory:uploads/thesis/}")
    private String uploadDirectory;
    
    @Value("${file.validation.directory:uploads/validation/}")
    private String validationDirectory;
    
    /**
     * Submit a new thesis for approval workflow
     */
    public PendingThesis submitThesisForApproval(
            String title, String author, String department, String institution,
            String supervisor, String coSupervisor, String abstractText,
            List<String> keywords, String adminId,
            MultipartFile thesisFile, MultipartFile validationDocument) throws IOException {
        
        log.info("📄 Admin {} submitting thesis '{}' for approval workflow", adminId, title);
        log.info("🔍 Searching for admin with identifier: '{}'", adminId);
        
        // Validate inputs
        if (thesisFile.isEmpty() || validationDocument.isEmpty()) {
            throw new IllegalArgumentException("Both thesis file and validation document are required");
        }
        
        // Check if admin exists - adminId can be either MongoDB ID or email
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        log.info("🔍 Search by ID result: {}", adminOpt.isPresent() ? "FOUND" : "NOT FOUND");
        
        if (adminOpt.isEmpty()) {
            // Try finding by email (which is used as username)
            log.info("🔍 Trying to find by email: '{}'", adminId);
            adminOpt = adminRepository.findByEmail(adminId);
            log.info("🔍 Search by email result: {}", adminOpt.isPresent() ? "FOUND" : "NOT FOUND");
            
            if (adminOpt.isEmpty()) {
                log.error("❌ Admin not found with ID or email: '{}'", adminId);
                log.error("❌ Please check if this email exists in admins collection");
                throw new IllegalArgumentException("Admin not found: " + adminId);
            }
        }
        Admin admin = adminOpt.get();
        log.info("✅ Found admin: {} (ID: {}, Email: {})", admin.getName(), admin.getId(), admin.getEmail());
        
        // Save thesis file
        String thesisFileHash = generateFileHash(thesisFile.getBytes());
        
        // Check for duplicate submissions
        Optional<PendingThesis> existingPending = pendingThesisRepository.findByFileHash(thesisFileHash);
        if (existingPending.isPresent()) {
            throw new IllegalArgumentException("This thesis file has already been submitted for approval");
        }
        
        // Check if already in main repository
        Optional<ResearchPaper> existingPaper = researchPaperRepository.findByFileHash(thesisFileHash);
        if (existingPaper.isPresent()) {
            throw new IllegalArgumentException("This thesis has already been verified and is on the blockchain");
        }
        
        // Save files to disk
        String thesisFileName = saveFile(thesisFile, uploadDirectory, "thesis_" + UUID.randomUUID().toString());
        String validationFileName = saveFile(validationDocument, validationDirectory, "validation_" + UUID.randomUUID().toString());
        
        // Calculate total admins required (all active admins except uploader)
        List<Admin> allAdmins = adminRepository.findByIsActive(true);
        int totalAdminsRequired = allAdmins.size() - 1; // Exclude uploader
        
        if (totalAdminsRequired <= 0) {
            throw new IllegalStateException("Insufficient admins for approval workflow. Need at least 2 active admins.");
        }
        
        // Create pending thesis record
        PendingThesis pendingThesis = PendingThesis.builder()
                .title(title)
                .author(author)
                .department(department)
                .institution(institution)
                .instituteId(admin.getInstituteId())
                .supervisor(supervisor)
                .coSupervisor(coSupervisor)
                .submissionDate(LocalDateTime.now())
                .fileHash(thesisFileHash)
                .fileName(thesisFile.getOriginalFilename())
                .fileSize(thesisFile.getSize())
                .filePath(thesisFileName)
                .abstractText(abstractText)
                .keywords(keywords)
                .validationDocumentPath(validationFileName)
                .validationDocumentName(validationDocument.getOriginalFilename())
                .validationDocumentHash(generateFileHash(validationDocument.getBytes()))
                .validationDocumentSize(validationDocument.getSize())
                .uploadedBy(adminId)
                .totalAdminsRequired(totalAdminsRequired)
                .build();
        
        PendingThesis saved = pendingThesisRepository.save(pendingThesis);
        
        log.info("✅ Thesis '{}' submitted for approval. Needs {} approvals from other admins", 
                title, totalAdminsRequired);
        
        return saved;
    }
    
    /**
     * Approve a pending thesis
     */
    public PendingThesis approveThesis(String thesisId, String adminId) {
        log.info("👍 Admin {} attempting to approve thesis {}", adminId, thesisId);
        
        Optional<PendingThesis> thesisOpt = pendingThesisRepository.findById(thesisId);
        if (thesisOpt.isEmpty()) {
            throw new IllegalArgumentException("Pending thesis not found");
        }
        
        PendingThesis thesis = thesisOpt.get();
        
        // Check if admin can approve
        if (!thesis.canApprove(adminId)) {
            if (thesis.getUploadedBy().equals(adminId)) {
                throw new IllegalArgumentException("You cannot approve your own thesis submission");
            } else {
                throw new IllegalArgumentException("You have already approved this thesis");
            }
        }
        
        // Add approval
        thesis.addApproval(adminId);
        
        // Check if fully approved
        if (thesis.isFullyApproved()) {
            log.info("🎉 Thesis '{}' is fully approved! Moving to blockchain...", thesis.getTitle());
            moveToBlockchain(thesis);
        }
        
        return pendingThesisRepository.save(thesis);
    }
    
    /**
     * Reject a pending thesis
     */
    public PendingThesis rejectThesis(String thesisId, String adminId, String reason) {
        log.info("❌ Admin {} rejecting thesis {} with reason: {}", adminId, thesisId, reason);
        
        Optional<PendingThesis> thesisOpt = pendingThesisRepository.findById(thesisId);
        if (thesisOpt.isEmpty()) {
            throw new IllegalArgumentException("Pending thesis not found");
        }
        
        PendingThesis thesis = thesisOpt.get();
        
        // Admin cannot reject their own submission
        if (thesis.getUploadedBy().equals(adminId)) {
            throw new IllegalArgumentException("You cannot reject your own thesis submission");
        }
        
        thesis.setStatus("REJECTED");
        thesis.setRejectionReason(reason);
        thesis.setRejectedBy(adminId);
        thesis.setRejectedAt(LocalDateTime.now());
        
        return pendingThesisRepository.save(thesis);
    }
    
    /**
     * Get all pending thesis for an admin dashboard
     */
    public List<PendingThesis> getPendingThesisForAdmin(String adminId) {
        // Return all pending thesis - admin can see their own uploads and others awaiting approval
        return pendingThesisRepository.findAllPendingThesis("PENDING_APPROVAL");
    }
    
    /**
     * Get thesis awaiting approval by specific admin
     */
    public List<PendingThesis> getThesisAwaitingApproval(String adminId) {
        return pendingThesisRepository.findThesisAwaitingApprovalByAdmin(adminId);
    }
    
    /**
     * Get ALL pending thesis (for debugging)
     */
    public List<PendingThesis> getAllPendingThesis() {
        return pendingThesisRepository.findByStatus("PENDING_APPROVAL");
    }
    
    /**
     * Get thesis uploaded by specific admin
     */
    public List<PendingThesis> getThesisUploadedByAdmin(String adminId) {
        return pendingThesisRepository.findByUploadedBy(adminId);
    }
    
    /**
     * Move approved thesis to blockchain
     */
    private void moveToBlockchain(PendingThesis pendingThesis) {
        try {
            log.info("🔗 Moving thesis '{}' to blockchain...", pendingThesis.getTitle());
            
            // Create ResearchPaper from PendingThesis
            ResearchPaper researchPaper = ResearchPaper.builder()
                    .title(pendingThesis.getTitle())
                    .author(pendingThesis.getAuthor())
                    .department(pendingThesis.getDepartment())
                    .institution(pendingThesis.getInstitution())
                    .instituteId(pendingThesis.getInstituteId())
                    .supervisor(pendingThesis.getSupervisor())
                    .coSupervisor(pendingThesis.getCoSupervisor())
                    .submissionDate(pendingThesis.getSubmissionDate())
                    .uploadedDate(LocalDateTime.now())
                    .fileHash(pendingThesis.getFileHash())
                    .fileName(pendingThesis.getFileName())
                    .fileSize(pendingThesis.getFileSize())
                    .filePath(pendingThesis.getFilePath())
                    .abstractText(pendingThesis.getAbstractText())
                    .keywords(pendingThesis.getKeywords())
                    .status("VERIFIED")
                    .uploadedBy(pendingThesis.getUploadedBy())
                    .verifiedBy(new ArrayList<>(pendingThesis.getApprovals())) // Copy approvals list
                    .validationDocumentPath(pendingThesis.getValidationDocumentPath())
                    .verificationDate(LocalDateTime.now())
                    .viewable(true)
                    .documentEmbedding(pendingThesis.getDocumentEmbedding())
                    .titleEmbedding(pendingThesis.getTitleEmbedding())
                    .embeddingModel(pendingThesis.getEmbeddingModel())
                    .embeddingGeneratedAt(pendingThesis.getEmbeddingGeneratedAt())
                    .build();
            
            // Submit to Hyperledger Fabric
            try {
                String blockchainTxId = fabricGatewayService.createPaperRecord(
                    researchPaper.getAuthor(), // student name as studentId
                    researchPaper.getFileHash(),
                    researchPaper.getAuthor(),
                    researchPaper.getUploadedBy(),
                    researchPaper.getSubmissionDate().toString()
                );
                researchPaper.setBlockchainTxId(blockchainTxId);
                researchPaper.setBlockchainHash(generateBlockchainHash(researchPaper));
                
                log.info("✅ Thesis submitted to blockchain with transaction ID: {}", blockchainTxId);
            } catch (Exception e) {
                log.error("❌ Failed to submit to blockchain: {}", e.getMessage());
                // Still save to database but mark status appropriately
                researchPaper.setStatus("BLOCKCHAIN_PENDING");
                researchPaper.setBlockchainTxId("PENDING");
            }
            
            // Save to main research papers collection
            researchPaperRepository.save(researchPaper);
            
            // Update pending thesis status
            pendingThesis.setStatus("APPROVED");
            pendingThesisRepository.save(pendingThesis);
            
            log.info("🎉 Thesis '{}' successfully moved to blockchain and main repository", pendingThesis.getTitle());
            
        } catch (Exception e) {
            log.error("❌ Error moving thesis to blockchain: {}", e.getMessage(), e);
            // Keep the thesis in pending state for manual review
            throw new RuntimeException("Failed to move thesis to blockchain: " + e.getMessage());
        }
    }
    
    /**
     * Get approval statistics for dashboard
     */
    public ApprovalStatistics getApprovalStatistics(String adminId) {
        long pendingCount = pendingThesisRepository.countByStatus("PENDING_APPROVAL");
        long uploadedByAdmin = pendingThesisRepository.countByUploadedBy(adminId);
        long approvedByAdmin = pendingThesisRepository.countThesisApprovedByAdmin(adminId);
        long awaitingApprovalCount = pendingThesisRepository.findThesisAwaitingApprovalByAdmin(adminId).size();
        
        return new ApprovalStatistics(pendingCount, uploadedByAdmin, approvedByAdmin, awaitingApprovalCount);
    }
    
    // Helper methods
    private String saveFile(MultipartFile file, String directory, String prefix) throws IOException {
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = prefix + "_" + System.currentTimeMillis() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
    
    private String generateFileHash(byte[] fileContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileContent);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate file hash", e);
        }
    }
    
    private String generateBlockchainHash(ResearchPaper paper) {
        // Generate a deterministic hash for blockchain verification
        String data = paper.getTitle() + paper.getAuthor() + paper.getFileHash() + 
                     paper.getSubmissionDate().toString();
        return generateFileHash(data.getBytes());
    }
    
    /**
     * Get a specific pending thesis by ID
     */
    public PendingThesis getThesisById(String thesisId) {
        Optional<PendingThesis> thesisOpt = pendingThesisRepository.findById(thesisId);
        return thesisOpt.orElse(null);
    }
    
    // Statistics DTO
    public static class ApprovalStatistics {
        private final long totalPending;
        private final long uploadedByMe;
        private final long approvedByMe;
        private final long awaitingMyApproval;
        
        public ApprovalStatistics(long totalPending, long uploadedByMe, long approvedByMe, long awaitingMyApproval) {
            this.totalPending = totalPending;
            this.uploadedByMe = uploadedByMe;
            this.approvedByMe = approvedByMe;
            this.awaitingMyApproval = awaitingMyApproval;
        }
        
        public long getTotalPending() { return totalPending; }
        public long getUploadedByMe() { return uploadedByMe; }
        public long getApprovedByMe() { return approvedByMe; }
        public long getAwaitingMyApproval() { return awaitingMyApproval; }
    }
}