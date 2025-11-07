package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pending_thesis")
public class PendingThesis {
    
    @Id
    private String id;
    
    // Thesis Information
    private String title;
    private String author;
    private String department;
    private String institution;
    private String instituteId; // Reference to Institute model
    private String supervisor;
    private String coSupervisor;
    private LocalDateTime submissionDate;
    private String fileHash; // SHA-256 hash of thesis PDF
    private String fileName;
    private Long fileSize;
    private String filePath; // Storage path/URL for thesis PDF
    private String abstractText;
    private List<String> keywords;
    
    // University/Government Validation Document
    private String validationDocumentPath; // Path to signed validation document
    private String validationDocumentName; // Original filename of validation document
    private String validationDocumentHash; // Hash of validation document for integrity
    private Long validationDocumentSize; // Size of validation document
    
    // Approval Workflow
    private String uploadedBy; // Admin ID who uploaded this thesis
    private List<String> approvals; // List of admin IDs who have approved
    private String status; // PENDING_APPROVAL, APPROVED, REJECTED
    private String rejectionReason; // If rejected, reason provided
    private String rejectedBy; // Admin ID who rejected
    private LocalDateTime rejectedAt; // When it was rejected
    
    // Metadata
    private int totalAdminsRequired; // Total number of admins (excluding uploader) needed for approval
    private int currentApprovals; // Current number of approvals received
    private Boolean requiresUnanimous; // Whether all admins must approve (default: true)
    
    // AI Analysis (copied from verification)
    private List<Double> documentEmbedding; // Document embedding for similarity check
    private List<Double> titleEmbedding; // Title embedding
    private String embeddingModel; // Ollama model used
    private LocalDateTime embeddingGeneratedAt;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public PendingThesis() {
        this.approvals = new ArrayList<>();
        this.status = "PENDING_APPROVAL";
        this.requiresUnanimous = true;
        this.currentApprovals = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDepartment() { return department; }
    public String getInstitution() { return institution; }
    public String getInstituteId() { return instituteId; }
    public String getSupervisor() { return supervisor; }
    public String getCoSupervisor() { return coSupervisor; }
    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public String getFileHash() { return fileHash; }
    public String getFileName() { return fileName; }
    public Long getFileSize() { return fileSize; }
    public String getFilePath() { return filePath; }
    public String getAbstractText() { return abstractText; }
    public List<String> getKeywords() { return keywords; }
    public String getValidationDocumentPath() { return validationDocumentPath; }
    public String getValidationDocumentName() { return validationDocumentName; }
    public String getValidationDocumentHash() { return validationDocumentHash; }
    public Long getValidationDocumentSize() { return validationDocumentSize; }
    public String getUploadedBy() { return uploadedBy; }
    public List<String> getApprovals() { return approvals; }
    public String getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public String getRejectedBy() { return rejectedBy; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public int getTotalAdminsRequired() { return totalAdminsRequired; }
    public int getCurrentApprovals() { return currentApprovals; }
    public Boolean getRequiresUnanimous() { return requiresUnanimous; }
    public List<Double> getDocumentEmbedding() { return documentEmbedding; }
    public List<Double> getTitleEmbedding() { return titleEmbedding; }
    public String getEmbeddingModel() { return embeddingModel; }
    public LocalDateTime getEmbeddingGeneratedAt() { return embeddingGeneratedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setDepartment(String department) { this.department = department; }
    public void setInstitution(String institution) { this.institution = institution; }
    public void setInstituteId(String instituteId) { this.instituteId = instituteId; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    public void setCoSupervisor(String coSupervisor) { this.coSupervisor = coSupervisor; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    public void setValidationDocumentPath(String validationDocumentPath) { this.validationDocumentPath = validationDocumentPath; }
    public void setValidationDocumentName(String validationDocumentName) { this.validationDocumentName = validationDocumentName; }
    public void setValidationDocumentHash(String validationDocumentHash) { this.validationDocumentHash = validationDocumentHash; }
    public void setValidationDocumentSize(Long validationDocumentSize) { this.validationDocumentSize = validationDocumentSize; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
    public void setApprovals(List<String> approvals) { this.approvals = approvals; }
    public void setStatus(String status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setRejectedBy(String rejectedBy) { this.rejectedBy = rejectedBy; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public void setTotalAdminsRequired(int totalAdminsRequired) { this.totalAdminsRequired = totalAdminsRequired; }
    public void setCurrentApprovals(int currentApprovals) { this.currentApprovals = currentApprovals; }
    public void setRequiresUnanimous(Boolean requiresUnanimous) { this.requiresUnanimous = requiresUnanimous; }
    public void setDocumentEmbedding(List<Double> documentEmbedding) { this.documentEmbedding = documentEmbedding; }
    public void setTitleEmbedding(List<Double> titleEmbedding) { this.titleEmbedding = titleEmbedding; }
    public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
    public void setEmbeddingGeneratedAt(LocalDateTime embeddingGeneratedAt) { this.embeddingGeneratedAt = embeddingGeneratedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility Methods
    public boolean canApprove(String adminId) {
        // Admin cannot approve their own submission
        if (this.uploadedBy.equals(adminId)) {
            return false;
        }
        // Admin cannot approve twice
        return !this.approvals.contains(adminId);
    }
    
    public void addApproval(String adminId) {
        if (canApprove(adminId)) {
            this.approvals.add(adminId);
            this.currentApprovals = this.approvals.size();
        }
    }
    
    public boolean isFullyApproved() {
        return this.currentApprovals >= this.totalAdminsRequired;
    }
    
    public double getApprovalProgress() {
        if (totalAdminsRequired == 0) return 0.0;
        return (double) currentApprovals / totalAdminsRequired * 100.0;
    }
    
    // Builder pattern
    public static PendingThesisBuilder builder() {
        return new PendingThesisBuilder();
    }
    
    public static class PendingThesisBuilder {
        private PendingThesis thesis = new PendingThesis();
        
        public PendingThesisBuilder title(String title) { thesis.setTitle(title); return this; }
        public PendingThesisBuilder author(String author) { thesis.setAuthor(author); return this; }
        public PendingThesisBuilder department(String department) { thesis.setDepartment(department); return this; }
        public PendingThesisBuilder institution(String institution) { thesis.setInstitution(institution); return this; }
        public PendingThesisBuilder instituteId(String instituteId) { thesis.setInstituteId(instituteId); return this; }
        public PendingThesisBuilder supervisor(String supervisor) { thesis.setSupervisor(supervisor); return this; }
        public PendingThesisBuilder coSupervisor(String coSupervisor) { thesis.setCoSupervisor(coSupervisor); return this; }
        public PendingThesisBuilder submissionDate(LocalDateTime submissionDate) { thesis.setSubmissionDate(submissionDate); return this; }
        public PendingThesisBuilder fileHash(String fileHash) { thesis.setFileHash(fileHash); return this; }
        public PendingThesisBuilder fileName(String fileName) { thesis.setFileName(fileName); return this; }
        public PendingThesisBuilder fileSize(Long fileSize) { thesis.setFileSize(fileSize); return this; }
        public PendingThesisBuilder filePath(String filePath) { thesis.setFilePath(filePath); return this; }
        public PendingThesisBuilder abstractText(String abstractText) { thesis.setAbstractText(abstractText); return this; }
        public PendingThesisBuilder keywords(List<String> keywords) { thesis.setKeywords(keywords); return this; }
        public PendingThesisBuilder validationDocumentPath(String path) { thesis.setValidationDocumentPath(path); return this; }
        public PendingThesisBuilder validationDocumentName(String name) { thesis.setValidationDocumentName(name); return this; }
        public PendingThesisBuilder validationDocumentHash(String hash) { thesis.setValidationDocumentHash(hash); return this; }
        public PendingThesisBuilder validationDocumentSize(Long size) { thesis.setValidationDocumentSize(size); return this; }
        public PendingThesisBuilder uploadedBy(String uploadedBy) { thesis.setUploadedBy(uploadedBy); return this; }
        public PendingThesisBuilder totalAdminsRequired(int total) { thesis.setTotalAdminsRequired(total); return this; }
        
        public PendingThesis build() {
            return thesis;
        }
    }
    
    @Override
    public String toString() {
        return "PendingThesis{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", uploadedBy='" + uploadedBy + '\'' +
                ", status='" + status + '\'' +
                ", currentApprovals=" + currentApprovals +
                ", totalAdminsRequired=" + totalAdminsRequired +
                ", createdAt=" + createdAt +
                '}';
    }
}