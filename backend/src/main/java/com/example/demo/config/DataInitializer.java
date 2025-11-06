package com.example.demo.config;

import com.example.demo.models.User;
import com.example.demo.models.Professor;
import com.example.demo.models.Admin;
import com.example.demo.models.Institute;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.InstituteRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Component
public class DataInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final InstituteRepository instituteRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, ProfessorRepository professorRepository,
                          AdminRepository adminRepository, InstituteRepository instituteRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.adminRepository = adminRepository;
        this.instituteRepository = instituteRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostConstruct
    public void init() {
        try {
            initializeInstitutes();
            initializeAdmins();
            initializeUsers();
            initializeProfessors();
        } catch (Exception e) {
            log.error("Error during data initialization", e);
        }
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Initializing demo users...");
            
            // Demo User 1 - Computer Science Student
            if (!userRepository.existsByEmail("rahul.sharma@jnu.ac.in")) {
                User user1 = User.builder()
                        .name("Rahul Sharma")
                        .email("rahul.sharma@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .institute("Jawaharlal Nehru University")
                        .subject("Computer Science")
                        .role("researcher")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user1);
                log.info("Created demo user: {}", user1.getEmail());
            }
            
            // Demo User 2 - Physics Student
            if (!userRepository.existsByEmail("priya.patel@jnu.ac.in")) {
                User user2 = User.builder()
                        .name("Priya Patel")
                        .email("priya.patel@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .institute("Jawaharlal Nehru University")
                        .subject("Physics")
                        .role("researcher")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user2);
                log.info("Created demo user: {}", user2.getEmail());
            }
            
            // Demo User 3 - Mathematics Student
            if (!userRepository.existsByEmail("amit.kumar@jnu.ac.in")) {
                User user3 = User.builder()
                        .name("Amit Kumar")
                        .email("amit.kumar@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .institute("Jawaharlal Nehru University")
                        .subject("Mathematics")
                        .role("researcher")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user3);
                log.info("Created demo user: {}", user3.getEmail());
            }
            
            // Demo User 4 - Chemistry Student
            if (!userRepository.existsByEmail("sneha.gupta@jnu.ac.in")) {
                User user4 = User.builder()
                        .name("Sneha Gupta")
                        .email("sneha.gupta@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .institute("Jawaharlal Nehru University")
                        .subject("Chemistry")
                        .role("researcher")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user4);
                log.info("Created demo user: {}", user4.getEmail());
            }
            
            // Demo User 5 - Biology Student
            if (!userRepository.existsByEmail("vikash.singh@jnu.ac.in")) {
                User user5 = User.builder()
                        .name("Vikash Singh")
                        .email("vikash.singh@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .institute("Jawaharlal Nehru University")
                        .subject("Biology")
                        .role("researcher")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userRepository.save(user5);
                log.info("Created demo user: {}", user5.getEmail());
            }
        }
    }
    
    private void initializeInstitutes() {
        if (instituteRepository.count() == 0) {
            log.info("Initializing demo institutes...");
            
            // Create JNU Institute
            Institute jnu = Institute.builder()
                    .name("Jawaharlal Nehru University")
                    .address("New Mehrauli Road")
                    .city("New Delhi")
                    .state("Delhi")
                    .country("India")
                    .pincode("110067")
                    .phoneNumber("+91-11-26704000")
                    .email("info@jnu.ac.in")
                    .website("https://www.jnu.ac.in")
                    .establishedYear("1969")
                    .instituteType("Central University")
                    .affiliation("University Grants Commission")
                    .departments(java.util.Arrays.asList(
                        "Computer Science", "Physics", "Mathematics", 
                        "Chemistry", "Biology", "Engineering", 
                        "Social Sciences", "Humanities", "Languages"
                    ))
                    .adminId(null)  // Will be updated after admin creation
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            instituteRepository.save(jnu);
            log.info("Created institute: {}", jnu.getName());
            
            // Create IIT Delhi Institute 
            Institute iitDelhi = Institute.builder()
                    .name("Indian Institute of Technology Delhi")
                    .address("Hauz Khas")
                    .city("New Delhi")
                    .state("Delhi") 
                    .country("India")
                    .pincode("110016")
                    .phoneNumber("+91-11-26591111")
                    .email("info@iitd.ac.in")
                    .website("https://www.iitd.ac.in")
                    .establishedYear("1961")
                    .instituteType("Institute of National Importance")
                    .affiliation("Ministry of Education, Govt. of India")
                    .departments(java.util.Arrays.asList(
                        "Computer Science and Engineering", "Electrical Engineering",
                        "Mechanical Engineering", "Civil Engineering", "Chemical Engineering",
                        "Mathematics", "Physics", "Chemistry", "Management Studies"
                    ))
                    .adminId(null)  // Will be updated after admin creation
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            instituteRepository.save(iitDelhi);
            log.info("Created institute: {}", iitDelhi.getName());
            
            log.info("Demo institutes initialized successfully!");
        }
    }
    
    private void initializeProfessors() {
        if (professorRepository.count() == 0) {
            log.info("Initializing demo professors...");
            
            // Get JNU Institute for linking professors
            Institute jnu = instituteRepository.findByName("Jawaharlal Nehru University").orElse(null);
            String jnuId = (jnu != null) ? jnu.getId() : null;
            
            // Demo Professor 1 - Computer Science
            if (!professorRepository.existsByEmail("dr.rajesh.cs@jnu.ac.in")) {
                Professor prof1 = Professor.builder()
                        .departmentId("CS001")
                        .name("Dr. Rajesh Kumar")
                        .email("dr.rajesh.cs@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .departmentName("Computer Science")
                        .institute("Jawaharlal Nehru University")
                        .instituteId(jnuId)
                        .designation("Professor")
                        .qualification("PhD in Computer Science")
                        .specialization("Artificial Intelligence, Machine Learning")
                        .phoneNumber("+91-9876543210")
                        .officeLocation("CS Building, Room 301")
                        .employeeId("EMP001")
                        .experience("15 years")
                        .researchInterests("AI, ML, Deep Learning, Computer Vision")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                professorRepository.save(prof1);
                log.info("Created demo professor: {}", prof1.getEmail());
            }
            
            // Demo Professor 2 - Physics
            if (!professorRepository.existsByEmail("dr.sunita.physics@jnu.ac.in")) {
                Professor prof2 = Professor.builder()
                        .departmentId("PHY001")
                        .name("Dr. Sunita Sharma")
                        .email("dr.sunita.physics@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .departmentName("Physics")
                        .institute("Jawaharlal Nehru University")
                        .instituteId(jnuId)
                        .designation("Associate Professor")
                        .qualification("PhD in Theoretical Physics")
                        .specialization("Quantum Mechanics, Particle Physics")
                        .phoneNumber("+91-9876543211")
                        .officeLocation("Physics Building, Room 205")
                        .employeeId("EMP002")
                        .experience("12 years")
                        .researchInterests("Quantum Computing, String Theory, Cosmology")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                professorRepository.save(prof2);
                log.info("Created demo professor: {}", prof2.getEmail());
            }
            
            // Demo Professor 3 - Mathematics
            if (!professorRepository.existsByEmail("dr.anil.math@jnu.ac.in")) {
                Professor prof3 = Professor.builder()
                        .departmentId("MATH001")
                        .name("Dr. Anil Verma")
                        .email("dr.anil.math@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .departmentName("Mathematics")
                        .institute("Jawaharlal Nehru University")
                        .instituteId(jnuId)
                        .designation("Professor")
                        .qualification("PhD in Pure Mathematics")
                        .specialization("Algebra, Number Theory")
                        .phoneNumber("+91-9876543212")
                        .officeLocation("Mathematics Building, Room 101")
                        .employeeId("EMP003")
                        .experience("18 years")
                        .researchInterests("Abstract Algebra, Cryptography, Discrete Mathematics")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                professorRepository.save(prof3);
                log.info("Created demo professor: {}", prof3.getEmail());
            }
            
            // Demo Professor 4 - Chemistry
            if (!professorRepository.existsByEmail("dr.meera.chem@jnu.ac.in")) {
                Professor prof4 = Professor.builder()
                        .departmentId("CHEM001")
                        .name("Dr. Meera Joshi")
                        .email("dr.meera.chem@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .departmentName("Chemistry")
                        .institute("Jawaharlal Nehru University")
                        .instituteId(jnuId)
                        .designation("Assistant Professor")
                        .qualification("PhD in Organic Chemistry")
                        .specialization("Organic Synthesis, Medicinal Chemistry")
                        .phoneNumber("+91-9876543213")
                        .officeLocation("Chemistry Building, Room 150")
                        .employeeId("EMP004")
                        .experience("8 years")
                        .researchInterests("Drug Discovery, Green Chemistry, Catalysis")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                professorRepository.save(prof4);
                log.info("Created demo professor: {}", prof4.getEmail());
            }
            
            // Demo Professor 5 - Biology
            if (!professorRepository.existsByEmail("dr.ramesh.bio@jnu.ac.in")) {
                Professor prof5 = Professor.builder()
                        .departmentId("BIO001")
                        .name("Dr. Ramesh Chandra")
                        .email("dr.ramesh.bio@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .departmentName("Biology")
                        .institute("Jawaharlal Nehru University")
                        .instituteId(jnuId)
                        .designation("Associate Professor")
                        .qualification("PhD in Molecular Biology")
                        .specialization("Genetics, Biotechnology")
                        .phoneNumber("+91-9876543214")
                        .officeLocation("Biology Building, Room 180")
                        .employeeId("EMP005")
                        .experience("10 years")
                        .researchInterests("Gene Therapy, Bioinformatics, Cell Biology")
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                professorRepository.save(prof5);
                log.info("Created demo professor: {}", prof5.getEmail());
            }
        }
    }
    
    private void initializeAdmins() {
        log.info("Initializing demo admins...");
        
        // Get institutes to link admins
        Institute jnu = instituteRepository.findByName("Jawaharlal Nehru University").orElse(null);
        Institute iitDelhi = instituteRepository.findByName("Indian Institute of Technology Delhi").orElse(null);
        
        String jnuId = (jnu != null) ? jnu.getId() : null;
        String iitId = (iitDelhi != null) ? iitDelhi.getId() : null;
        
        // Update existing admins with institute IDs if they don't have them
        updateExistingAdminsWithInstituteIds(jnuId, iitId);
        
        if (adminRepository.count() == 0) {
            
            // Demo Admin 1 - Super Admin (JNU)
            if (!adminRepository.existsByEmail("admin.super@jnu.ac.in")) {
                Admin admin1 = Admin.builder()
                        .email("admin.super@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .name("Super Admin")
                        .role("SUPER_ADMIN")
                        .instituteId(jnuId)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                adminRepository.save(admin1);
                log.info("Created demo admin: {} for institute: {}", admin1.getEmail(), jnu != null ? jnu.getName() : "Unknown");
                
                // Update JNU institute with super admin reference
                if (jnu != null) {
                    jnu.setAdminId(admin1.getId());
                    instituteRepository.save(jnu);
                }
            }
            
            // Demo Admin 2 - System Admin (JNU)
            if (!adminRepository.existsByEmail("admin.system@jnu.ac.in")) {
                Admin admin2 = Admin.builder()
                        .email("admin.system@jnu.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .name("System Administrator")
                        .role("SYSTEM_ADMIN")
                        .instituteId(jnuId)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                adminRepository.save(admin2);
                log.info("Created demo admin: {} for institute: {}", admin2.getEmail(), jnu != null ? jnu.getName() : "Unknown");
            }
            
            // Demo Admin 3 - Academic Admin (IIT Delhi)
            if (!adminRepository.existsByEmail("admin.academic@iitd.ac.in")) {
                Admin admin3 = Admin.builder()
                        .email("admin.academic@iitd.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .name("Academic Administrator")
                        .role("ACADEMIC_ADMIN")
                        .instituteId(iitId)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                adminRepository.save(admin3);
                log.info("Created demo admin: {} for institute: {}", admin3.getEmail(), iitDelhi != null ? iitDelhi.getName() : "Unknown");
                
                // Update IIT Delhi institute with admin reference
                if (iitDelhi != null) {
                    iitDelhi.setAdminId(admin3.getId());
                    instituteRepository.save(iitDelhi);
                }
            }
            
            // Demo Admin 4 - Research Admin (IIT Delhi)
            if (!adminRepository.existsByEmail("admin.research@iitd.ac.in")) {
                Admin admin4 = Admin.builder()
                        .email("admin.research@iitd.ac.in")
                        .password(passwordEncoder.encode("1234"))
                        .name("Research Administrator")
                        .role("RESEARCH_ADMIN")
                        .instituteId(iitId)
                        .isActive(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                adminRepository.save(admin4);
                log.info("Created demo admin: {} for institute: {}", admin4.getEmail(), iitDelhi != null ? iitDelhi.getName() : "Unknown");
            }
        }
        
        log.info("Demo data initialization completed!");
        log.info("=================================================");
        log.info("Demo Institutes created: {}", instituteRepository.count());
        log.info("Demo Users created: {}", userRepository.count());
        log.info("Demo Professors created: {}", professorRepository.count());
        log.info("Demo Admins created: {}", adminRepository.count());
        log.info("=================================================");
        log.info("All demo accounts use password: 1234");
        log.info("All emails use domain: @jnu.ac.in");
        log.info("Institutes: JNU, IIT Delhi with proper admin references");
        log.info("=================================================");
    }
    
    private void updateExistingAdminsWithInstituteIds(String jnuId, String iitId) {
        // Update JNU admins
        updateAdminInstituteId("admin.super@jnu.ac.in", jnuId);
        updateAdminInstituteId("admin.system@jnu.ac.in", jnuId);
        
        // Update all remaining admins (they might have old @jnu.ac.in domains)
        updateAdminInstituteId("admin.academic@jnu.ac.in", iitId);  // Original might be @jnu.ac.in
        updateAdminInstituteId("admin.research@jnu.ac.in", iitId);  // Original might be @jnu.ac.in
        
        // Also check for the new IIT Delhi emails in case they exist
        updateAdminInstituteId("admin.academic@iitd.ac.in", iitId);
        updateAdminInstituteId("admin.research@iitd.ac.in", iitId);
    }
    
    private void updateAdminInstituteId(String email, String instituteId) {
        adminRepository.findByEmail(email).ifPresent(admin -> {
            if (admin.getInstituteId() == null || admin.getInstituteId().isEmpty()) {
                admin.setInstituteId(instituteId);
                adminRepository.save(admin);
                log.info("Updated admin {} with institute ID: {}", email, instituteId);
            }
        });
    }
}