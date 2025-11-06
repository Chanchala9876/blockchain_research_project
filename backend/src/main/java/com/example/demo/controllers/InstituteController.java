package com.example.demo.controllers;

import com.example.demo.models.Institute;
import com.example.demo.repositories.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/institutes")
@CrossOrigin(origins = "*")
public class InstituteController {

    @Autowired
    private InstituteRepository instituteRepository;

    @GetMapping
    public List<Institute> getAllInstitutes() {
        return instituteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institute> getInstituteById(@PathVariable String id) {
        Optional<Institute> institute = instituteRepository.findById(id);
        return institute.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Institute> getInstituteByName(@PathVariable String name) {
        Optional<Institute> institute = instituteRepository.findByName(name);
        return institute.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/{adminId}")
    public List<Institute> getInstitutesByAdmin(@PathVariable String adminId) {
        return instituteRepository.findByAdminId(adminId);
    }

    @GetMapping("/type/{type}")
    public List<Institute> getInstitutesByType(@PathVariable String type) {
        return instituteRepository.findByInstituteType(type);
    }

    @PostMapping
    public Institute createInstitute(@RequestBody Institute institute) {
        institute.setCreatedAt(LocalDateTime.now());
        institute.setUpdatedAt(LocalDateTime.now());
        return instituteRepository.save(institute);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Institute> updateInstitute(@PathVariable String id, 
                                                   @RequestBody Institute instituteDetails) {
        Optional<Institute> optionalInstitute = instituteRepository.findById(id);
        
        if (optionalInstitute.isPresent()) {
            Institute institute = optionalInstitute.get();
            
            // Update fields
            institute.setName(instituteDetails.getName());
            institute.setAddress(instituteDetails.getAddress());
            institute.setCity(instituteDetails.getCity());
            institute.setState(instituteDetails.getState());
            institute.setCountry(instituteDetails.getCountry());
            institute.setPincode(instituteDetails.getPincode());
            institute.setPhoneNumber(instituteDetails.getPhoneNumber());
            institute.setEmail(instituteDetails.getEmail());
            institute.setWebsite(instituteDetails.getWebsite());
            institute.setEstablishedYear(instituteDetails.getEstablishedYear());
            institute.setInstituteType(instituteDetails.getInstituteType());
            institute.setAffiliation(instituteDetails.getAffiliation());
            institute.setDepartments(instituteDetails.getDepartments());
            institute.setAdminId(instituteDetails.getAdminId());
            institute.setUpdatedAt(LocalDateTime.now());
            
            return ResponseEntity.ok(instituteRepository.save(institute));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitute(@PathVariable String id) {
        if (instituteRepository.existsById(id)) {
            instituteRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}