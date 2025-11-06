package com.example.demo.dto;

import java.time.LocalDateTime;

public class AdminResponse {
    private String id;
    private String email;
    private String name;
    private String role;
    private boolean isActive;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public AdminResponse() {}
    
    public AdminResponse(String id, String email, String name, String role, boolean isActive,
                        String lastLoginIp, LocalDateTime lastLoginTime, LocalDateTime createdAt,
                        LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.isActive = isActive;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginTime = lastLoginTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public static AdminResponseBuilder builder() {
        return new AdminResponseBuilder();
    }
    
    public static class AdminResponseBuilder {
        private String id;
        private String email;
        private String name;
        private String role;
        private boolean isActive;
        private String lastLoginIp;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public AdminResponseBuilder id(String id) { this.id = id; return this; }
        public AdminResponseBuilder email(String email) { this.email = email; return this; }
        public AdminResponseBuilder name(String name) { this.name = name; return this; }
        public AdminResponseBuilder role(String role) { this.role = role; return this; }
        public AdminResponseBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public AdminResponseBuilder lastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; return this; }
        public AdminResponseBuilder lastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; return this; }
        public AdminResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AdminResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public AdminResponse build() {
            return new AdminResponse(id, email, name, role, isActive, lastLoginIp, 
                                   lastLoginTime, createdAt, updatedAt);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public boolean isActive() { return isActive; }
    public String getLastLoginIp() { return lastLoginIp; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setActive(boolean active) { isActive = active; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}