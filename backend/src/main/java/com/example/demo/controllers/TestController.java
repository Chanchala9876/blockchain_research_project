package com.example.demo.controllers;

import com.example.demo.models.ResearchPaper;
import com.example.demo.services.FabricGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private FabricGatewayService fabricGatewayService;

    @GetMapping("/mongodb")
    public ResponseEntity<Map<String, Object>> testMongoDB() {
        Map<String, Object> testDocument = new HashMap<>();
        testDocument.put("name", "Test Document");
        testDocument.put("timestamp", LocalDateTime.now().toString());
        testDocument.put("message", "This is a test document to initialize the database");

        // Insert the document into a test collection
        Map<String, Object> savedDocument = mongoTemplate.save(testDocument, "test_collection");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "MongoDB connection successful. Database has been initialized.");
        response.put("document", savedDocument);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/blockchain-status")
    public ResponseEntity<Map<String, Object>> checkBlockchainStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get recent research papers from MongoDB
            Query query = new Query();
            query.limit(5);
            query.with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
            
            List<ResearchPaper> recentPapers = mongoTemplate.find(query, ResearchPaper.class);
            
            response.put("success", true);
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("totalPapers", recentPapers.size());
            
            // Create a simplified view of papers with blockchain info
            List<Map<String, Object>> paperInfo = new java.util.ArrayList<>();
            
            for (ResearchPaper paper : recentPapers) {
                Map<String, Object> info = new HashMap<>();
                info.put("id", paper.getId());
                info.put("title", paper.getTitle());
                info.put("author", paper.getAuthor());
                info.put("createdAt", paper.getCreatedAt());
                info.put("fileHash", paper.getFileHash());
                info.put("blockchainTxId", paper.getBlockchainTxId());
                info.put("blockchainHash", paper.getBlockchainHash());
                
                // Check if this paper has blockchain data
                boolean hasBlockchainData = paper.getBlockchainTxId() != null && !paper.getBlockchainTxId().isEmpty();
                info.put("hasBlockchainData", hasBlockchainData);
                
                if (hasBlockchainData) {
                    try {
                        // Try to verify the paper record on blockchain
                        boolean verified = fabricGatewayService.verifyPaperRecord(paper.getFileHash());
                        info.put("blockchainVerified", verified);
                        info.put("verificationStatus", verified ? "VERIFIED_ON_BLOCKCHAIN" : "NOT_FOUND_ON_BLOCKCHAIN");
                    } catch (Exception e) {
                        info.put("blockchainVerified", false);
                        info.put("verificationStatus", "VERIFICATION_ERROR: " + e.getMessage());
                    }
                } else {
                    info.put("blockchainVerified", false);
                    info.put("verificationStatus", "NO_BLOCKCHAIN_DATA");
                }
                
                paperInfo.add(info);
            }
            
            response.put("papers", paperInfo);
            response.put("message", "Blockchain status check completed successfully");
            
            // Add network status
            response.put("networkInfo", Map.of(
                "fabricPeerRunning", "localhost:7051",
                "couchDBRunning", "localhost:5984",
                "blockchainMode", "Real Hyperledger Fabric"
            ));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Failed to check blockchain status");
        }
        
        return ResponseEntity.ok(response);
    }
}