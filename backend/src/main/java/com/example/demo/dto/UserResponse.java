package com.example.demo.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String institute;
    private String subject;
    private String role;
    private LocalDateTime createdAt;
    
    public UserResponse() {}
    
    public UserResponse(String id, String name, String email, String institute,
                       String subject, String role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.institute = institute;
        this.subject = subject;
        this.role = role;
        this.createdAt = createdAt;
    }
    
    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }
    
    public static class UserResponseBuilder {
        private String id;
        private String name;
        private String email;
        private String institute;
        private String subject;
        private String role;
        private LocalDateTime createdAt;
        
        public UserResponseBuilder id(String id) { this.id = id; return this; }
        public UserResponseBuilder name(String name) { this.name = name; return this; }
        public UserResponseBuilder email(String email) { this.email = email; return this; }
        public UserResponseBuilder institute(String institute) { this.institute = institute; return this; }
        public UserResponseBuilder subject(String subject) { this.subject = subject; return this; }
        public UserResponseBuilder role(String role) { this.role = role; return this; }
        public UserResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        
        public UserResponse build() {
            return new UserResponse(id, name, email, institute, subject, role, createdAt);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getInstitute() {
        return institute;
    }
    
    public void setInstitute(String institute) {
        this.institute = institute;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}