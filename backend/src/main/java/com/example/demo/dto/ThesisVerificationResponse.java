package com.example.demo.dto;

import com.example.demo.models.ResearchPaper;
import java.time.LocalDateTime;
import java.util.List;

public class ThesisVerificationResponse {
    
    private boolean verified;
    private ResearchPaper paper;
    private Double similarityScore;
    private String matchType; // EXACT_MATCH, PARTIAL_MATCH, NO_MATCH
    private Double plagiarismScore;
    private AIAnalysis aiAnalysis;
    private String message;
    private LocalDateTime verificationTimestamp;
    
    // Constructors
    public ThesisVerificationResponse() {
        this.verificationTimestamp = LocalDateTime.now();
    }
    
    public ThesisVerificationResponse(boolean verified, String message) {
        this();
        this.verified = verified;
        this.message = message;
    }
    
    public ThesisVerificationResponse(boolean verified, ResearchPaper paper, Double similarityScore, 
                                    String matchType, Double plagiarismScore, AIAnalysis aiAnalysis) {
        this();
        this.verified = verified;
        this.paper = paper;
        this.similarityScore = similarityScore;
        this.matchType = matchType;
        this.plagiarismScore = plagiarismScore;
        this.aiAnalysis = aiAnalysis;
    }
    
    // Getters and Setters
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    
    public ResearchPaper getPaper() { return paper; }
    public void setPaper(ResearchPaper paper) { this.paper = paper; }
    
    public Double getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }
    
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    
    public Double getPlagiarismScore() { return plagiarismScore; }
    public void setPlagiarismScore(Double plagiarismScore) { this.plagiarismScore = plagiarismScore; }
    
    public AIAnalysis getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(AIAnalysis aiAnalysis) { this.aiAnalysis = aiAnalysis; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getVerificationTimestamp() { return verificationTimestamp; }
    public void setVerificationTimestamp(LocalDateTime verificationTimestamp) { this.verificationTimestamp = verificationTimestamp; }
    
    // Inner class for AI Analysis metrics
    public static class AIAnalysis {
        private Double embeddingScore;
        private Double titleSimilarity;
        private Double contentSimilarity;
        private Integer matchedPapersCount;
        private List<SimilarPaper> topMatches;
        
        // AI Detection results
        private Double aiDetectionScore;
        private String aiDetectionConclusion;
        private List<String> aiDetectionIndicators;
        
        public AIAnalysis() {}
        
        public AIAnalysis(Double embeddingScore, Double titleSimilarity, Double contentSimilarity) {
            this.embeddingScore = embeddingScore;
            this.titleSimilarity = titleSimilarity;
            this.contentSimilarity = contentSimilarity;
        }
        
        // Getters and Setters for existing fields
        public Double getEmbeddingScore() { return embeddingScore; }
        public void setEmbeddingScore(Double embeddingScore) { this.embeddingScore = embeddingScore; }
        
        public Double getTitleSimilarity() { return titleSimilarity; }
        public void setTitleSimilarity(Double titleSimilarity) { this.titleSimilarity = titleSimilarity; }
        
        public Double getContentSimilarity() { return contentSimilarity; }
        public void setContentSimilarity(Double contentSimilarity) { this.contentSimilarity = contentSimilarity; }
        
        public Integer getMatchedPapersCount() { return matchedPapersCount; }
        public void setMatchedPapersCount(Integer matchedPapersCount) { this.matchedPapersCount = matchedPapersCount; }
        
        public List<SimilarPaper> getTopMatches() { return topMatches; }
        public void setTopMatches(List<SimilarPaper> topMatches) { this.topMatches = topMatches; }
        
        // Getters and Setters for AI Detection
        public Double getAiDetectionScore() { return aiDetectionScore; }
        public void setAiDetectionScore(Double aiDetectionScore) { this.aiDetectionScore = aiDetectionScore; }
        
        public String getAiDetectionConclusion() { return aiDetectionConclusion; }
        public void setAiDetectionConclusion(String aiDetectionConclusion) { this.aiDetectionConclusion = aiDetectionConclusion; }
        
        public List<String> getAiDetectionIndicators() { return aiDetectionIndicators; }
        public void setAiDetectionIndicators(List<String> aiDetectionIndicators) { this.aiDetectionIndicators = aiDetectionIndicators; }
    }
    
    // Inner class for similar paper matches
    public static class SimilarPaper {
        private String paperId;
        private String title;
        private String author;
        private Double similarityScore;
        private String department;
        private LocalDateTime submissionDate;
        
        public SimilarPaper() {}
        
        public SimilarPaper(String paperId, String title, String author, Double similarityScore) {
            this.paperId = paperId;
            this.title = title;
            this.author = author;
            this.similarityScore = similarityScore;
        }
        
        // Getters and Setters
        public String getPaperId() { return paperId; }
        public void setPaperId(String paperId) { this.paperId = paperId; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public Double getSimilarityScore() { return similarityScore; }
        public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public LocalDateTime getSubmissionDate() { return submissionDate; }
        public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }
    }
    
    @Override
    public String toString() {
        return "ThesisVerificationResponse{" +
                "verified=" + verified +
                ", matchType='" + matchType + '\'' +
                ", similarityScore=" + similarityScore +
                ", plagiarismScore=" + plagiarismScore +
                ", verificationTimestamp=" + verificationTimestamp +
                '}';
    }
}