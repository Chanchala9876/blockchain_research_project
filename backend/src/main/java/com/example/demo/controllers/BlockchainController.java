package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.BlockchainRecordResponse;
import com.example.demo.services.PaperService;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {
    
    private final PaperService paperService;
    
    public BlockchainController(PaperService paperService) {
        this.paperService = paperService;
    }
    
    @GetMapping("/verify/{paperId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlockchainRecordResponse> verifyPaperOnBlockchain(@PathVariable String paperId) {
        BlockchainRecordResponse record = paperService.verifyPaperOnBlockchain(paperId);
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/records")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<BlockchainRecordResponse>> getAllBlockchainRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(page, limit, Sort.by("timestamp").descending());
        Page<BlockchainRecordResponse> records = paperService.getAllBlockchainRecords(pageable);
        
        return ResponseEntity.ok(records);
    }
}