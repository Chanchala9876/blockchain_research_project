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

@Document(collection = "professors")
public class Professor implements UserDetails {
    
    @Id
    private String id;
    private String departmentId;
    private String name;
    private String email;
    private String password;
    private String departmentName;
    private String institute; // Institute/University name
    private String instituteId; // Reference to Institute model
    private String designation; // Professor, Associate Professor, Assistant Professor
    private String qualification; // PhD, Masters, etc.
    private String specialization; // Field of expertise
    private String phoneNumber;
    private String officeLocation;
    private String employeeId;
    private String experience; // Years of experience
    private String researchInterests;
    private boolean isActive;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public Professor() {}
    
    public Professor(String id, String departmentId, String name, String email, String password,
                    String departmentName, String institute, String instituteId, String designation, String qualification, String specialization,
                    String phoneNumber, String officeLocation, String employeeId, String experience,
                    String researchInterests, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.departmentName = departmentName;
        this.institute = institute;
        this.instituteId = instituteId;
        this.designation = designation;
        this.qualification = qualification;
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.officeLocation = officeLocation;
        this.employeeId = employeeId;
        this.experience = experience;
        this.researchInterests = researchInterests;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getId() { return id; }
    public String getDepartmentId() { return departmentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartmentName() { return departmentName; }
    public String getInstitute() { return institute; }
    public String getInstituteId() { return instituteId; }
    public String getDesignation() { return designation; }
    public String getQualification() { return qualification; }
    public String getSpecialization() { return specialization; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOfficeLocation() { return officeLocation; }
    public String getEmployeeId() { return employeeId; }
    public String getExperience() { return experience; }
    public String getResearchInterests() { return researchInterests; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setInstitute(String institute) { this.institute = institute; }
    public void setInstituteId(String instituteId) { this.instituteId = instituteId; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setResearchInterests(String researchInterests) { this.researchInterests = researchInterests; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Builder pattern
    public static ProfessorBuilder builder() {
        return new ProfessorBuilder();
    }
    
    public static class ProfessorBuilder {
        private String id;
        private String departmentId;
        private String name;
        private String email;
        private String password;
        private String departmentName;
        private String institute;
        private String instituteId;
        private String designation;
        private String qualification;
        private String specialization;
        private String phoneNumber;
        private String officeLocation;
        private String employeeId;
        private String experience;
        private String researchInterests;
        private boolean isActive = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public ProfessorBuilder id(String id) { this.id = id; return this; }
        public ProfessorBuilder departmentId(String departmentId) { this.departmentId = departmentId; return this; }
        public ProfessorBuilder name(String name) { this.name = name; return this; }
        public ProfessorBuilder email(String email) { this.email = email; return this; }
        public ProfessorBuilder password(String password) { this.password = password; return this; }
        public ProfessorBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public ProfessorBuilder institute(String institute) { this.institute = institute; return this; }
        public ProfessorBuilder instituteId(String instituteId) { this.instituteId = instituteId; return this; }
        public ProfessorBuilder designation(String designation) { this.designation = designation; return this; }
        public ProfessorBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public ProfessorBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ProfessorBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public ProfessorBuilder officeLocation(String officeLocation) { this.officeLocation = officeLocation; return this; }
        public ProfessorBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public ProfessorBuilder experience(String experience) { this.experience = experience; return this; }
        public ProfessorBuilder researchInterests(String researchInterests) { this.researchInterests = researchInterests; return this; }
        public ProfessorBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public ProfessorBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProfessorBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Professor build() {
            return new Professor(id, departmentId, name, email, password, departmentName, institute,
                               instituteId, designation, qualification, specialization, phoneNumber, 
                               officeLocation, employeeId, experience, researchInterests, isActive, 
                               createdAt, updatedAt);
        }
    }
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
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
        return "Professor{" +
                "id='" + id + '\'' +
                ", departmentId='" + departmentId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", designation='" + designation + '\'' +
                ", qualification='" + qualification + '\'' +
                ", specialization='" + specialization + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", experience='" + experience + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}