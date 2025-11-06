package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

public class ThesisVerificationRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotNull(message = "Submission year is required")
    @Min(value = 1900, message = "Submission year must be after 1900")
    @Max(value = 2030, message = "Submission year cannot be in the future")
    private Integer submissionYear;
    
    // Optional fields
    private String institution;
    private String abstractText;
    private List<String> keywords;
    private String supervisor;
    private String coSupervisor;
    
    // File metadata (calculated from uploaded file)
    private String fileName;
    private Long fileSize;
    private String fileHash;
    
    // Constructors
    public ThesisVerificationRequest() {}
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Integer getSubmissionYear() { return submissionYear; }
    public void setSubmissionYear(Integer submissionYear) { this.submissionYear = submissionYear; }
    
    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }
    
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    
    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String supervisor) { this.supervisor = supervisor; }
    
    public String getCoSupervisor() { return coSupervisor; }
    public void setCoSupervisor(String coSupervisor) { this.coSupervisor = coSupervisor; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    
    @Override
    public String toString() {
        return "ThesisVerificationRequest{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", department='" + department + '\'' +
                ", submissionYear=" + submissionYear +
                ", institution='" + institution + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}