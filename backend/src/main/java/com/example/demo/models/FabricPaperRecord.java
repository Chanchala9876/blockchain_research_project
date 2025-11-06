package com.example.demo.models;

public class FabricPaperRecord {
    private String studentId;
    private String paperHash;
    private String timestamp;
    private String author;
    private String authorId;
    private String paperDate;
    
    public FabricPaperRecord() {}
    
    public FabricPaperRecord(String studentId, String paperHash, String timestamp, 
                            String author, String authorId, String paperDate) {
        this.studentId = studentId;
        this.paperHash = paperHash;
        this.timestamp = timestamp;
        this.author = author;
        this.authorId = authorId;
        this.paperDate = paperDate;
    }
    
    // Getters
    public String getStudentId() { return studentId; }
    public String getPaperHash() { return paperHash; }
    public String getTimestamp() { return timestamp; }
    public String getAuthor() { return author; }
    public String getAuthorId() { return authorId; }
    public String getPaperDate() { return paperDate; }
    
    // Setters
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setPaperHash(String paperHash) { this.paperHash = paperHash; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setAuthor(String author) { this.author = author; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setPaperDate(String paperDate) { this.paperDate = paperDate; }
    
    @Override
    public String toString() {
        return "FabricPaperRecord{" +
                "studentId='" + studentId + '\'' +
                ", paperHash='" + paperHash + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", author='" + author + '\'' +
                ", authorId='" + authorId + '\'' +
                ", paperDate='" + paperDate + '\'' +
                '}';
    }
}