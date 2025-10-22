package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection = "admins")
public class Admin implements UserDetails {
    
    @Id
    private String id;
    private String email;
    private String password;
    private String name;
    private String role; // Super Admin, System Admin, etc.
    private String instituteId; // Reference to Institute model
    private boolean isActive;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public Admin() {}
    
    public Admin(String id, String email, String password, String name, String role,
                String instituteId, boolean isActive, String lastLoginIp, LocalDateTime lastLoginTime,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.instituteId = instituteId;
        this.isActive = isActive;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginTime = lastLoginTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getInstituteId() { return instituteId; }
    public boolean isActive() { return isActive; }
    public String getLastLoginIp() { return lastLoginIp; }
    public LocalDateTime getLastLoginTime() { return lastLoginTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setInstituteId(String instituteId) { this.instituteId = instituteId; }
    public void setActive(boolean active) { isActive = active; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }
    public void setLastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Builder pattern
    public static AdminBuilder builder() {
        return new AdminBuilder();
    }
    
    public static class AdminBuilder {
        private String id;
        private String email;
        private String password;
        private String name;
        private String role;
        private String instituteId;
        private boolean isActive = true;
        private String lastLoginIp;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public AdminBuilder id(String id) { this.id = id; return this; }
        public AdminBuilder email(String email) { this.email = email; return this; }
        public AdminBuilder password(String password) { this.password = password; return this; }
        public AdminBuilder name(String name) { this.name = name; return this; }
        public AdminBuilder role(String role) { this.role = role; return this; }
        public AdminBuilder instituteId(String instituteId) { this.instituteId = instituteId; return this; }
        public AdminBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public AdminBuilder lastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; return this; }
        public AdminBuilder lastLoginTime(LocalDateTime lastLoginTime) { this.lastLoginTime = lastLoginTime; return this; }
        public AdminBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AdminBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Admin build() {
            return new Admin(id, email, password, name, role, instituteId, isActive, lastLoginIp, 
                           lastLoginTime, createdAt, updatedAt);
        }
    }
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive;
    }
    
    @Override
    public String toString() {
        return "Admin{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}