package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public class AdminBlockchainRecordsResponse {
    
    @JsonProperty("records")
    private List<BlockchainRecord> records;
    
    @JsonProperty("totalRecords")
    private int totalRecords;
    
    @JsonProperty("instituteInfo")
    private InstituteInfo instituteInfo;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("message")
    private String message;
    
    // Constructors
    public AdminBlockchainRecordsResponse() {}
    
    public AdminBlockchainRecordsResponse(List<BlockchainRecord> records, InstituteInfo instituteInfo) {
        this.records = records;
        this.totalRecords = records != null ? records.size() : 0;
        this.instituteInfo = instituteInfo;
        this.success = true;
    }
    
    // Getters and Setters
    public List<BlockchainRecord> getRecords() {
        return records;
    }
    
    public void setRecords(List<BlockchainRecord> records) {
        this.records = records;
        this.totalRecords = records != null ? records.size() : 0;
    }
    
    public int getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public InstituteInfo getInstituteInfo() {
        return instituteInfo;
    }
    
    public void setInstituteInfo(InstituteInfo instituteInfo) {
        this.instituteInfo = instituteInfo;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    // Inner class for individual blockchain records
    public static class BlockchainRecord {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("title")
        private String title;
        
        @JsonProperty("author")
        private String author;
        
        @JsonProperty("department")
        private String department;
        
        @JsonProperty("institution")
        private String institution;
        
        @JsonProperty("submissionDate")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime submissionDate;
        
        @JsonProperty("verificationDate")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private LocalDateTime verificationDate;
        
        @JsonProperty("blockchainHash")
        private String blockchainHash;
        
        @JsonProperty("transactionId")
        private String transactionId;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("similarity")
        private Double similarity;
        
        @JsonProperty("fileSize")
        private Double fileSize;
        
        @JsonProperty("filePath")
        private String filePath;
        
        @JsonProperty("ipfsHash")
        private String ipfsHash;
        
        @JsonProperty("verifiedBy")
        private String verifiedBy;
        
        @JsonProperty("paperCategory")
        private String paperCategory;
        
        @JsonProperty("keywords")
        private List<String> keywords;
        
        // Constructors
        public BlockchainRecord() {}
        
        public BlockchainRecord(String id, String title, String author, String department, 
                               String institution, String blockchainHash, String transactionId) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.department = department;
            this.institution = institution;
            this.blockchainHash = blockchainHash;
            this.transactionId = transactionId;
            this.status = "VERIFIED";
            this.submissionDate = LocalDateTime.now();
            this.verificationDate = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        
        public LocalDateTime getSubmissionDate() { return submissionDate; }
        public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
        
        public LocalDateTime getVerificationDate() { return verificationDate; }
        public void setVerificationDate(LocalDateTime verificationDate) { this.verificationDate = verificationDate; }
        
        public String getBlockchainHash() { return blockchainHash; }
        public void setBlockchainHash(String blockchainHash) { this.blockchainHash = blockchainHash; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Double getSimilarity() { return similarity; }
        public void setSimilarity(Double similarity) { this.similarity = similarity; }
        
        public Double getFileSize() { return fileSize; }
        public void setFileSize(Double fileSize) { this.fileSize = fileSize; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getIpfsHash() { return ipfsHash; }
        public void setIpfsHash(String ipfsHash) { this.ipfsHash = ipfsHash; }
        
        public String getVerifiedBy() { return verifiedBy; }
        public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
        
        public String getPaperCategory() { return paperCategory; }
        public void setPaperCategory(String paperCategory) { this.paperCategory = paperCategory; }
        
        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    }
    
    // Inner class for institute information
    public static class InstituteInfo {
        @JsonProperty("instituteName")
        private String instituteName;
        
        @JsonProperty("adminEmail")
        private String adminEmail;
        
        @JsonProperty("totalPapers")
        private int totalPapers;
        
        @JsonProperty("verifiedPapers")
        private int verifiedPapers;
        
        @JsonProperty("departments")
        private List<String> departments;
        
        // Constructors
        public InstituteInfo() {}
        
        public InstituteInfo(String instituteName, String adminEmail) {
            this.instituteName = instituteName;
            this.adminEmail = adminEmail;
        }
        
        // Getters and Setters
        public String getInstituteName() { return instituteName; }
        public void setInstituteName(String instituteName) { this.instituteName = instituteName; }
        
        public String getAdminEmail() { return adminEmail; }
        public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
        
        public int getTotalPapers() { return totalPapers; }
        public void setTotalPapers(int totalPapers) { this.totalPapers = totalPapers; }
        
        public int getVerifiedPapers() { return verifiedPapers; }
        public void setVerifiedPapers(int verifiedPapers) { this.verifiedPapers = verifiedPapers; }
        
        public List<String> getDepartments() { return departments; }
        public void setDepartments(List<String> departments) { this.departments = departments; }
    }
}