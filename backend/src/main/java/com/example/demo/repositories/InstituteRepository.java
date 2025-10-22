package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Institute;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstituteRepository extends MongoRepository<Institute, String> {
    
    /**
     * Find institute by name
     */
    Optional<Institute> findByName(String name);
    
    /**
     * Find institutes by name containing (case insensitive)
     */
    List<Institute> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find institutes by type
     */
    List<Institute> findByInstituteType(String instituteType);
    
    /**
     * Find institutes by city
     */
    List<Institute> findByCity(String city);
    
    /**
     * Find institutes by state
     */
    List<Institute> findByState(String state);
    
    /**
     * Find institutes by country
     */
    List<Institute> findByCountry(String country);
    
    /**
     * Find institutes by admin ID
     */
    List<Institute> findByAdminId(String adminId);
    
    /**
     * Find active institutes
     */
    List<Institute> findByIsActive(boolean isActive);
    
    /**
     * Find institutes by type with pagination
     */
    Page<Institute> findByInstituteType(String instituteType, Pageable pageable);
    
    /**
     * Find institutes by state with pagination
     */
    Page<Institute> findByState(String state, Pageable pageable);
    
    /**
     * Find institutes by admin ID with pagination
     */
    Page<Institute> findByAdminId(String adminId, Pageable pageable);
    
    /**
     * Find active institutes with pagination
     */
    Page<Institute> findByIsActive(boolean isActive, Pageable pageable);
    
    /**
     * Check if institute name exists
     */
    boolean existsByName(String name);
    
    /**
     * Count institutes by admin ID
     */
    long countByAdminId(String adminId);
    
    /**
     * Count active institutes
     */
    long countByIsActive(boolean isActive);
}