package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    
    Optional<Admin> findByEmail(String email);
    
    List<Admin> findByRole(String role);
    
    List<Admin> findByIsActive(boolean isActive);
    
    Page<Admin> findByRole(String role, Pageable pageable);
    
    Page<Admin> findByIsActive(boolean isActive, Pageable pageable);
    
    List<Admin> findByNameContainingIgnoreCase(String name);
    
    List<Admin> findByLastLoginTimeBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByEmail(String email);
    
    long countByIsActive(boolean isActive);
}