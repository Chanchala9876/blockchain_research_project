package com.example.demo.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.PaperResponse;
import com.example.demo.dto.PaperSubmissionRequest;
import com.example.demo.models.User;
import com.example.demo.services.PaperService;
import com.example.demo.utils.FileStorageService;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/papers")
public class PaperController {
    
    private final PaperService paperService;
    private final FileStorageService fileStorageService;
    
    public PaperController(PaperService paperService, FileStorageService fileStorageService) {
        this.paperService = paperService;
        this.fileStorageService = fileStorageService;
    }
    
    @PostMapping("/submit")
    @PreAuthorize("hasRole('ROLE_RESEARCHER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<PaperResponse> submitPaper(
            @RequestParam("title") String title,
            @RequestParam("abstract") String abstract_,
            @RequestParam("documentFile") MultipartFile documentFile) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        PaperSubmissionRequest request = new PaperSubmissionRequest();
        request.setTitle(title);
        request.setAbstract_(abstract_);
        
        PaperResponse response = paperService.submitPaper(user.getId(), request, documentFile);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PaperResponse>> getAllPapers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<PaperResponse> papers = paperService.getAllPapers(status, pageable);
        
        return ResponseEntity.ok(papers);
    }
    
    @GetMapping("/my-papers")
    @PreAuthorize("hasRole('ROLE_RESEARCHER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<PaperResponse>> getUserPapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<PaperResponse> papers = paperService.getUserPapers(user.getId(), pageable);
        
        return ResponseEntity.ok(papers);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaperResponse> getPaperById(@PathVariable String id) {
        PaperResponse paper = paperService.getPaperById(id);
        return ResponseEntity.ok(paper);
    }
    
    @GetMapping("/download/{fileName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadPaper(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/verify/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PaperResponse> verifyPaper(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = (User) auth.getPrincipal();
        
        PaperResponse verifiedPaper = paperService.verifyPaper(id, admin.getId());
        
        return ResponseEntity.ok(verifiedPaper);
    }
}