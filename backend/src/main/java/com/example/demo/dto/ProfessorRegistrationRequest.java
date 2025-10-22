package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfessorRegistrationRequest {
    @NotBlank
    private String departmentId;
    
    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    @NotBlank
    private String departmentName;
    
    @NotBlank
    private String designation;
    
    @NotBlank
    private String qualification;
    
    private String specialization;
    private String phoneNumber;
    private String officeLocation;
    
    @NotBlank
    private String employeeId;
    
    private String experience;
    private String researchInterests;
    
    public ProfessorRegistrationRequest() {}
    
    // Getters
    public String getDepartmentId() { return departmentId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDepartmentName() { return departmentName; }
    public String getDesignation() { return designation; }
    public String getQualification() { return qualification; }
    public String getSpecialization() { return specialization; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getOfficeLocation() { return officeLocation; }
    public String getEmployeeId() { return employeeId; }
    public String getExperience() { return experience; }
    public String getResearchInterests() { return researchInterests; }
    
    // Setters
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setDesignation(String designation) { this.designation = designation; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setResearchInterests(String researchInterests) { this.researchInterests = researchInterests; }
}