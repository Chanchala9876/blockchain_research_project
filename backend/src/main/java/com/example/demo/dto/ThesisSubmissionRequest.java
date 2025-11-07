package com.example.demo.dto;

import java.util.List;

/**
 * Request DTO for thesis submission in the approval workflow
 */
public class ThesisSubmissionRequest {
    private String title;
    private String author;
    private String department;
    private String institution;
    private String supervisor;
    private String coSupervisor;
    private String abstractText;
    private List<String> keywords;
    private String submittedBy; // Admin ID who submitted
    
    public ThesisSubmissionRequest() {}
    
    public ThesisSubmissionRequest(String title, String author, String department, String institution,
                                  String supervisor, String coSupervisor, String abstractText,
                                  List<String> keywords, String submittedBy) {
        this.title = title;
        this.author = author;
        this.department = department;
        this.institution = institution;
        this.supervisor = supervisor;
        this.coSupervisor = coSupervisor;
        this.abstractText = abstractText;
        this.keywords = keywords;
        this.submittedBy = submittedBy;
    }
    
    // Builder pattern
    public static ThesisSubmissionRequestBuilder builder() {
        return new ThesisSubmissionRequestBuilder();
    }
    
    public static class ThesisSubmissionRequestBuilder {
        private String title;
        private String author;
        private String department;
        private String institution;
        private String supervisor;
        private String coSupervisor;
        private String abstractText;
        private List<String> keywords;
        private String submittedBy;
        
        public ThesisSubmissionRequestBuilder title(String title) {
            this.title = title;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder author(String author) {
            this.author = author;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder department(String department) {
            this.department = department;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder institution(String institution) {
            this.institution = institution;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder supervisor(String supervisor) {
            this.supervisor = supervisor;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder coSupervisor(String coSupervisor) {
            this.coSupervisor = coSupervisor;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder abstractText(String abstractText) {
            this.abstractText = abstractText;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder keywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }
        
        public ThesisSubmissionRequestBuilder submittedBy(String submittedBy) {
            this.submittedBy = submittedBy;
            return this;
        }
        
        public ThesisSubmissionRequest build() {
            return new ThesisSubmissionRequest(title, author, department, institution,
                                             supervisor, coSupervisor, abstractText, keywords, submittedBy);
        }
    }
    
    // Getters and setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getInstitution() {
        return institution;
    }
    
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    
    public String getSupervisor() {
        return supervisor;
    }
    
    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }
    
    public String getCoSupervisor() {
        return coSupervisor;
    }
    
    public void setCoSupervisor(String coSupervisor) {
        this.coSupervisor = coSupervisor;
    }
    
    public String getAbstractText() {
        return abstractText;
    }
    
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }
    
    public List<String> getKeywords() {
        return keywords;
    }
    
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
    
    public String getSubmittedBy() {
        return submittedBy;
    }
    
    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }
}