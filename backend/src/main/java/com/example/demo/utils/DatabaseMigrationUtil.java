package com.example.demo.utils;

import com.example.demo.models.Professor;
import com.example.demo.models.Institute;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.repositories.InstituteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DatabaseMigrationUtil {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationUtil.class);
    
    @Autowired
    private ProfessorRepository professorRepository;
    
    @Autowired
    private InstituteRepository instituteRepository;
    
    public void updateProfessorsWithInstitute() {
        log.info("Starting professor institute migration...");
        
        // Get JNU Institute
        Optional<Institute> jnuOpt = instituteRepository.findByName("Jawaharlal Nehru University");
        if (!jnuOpt.isPresent()) {
            log.error("JNU Institute not found! Cannot update professors.");
            return;
        }
        
        Institute jnu = jnuOpt.get();
        String jnuId = jnu.getId();
        
        // Get all professors that need update (missing institute field)
        List<Professor> allProfessors = professorRepository.findAll();
        int updatedCount = 0;
        
        for (Professor professor : allProfessors) {
            // Check if institute field is missing or empty
            if (professor.getInstitute() == null || professor.getInstitute().isEmpty() ||
                professor.getInstituteId() == null || professor.getInstituteId().isEmpty()) {
                
                professor.setInstitute("Jawaharlal Nehru University");
                professor.setInstituteId(jnuId);
                
                professorRepository.save(professor);
                updatedCount++;
                
                log.info("Updated professor {} with institute information", professor.getEmail());
            }
        }
        
        log.info("Professor institute migration completed. Updated {} professors.", updatedCount);
    }
    
    public void logInstituteInfo() {
        log.info("=== Institute Information ===");
        List<Institute> institutes = instituteRepository.findAll();
        for (Institute institute : institutes) {
            log.info("Institute: {} (ID: {})", institute.getName(), institute.getId());
            log.info("  Address: {}, {}, {}", institute.getAddress(), institute.getCity(), institute.getState());
            log.info("  Admin ID: {}", institute.getAdminId());
            log.info("  Departments: {}", institute.getDepartments());
        }
        log.info("=== End Institute Information ===");
    }
}