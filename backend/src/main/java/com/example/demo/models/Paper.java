package com.example.demo.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "papers")
public class Paper {
    
    @Id
    private String id;
    private String title;
    
    @DBRef
    private User author;
    private String authorId; // Reference to User ID
    private String abstract_;
    private String pdfUrl;
    private String hash; // Paper hash for blockchain
    private String status; // pending, verified, rejected
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public Paper() {}
    
    public Paper(String id, String title, User author, String authorId, String abstract_, 
                String pdfUrl, String hash, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.authorId = authorId;
        this.abstract_ = abstract_;
        this.pdfUrl = pdfUrl;
        this.hash = hash;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static PaperBuilder builder() {
        return new PaperBuilder();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public User getAuthor() { return author; }
    public String getAuthorId() { return authorId; }
    public String getAbstract_() { return abstract_; }
    public String getPdfUrl() { return pdfUrl; }
    public String getHash() { return hash; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(User author) { this.author = author; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAbstract_(String abstract_) { this.abstract_ = abstract_; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    public void setHash(String hash) { this.hash = hash; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Builder class
    public static class PaperBuilder {
        private String id;
        private String title;
        private User author;
        private String authorId;
        private String abstract_;
        private String pdfUrl;
        private String hash;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public PaperBuilder id(String id) { this.id = id; return this; }
        public PaperBuilder title(String title) { this.title = title; return this; }
        public PaperBuilder author(User author) { this.author = author; return this; }
        public PaperBuilder authorId(String authorId) { this.authorId = authorId; return this; }
        public PaperBuilder abstract_(String abstract_) { this.abstract_ = abstract_; return this; }
        public PaperBuilder pdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; return this; }
        public PaperBuilder hash(String hash) { this.hash = hash; return this; }
        public PaperBuilder status(String status) { this.status = status; return this; }
        public PaperBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaperBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Paper build() {
            return new Paper(id, title, author, authorId, abstract_, pdfUrl, hash, status, createdAt, updatedAt);
        }
    }
}