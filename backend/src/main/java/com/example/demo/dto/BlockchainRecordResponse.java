package com.example.demo.dto;

import java.time.LocalDateTime;

public class BlockchainRecordResponse {
    private String id;
    private String paperHash;
    private String previousHash;
    private LocalDateTime timestamp;
    private String authorId;
    private String authorName;
    private String paperTitle;
    private String institute;
    
    public BlockchainRecordResponse() {}
    
    public BlockchainRecordResponse(String id, String paperHash, String previousHash, LocalDateTime timestamp,
                                   String authorId, String authorName, String paperTitle, String institute) {
        this.id = id;
        this.paperHash = paperHash;
        this.previousHash = previousHash;
        this.timestamp = timestamp;
        this.authorId = authorId;
        this.authorName = authorName;
        this.paperTitle = paperTitle;
        this.institute = institute;
    }
    
    public static BlockchainRecordResponseBuilder builder() {
        return new BlockchainRecordResponseBuilder();
    }
    
    public static class BlockchainRecordResponseBuilder {
        private String id;
        private String paperHash;
        private String previousHash;
        private LocalDateTime timestamp;
        private String authorId;
        private String authorName;
        private String paperTitle;
        private String institute;
        
        public BlockchainRecordResponseBuilder id(String id) { this.id = id; return this; }
        public BlockchainRecordResponseBuilder paperHash(String paperHash) { this.paperHash = paperHash; return this; }
        public BlockchainRecordResponseBuilder previousHash(String previousHash) { this.previousHash = previousHash; return this; }
        public BlockchainRecordResponseBuilder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public BlockchainRecordResponseBuilder authorId(String authorId) { this.authorId = authorId; return this; }
        public BlockchainRecordResponseBuilder authorName(String authorName) { this.authorName = authorName; return this; }
        public BlockchainRecordResponseBuilder paperTitle(String paperTitle) { this.paperTitle = paperTitle; return this; }
        public BlockchainRecordResponseBuilder institute(String institute) { this.institute = institute; return this; }
        
        public BlockchainRecordResponse build() {
            return new BlockchainRecordResponse(id, paperHash, previousHash, timestamp, authorId, authorName, paperTitle, institute);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPaperHash() {
        return paperHash;
    }
    
    public void setPaperHash(String paperHash) {
        this.paperHash = paperHash;
    }
    
    public String getPreviousHash() {
        return previousHash;
    }
    
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getPaperTitle() {
        return paperTitle;
    }
    
    public void setPaperTitle(String paperTitle) {
        this.paperTitle = paperTitle;
    }
    
    public String getInstitute() {
        return institute;
    }
    
    public void setInstitute(String institute) {
        this.institute = institute;
    }
}