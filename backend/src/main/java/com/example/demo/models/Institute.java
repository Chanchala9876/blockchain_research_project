package com.example.demo.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "institutes")
public class Institute {
    
    @Id
    private String id;
    
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String phoneNumber;
    private String email;
    private String website;
    private String establishedYear;
    private String instituteType; // UNIVERSITY, COLLEGE, RESEARCH_INSTITUTE
    private String affiliation; // UGC, AICTE, etc.
    private List<String> departments; // List of department names/IDs
    private String adminId; // Reference to Admin model
    private boolean isActive;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Institute() {}
    
    public Institute(String id, String name, String address, String city, String state, 
                    String country, String pincode, String phoneNumber, String email, String website,
                    String establishedYear, String instituteType, String affiliation, 
                    List<String> departments, String adminId, boolean isActive, 
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.establishedYear = establishedYear;
        this.instituteType = instituteType;
        this.affiliation = affiliation;
        this.departments = departments;
        this.adminId = adminId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getPincode() { return pincode; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getEstablishedYear() { return establishedYear; }
    public String getInstituteType() { return instituteType; }
    public String getAffiliation() { return affiliation; }
    public List<String> getDepartments() { return departments; }
    public String getAdminId() { return adminId; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setWebsite(String website) { this.website = website; }
    public void setEstablishedYear(String establishedYear) { this.establishedYear = establishedYear; }
    public void setInstituteType(String instituteType) { this.instituteType = instituteType; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    public void setDepartments(List<String> departments) { this.departments = departments; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Builder pattern
    public static InstituteBuilder builder() {
        return new InstituteBuilder();
    }
    
    public static class InstituteBuilder {
        private String id;
        private String name;
        private String address;
        private String city;
        private String state;
        private String country;
        private String pincode;
        private String phoneNumber;
        private String email;
        private String website;
        private String establishedYear;
        private String instituteType;
        private String affiliation;
        private List<String> departments;
        private String adminId;
        private boolean isActive = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public InstituteBuilder id(String id) { this.id = id; return this; }
        public InstituteBuilder name(String name) { this.name = name; return this; }
        public InstituteBuilder address(String address) { this.address = address; return this; }
        public InstituteBuilder city(String city) { this.city = city; return this; }
        public InstituteBuilder state(String state) { this.state = state; return this; }
        public InstituteBuilder country(String country) { this.country = country; return this; }
        public InstituteBuilder pincode(String pincode) { this.pincode = pincode; return this; }
        public InstituteBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public InstituteBuilder email(String email) { this.email = email; return this; }
        public InstituteBuilder website(String website) { this.website = website; return this; }
        public InstituteBuilder establishedYear(String establishedYear) { this.establishedYear = establishedYear; return this; }
        public InstituteBuilder instituteType(String instituteType) { this.instituteType = instituteType; return this; }
        public InstituteBuilder affiliation(String affiliation) { this.affiliation = affiliation; return this; }
        public InstituteBuilder departments(List<String> departments) { this.departments = departments; return this; }
        public InstituteBuilder adminId(String adminId) { this.adminId = adminId; return this; }
        public InstituteBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public InstituteBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public InstituteBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Institute build() {
            return new Institute(id, name, address, city, state, country, pincode, phoneNumber,
                               email, website, establishedYear, instituteType, affiliation, 
                               departments, adminId, isActive, createdAt, updatedAt);
        }
    }
    
    @Override
    public String toString() {
        return "Institute{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", pincode='" + pincode + '\'' +
                ", instituteType='" + instituteType + '\'' +
                ", adminId='" + adminId + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}