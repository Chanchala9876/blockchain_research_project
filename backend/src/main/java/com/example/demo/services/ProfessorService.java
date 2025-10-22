package com.example.demo.services;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ProfessorRegistrationRequest;
import com.example.demo.dto.ProfessorResponse;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.Professor;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.security.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfessorService {
    
    private static final Logger log = LoggerFactory.getLogger(ProfessorService.class);
    
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    
    public ProfessorService(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    public JwtResponse authenticateProfessor(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        Professor professor = (Professor) authentication.getPrincipal();
        
        return JwtResponse.builder()
                .token(jwt)
                .id(professor.getId())
                .name(professor.getName())
                .email(professor.getEmail())
                .role("ROLE_PROFESSOR")
                .build();
    }
    
    public ProfessorResponse registerProfessor(ProfessorRegistrationRequest request) {
        // Check if professor already exists
        if (professorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Professor with this email already exists");
        }
        
        if (professorRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Professor with this employee ID already exists");
        }
        
        // Create new professor
        Professor professor = Professor.builder()
                .departmentId(request.getDepartmentId())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .departmentName(request.getDepartmentName())
                .designation(request.getDesignation())
                .qualification(request.getQualification())
                .specialization(request.getSpecialization())
                .phoneNumber(request.getPhoneNumber())
                .officeLocation(request.getOfficeLocation())
                .employeeId(request.getEmployeeId())
                .experience(request.getExperience())
                .researchInterests(request.getResearchInterests())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Professor savedProfessor = professorRepository.save(professor);
        log.info("Professor registered successfully with email: {}", request.getEmail());
        
        return mapProfessorToResponse(savedProfessor);
    }
    
    public ProfessorResponse getProfessorById(String id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        return mapProfessorToResponse(professor);
    }
    
    public ProfessorResponse getProfessorByEmail(String email) {
        Professor professor = professorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        return mapProfessorToResponse(professor);
    }
    
    public ProfessorResponse getProfessorByEmployeeId(String employeeId) {
        Professor professor = professorRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        return mapProfessorToResponse(professor);
    }
    
    public Page<ProfessorResponse> getAllProfessors(Pageable pageable) {
        Page<Professor> professors = professorRepository.findAll(pageable);
        return professors.map(this::mapProfessorToResponse);
    }
    
    public Page<ProfessorResponse> getProfessorsByDepartment(String departmentId, Pageable pageable) {
        Page<Professor> professors = professorRepository.findByDepartmentId(departmentId, pageable);
        return professors.map(this::mapProfessorToResponse);
    }
    
    public Page<ProfessorResponse> getProfessorsByDesignation(String designation, Pageable pageable) {
        Page<Professor> professors = professorRepository.findByDesignation(designation, pageable);
        return professors.map(this::mapProfessorToResponse);
    }
    
    public Page<ProfessorResponse> getActiveProfessors(Pageable pageable) {
        Page<Professor> professors = professorRepository.findByIsActive(true, pageable);
        return professors.map(this::mapProfessorToResponse);
    }
    
    public ProfessorResponse updateProfessor(String id, ProfessorRegistrationRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        
        // Check if email is being changed and if new email already exists
        if (!professor.getEmail().equals(request.getEmail()) && 
            professorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Professor with this email already exists");
        }
        
        // Check if employee ID is being changed and if new employee ID already exists
        if (!professor.getEmployeeId().equals(request.getEmployeeId()) && 
            professorRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Professor with this employee ID already exists");
        }
        
        // Update professor fields
        professor.setDepartmentId(request.getDepartmentId());
        professor.setName(request.getName());
        professor.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            professor.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        professor.setDepartmentName(request.getDepartmentName());
        professor.setDesignation(request.getDesignation());
        professor.setQualification(request.getQualification());
        professor.setSpecialization(request.getSpecialization());
        professor.setPhoneNumber(request.getPhoneNumber());
        professor.setOfficeLocation(request.getOfficeLocation());
        professor.setEmployeeId(request.getEmployeeId());
        professor.setExperience(request.getExperience());
        professor.setResearchInterests(request.getResearchInterests());
        professor.setUpdatedAt(LocalDateTime.now());
        
        Professor updatedProfessor = professorRepository.save(professor);
        log.info("Professor updated successfully with ID: {}", id);
        
        return mapProfessorToResponse(updatedProfessor);
    }
    
    public void deactivateProfessor(String id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        
        professor.setActive(false);
        professor.setUpdatedAt(LocalDateTime.now());
        professorRepository.save(professor);
        
        log.info("Professor deactivated successfully with ID: {}", id);
    }
    
    public void activateProfessor(String id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        
        professor.setActive(true);
        professor.setUpdatedAt(LocalDateTime.now());
        professorRepository.save(professor);
        
        log.info("Professor activated successfully with ID: {}", id);
    }
    
    private ProfessorResponse mapProfessorToResponse(Professor professor) {
        return ProfessorResponse.builder()
                .id(professor.getId())
                .departmentId(professor.getDepartmentId())
                .name(professor.getName())
                .email(professor.getEmail())
                .departmentName(professor.getDepartmentName())
                .designation(professor.getDesignation())
                .qualification(professor.getQualification())
                .specialization(professor.getSpecialization())
                .phoneNumber(professor.getPhoneNumber())
                .officeLocation(professor.getOfficeLocation())
                .employeeId(professor.getEmployeeId())
                .experience(professor.getExperience())
                .researchInterests(professor.getResearchInterests())
                .isActive(professor.isActive())
                .createdAt(professor.getCreatedAt())
                .updatedAt(professor.getUpdatedAt())
                .build();
    }
}