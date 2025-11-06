package com.example.demo.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "blockchainRecords")
public class BlockchainRecord {
    
    @Id
    private String id;
    private String paperHash;
    private String previousHash;
    private LocalDateTime timestamp;
    
    @DBRef
    private User author;
    private String authorId; // Reference to User ID
    
    private PaperData paperData;
    
    public BlockchainRecord() {}
    
    public BlockchainRecord(String id, String paperHash, String previousHash, LocalDateTime timestamp, 
                           User author, String authorId, PaperData paperData) {
        this.id = id;
        this.paperHash = paperHash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.author = author;
        this.authorId = authorId;
        this.paperData = paperData;
    }
    
    public static BlockchainRecordBuilder builder() {
        return new BlockchainRecordBuilder();
    }
    
    // Getters
    public String getId() { return id; }
    public String getPaperHash() { return paperHash; }
    public String getPreviousHash() { return previousHash; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public User getAuthor() { return author; }
    public String getAuthorId() { return authorId; }
    public PaperData getPaperData() { return paperData; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setPaperHash(String paperHash) { this.paperHash = paperHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setAuthor(User author) { this.author = author; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setPaperData(PaperData paperData) { this.paperData = paperData; }
    
    // Builder class
    public static class BlockchainRecordBuilder {
        private String id;
        private String paperHash;
        private String previousHash;
        private LocalDateTime timestamp;
        private User author;
        private String authorId;
        private PaperData paperData;
        
        public BlockchainRecordBuilder id(String id) { this.id = id; return this; }
        public BlockchainRecordBuilder paperHash(String paperHash) { this.paperHash = paperHash; return this; }
        public BlockchainRecordBuilder previousHash(String previousHash) { this.previousHash = previousHash; return this; }
        public BlockchainRecordBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public BlockchainRecordBuilder author(User author) { this.author = author; return this; }
        public BlockchainRecordBuilder authorId(String authorId) { this.authorId = authorId; return this; }
        public BlockchainRecordBuilder paperData(PaperData paperData) { this.paperData = paperData; return this; }
        
        public BlockchainRecord build() {
            return new BlockchainRecord(id, paperHash, previousHash, timestamp, author, authorId, paperData);
        }
    }
    
    public static class PaperData {
        private String title;
        private String authorName;
        private String institute;
        private LocalDateTime timestamp;
        
        public PaperData() {}
        
        public PaperData(String title, String authorName, String institute, LocalDateTime timestamp) {
            this.title = title;
            this.authorName = authorName;
            this.institute = institute;
            this.timestamp = timestamp;
        }
        
        public static PaperDataBuilder builder() {
            return new PaperDataBuilder();
        }
        
        // Getters
        public String getTitle() { return title; }
        public String getAuthorName() { return authorName; }
        public String getInstitute() { return institute; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        // Setters
        public void setTitle(String title) { this.title = title; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        public void setInstitute(String institute) { this.institute = institute; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        // Builder class
        public static class PaperDataBuilder {
            private String title;
            private String authorName;
            private String institute;
            private LocalDateTime timestamp;
            
            public PaperDataBuilder title(String title) { this.title = title; return this; }
            public PaperDataBuilder authorName(String authorName) { this.authorName = authorName; return this; }
            public PaperDataBuilder institute(String institute) { this.institute = institute; return this; }
            public PaperDataBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
            
            public PaperData build() {
                return new PaperData(title, authorName, institute, timestamp);
            }
        }
    }
}