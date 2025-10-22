package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class PaperSubmissionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String abstract_;
    
    // The actual PDF file is handled separately in MultipartFile
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAbstract_() {
        return abstract_;
    }
    
    public void setAbstract_(String abstract_) {
        this.abstract_ = abstract_;
    }
}