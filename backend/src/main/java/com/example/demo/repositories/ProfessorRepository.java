package com.example.demo.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Professor;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends MongoRepository<Professor, String> {
    
    Optional<Professor> findByEmail(String email);
    
    Optional<Professor> findByEmployeeId(String employeeId);
    
    List<Professor> findByDepartmentId(String departmentId);
    
    List<Professor> findByDepartmentName(String departmentName);
    
    List<Professor> findByDesignation(String designation);
    
    List<Professor> findByIsActive(boolean isActive);
    
    Page<Professor> findByDepartmentId(String departmentId, Pageable pageable);
    
    Page<Professor> findByDepartmentName(String departmentName, Pageable pageable);
    
    Page<Professor> findByDesignation(String designation, Pageable pageable);
    
    Page<Professor> findByIsActive(boolean isActive, Pageable pageable);
    
    List<Professor> findBySpecializationContainingIgnoreCase(String specialization);
    
    List<Professor> findByNameContainingIgnoreCase(String name);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeId(String employeeId);
}