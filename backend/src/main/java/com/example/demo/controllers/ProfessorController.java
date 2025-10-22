package com.example.demo.controllers;

import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.ProfessorRegistrationRequest;
import com.example.demo.dto.ProfessorResponse;
import com.example.demo.services.ProfessorService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {
    
    private final ProfessorService professorService;
    
    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginProfessor(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = professorService.authenticateProfessor(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProfessorResponse> registerProfessor(@Valid @RequestBody ProfessorRegistrationRequest request) {
        ProfessorResponse professor = professorService.registerProfessor(request);
        return ResponseEntity.ok(professor);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR')")
    public ResponseEntity<ProfessorResponse> getProfessorById(@PathVariable String id) {
        ProfessorResponse professor = professorService.getProfessorById(id);
        return ResponseEntity.ok(professor);
    }
    
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR')")
    public ResponseEntity<ProfessorResponse> getProfessorByEmail(@PathVariable String email) {
        ProfessorResponse professor = professorService.getProfessorByEmail(email);
        return ResponseEntity.ok(professor);
    }
    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR')")
    public ResponseEntity<ProfessorResponse> getProfessorByEmployeeId(@PathVariable String employeeId) {
        ProfessorResponse professor = professorService.getProfessorByEmployeeId(employeeId);
        return ResponseEntity.ok(professor);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ProfessorResponse>> getAllProfessors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, limit, sort);
        
        Page<ProfessorResponse> professors = professorService.getAllProfessors(pageable);
        return ResponseEntity.ok(professors);
    }
    
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PROFESSOR')")
    public ResponseEntity<Page<ProfessorResponse>> getProfessorsByDepartment(
            @PathVariable String departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name").ascending());
        Page<ProfessorResponse> professors = professorService.getProfessorsByDepartment(departmentId, pageable);
        return ResponseEntity.ok(professors);
    }
    
    @GetMapping("/designation/{designation}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ProfessorResponse>> getProfessorsByDesignation(
            @PathVariable String designation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name").ascending());
        Page<ProfessorResponse> professors = professorService.getProfessorsByDesignation(designation, pageable);
        return ResponseEntity.ok(professors);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<ProfessorResponse>> getActiveProfessors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("name").ascending());
        Page<ProfessorResponse> professors = professorService.getActiveProfessors(pageable);
        return ResponseEntity.ok(professors);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProfessorResponse> updateProfessor(
            @PathVariable String id,
            @Valid @RequestBody ProfessorRegistrationRequest request) {
        ProfessorResponse professor = professorService.updateProfessor(id, request);
        return ResponseEntity.ok(professor);
    }
    
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deactivateProfessor(@PathVariable String id) {
        professorService.deactivateProfessor(id);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> activateProfessor(@PathVariable String id) {
        professorService.activateProfessor(id);
        return ResponseEntity.ok().build();
    }
}