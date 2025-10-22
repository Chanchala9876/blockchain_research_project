package com.example.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.ProfessorRepository;
import com.example.demo.repositories.AdminRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, 
                                ProfessorRepository professorRepository,
                                AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to find user in User repository first
        return userRepository.findByEmail(username)
                .map(user -> (UserDetails) user)
                .orElse(
                    // If not found in User, try Professor repository
                    professorRepository.findByEmail(username)
                        .map(professor -> (UserDetails) professor)
                        .orElse(
                            // If not found in Professor, try Admin repository
                            adminRepository.findByEmail(username)
                                .map(admin -> (UserDetails) admin)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                    "User Not Found with email: " + username))
                        )
                );
    }
}