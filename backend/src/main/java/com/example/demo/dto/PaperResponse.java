package com.example.demo.dto;

import java.time.LocalDateTime;

public class PaperResponse {
    private String id;
    private String title;
    private String authorName;
    private String authorId;
    private String abstract_;
    private String pdfUrl;
    private String status;
    private String hash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public PaperResponse() {}
    
    public PaperResponse(String id, String title, String authorName, String authorId, String abstract_,
                        String pdfUrl, String status, String hash, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.authorId = authorId;
        this.abstract_ = abstract_;
        this.pdfUrl = pdfUrl;
        this.status = status;
        this.hash = hash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static PaperResponseBuilder builder() {
        return new PaperResponseBuilder();
    }
    
    public static class PaperResponseBuilder {
        private String id;
        private String title;
        private String authorName;
        private String authorId;
        private String abstract_;
        private String pdfUrl;
        private String status;
        private String hash;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public PaperResponseBuilder id(String id) { this.id = id; return this; }
        public PaperResponseBuilder title(String title) { this.title = title; return this; }
        public PaperResponseBuilder authorName(String authorName) { this.authorName = authorName; return this; }
        public PaperResponseBuilder authorId(String authorId) { this.authorId = authorId; return this; }
        public PaperResponseBuilder abstract_(String abstract_) { this.abstract_ = abstract_; return this; }
        public PaperResponseBuilder pdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; return this; }
        public PaperResponseBuilder status(String status) { this.status = status; return this; }
        public PaperResponseBuilder hash(String hash) { this.hash = hash; return this; }
        public PaperResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaperResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public PaperResponse build() {
            return new PaperResponse(id, title, authorName, authorId, abstract_, pdfUrl, status, hash, createdAt, updatedAt);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAbstract_() {
        return abstract_;
    }
    
    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }
    
    public String getPdfUrl() {
        return pdfUrl;
    }
    
    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getHash() {
        return hash;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}