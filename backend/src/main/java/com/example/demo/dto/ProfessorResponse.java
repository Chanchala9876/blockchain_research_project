package com.example.demo.dto;

import java.time.LocalDateTime;

public class ProfessorResponse {
    private String id;
    private String departmentId;
    private String name;
    private String email;
    private String departmentName;
    private String designation;
    private String qualification;
    private String specialization;
    private String phoneNumber;
    private String officeLocation;
    private String employeeId;
    private String experience;
    private String researchInterests;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProfessorResponse() {}
    
    public ProfessorResponse(String id, String departmentId, String name, String email,
                           String departmentName, String designation, String qualification,
                           String specialization, String phoneNumber, String officeLocation,
                           String employeeId, String experience, String researchInterests,
                           boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.name = name;
        this.email = email;
        this.departmentName = departmentName;
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
    
    public static ProfessorResponseBuilder builder() {
        return new ProfessorResponseBuilder();
    }
    
    public static class ProfessorResponseBuilder {
        private String id;
        private String departmentId;
        private String name;
        private String email;
        private String departmentName;
        private String designation;
        private String qualification;
        private String specialization;
        private String phoneNumber;
        private String officeLocation;
        private String employeeId;
        private String experience;
        private String researchInterests;
        private boolean isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public ProfessorResponseBuilder id(String id) { this.id = id; return this; }
        public ProfessorResponseBuilder departmentId(String departmentId) { this.departmentId = departmentId; return this; }
        public ProfessorResponseBuilder name(String name) { this.name = name; return this; }
        public ProfessorResponseBuilder email(String email) { this.email = email; return this; }
        public ProfessorResponseBuilder departmentName(String departmentName) { this.departmentName = departmentName; return this; }
        public ProfessorResponseBuilder designation(String designation) { this.designation = designation; return this; }
        public ProfessorResponseBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public ProfessorResponseBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public ProfessorResponseBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public ProfessorResponseBuilder officeLocation(String officeLocation) { this.officeLocation = officeLocation; return this; }
        public ProfessorResponseBuilder employeeId(String employeeId) { this.employeeId = employeeId; return this; }
        public ProfessorResponseBuilder experience(String experience) { this.experience = experience; return this; }
        public ProfessorResponseBuilder researchInterests(String researchInterests) { this.researchInterests = researchInterests; return this; }
        public ProfessorResponseBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public ProfessorResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ProfessorResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public ProfessorResponse build() {
            return new ProfessorResponse(id, departmentId, name, email, departmentName, designation,
                                       qualification, specialization, phoneNumber, officeLocation,
                                       employeeId, experience, researchInterests, isActive, createdAt, updatedAt);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getDepartmentId() { return departmentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartmentName() { return departmentName; }
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
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
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
}