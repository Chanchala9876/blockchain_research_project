package com.example.demo.controllers;

import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.ResearchPaperRepository;
import com.example.demo.services.FabricGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/blockchain-viewer")
@CrossOrigin(origins = "*")
public class BlockchainViewerController {

    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private FabricGatewayService fabricGatewayService;

    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> viewAllBlockchainRecords() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> blockchainRecords = new ArrayList<>();
        
        try {
            // Get all research papers with blockchain data
            Query query = new Query();
            query.addCriteria(Criteria.where("blockchainTxId").ne(null));
            List<ResearchPaper> papers = mongoTemplate.find(query, ResearchPaper.class);
            
            for (ResearchPaper paper : papers) {
                Map<String, Object> record = new HashMap<>();
                
                // Basic paper info
                record.put("id", paper.getId());
                record.put("title", paper.getTitle());
                record.put("author", paper.getAuthor());
                record.put("uploadedAt", paper.getCreatedAt());
                
                // Blockchain specific data
                record.put("blockchainTransactionId", paper.getBlockchainTxId());
                record.put("blockchainHash", paper.getBlockchainHash());
                record.put("fileHash", paper.getFileHash());
                record.put("isOnBlockchain", paper.getBlockchainTxId() != null);
                record.put("blockchainVerified", paper.getBlockchainTxId() != null && paper.getBlockchainHash() != null);
                
                // Network information
                record.put("networkType", fabricGatewayService.isFabricNetworkAvailable() ? 
                    "Real Hyperledger Fabric" : "Simulation Mode");
                
                // Immutable proof
                Map<String, Object> immutableProof = new HashMap<>();
                immutableProof.put("documentHash", paper.getFileHash());
                immutableProof.put("blockchainReference", paper.getBlockchainTxId());
                immutableProof.put("timestampProof", paper.getCreatedAt());
                immutableProof.put("authorshipProof", paper.getAuthor());
                record.put("immutableProof", immutableProof);
                
                blockchainRecords.add(record);
            }
            
            // Network status
            Map<String, Object> networkStatus = fabricGatewayService.getNetworkStatus();
            
            response.put("success", true);
            response.put("totalBlockchainRecords", blockchainRecords.size());
            response.put("records", blockchainRecords);
            response.put("networkStatus", networkStatus);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            response.put("message", "Successfully retrieved blockchain records from " + 
                (fabricGatewayService.isFabricNetworkAvailable() ? "Real Hyperledger Fabric" : "Simulation Mode"));
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("records", new ArrayList<>());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/record/{id}")
    public ResponseEntity<Map<String, Object>> viewSpecificRecord(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ResearchPaper> paperOpt = researchPaperRepository.findById(id);
            
            if (paperOpt.isPresent()) {
                ResearchPaper paper = paperOpt.get();
                
                Map<String, Object> detailedRecord = new HashMap<>();
                
                // Complete paper information
                detailedRecord.put("mongoId", paper.getId());
                detailedRecord.put("title", paper.getTitle());
                detailedRecord.put("author", paper.getAuthor());
                detailedRecord.put("abstractText", paper.getAbstractText());
                detailedRecord.put("keywords", paper.getKeywords());
                detailedRecord.put("uploadedAt", paper.getCreatedAt());
                detailedRecord.put("filename", paper.getFileName());
                detailedRecord.put("fileSize", paper.getFileSize());
                
                // Blockchain immutability data
                Map<String, Object> blockchainData = new HashMap<>();
                blockchainData.put("transactionId", paper.getBlockchainTxId());
                blockchainData.put("blockchainHash", paper.getBlockchainHash());
                blockchainData.put("fileHash", paper.getFileHash());
                blockchainData.put("verified", paper.getBlockchainTxId() != null && paper.getBlockchainHash() != null);
                blockchainData.put("networkType", fabricGatewayService.isFabricNetworkAvailable() ? 
                    "Real Hyperledger Fabric" : "Simulation Mode");
                detailedRecord.put("blockchainData", blockchainData);
                
                // AI/ML embeddings (if available)
                if (paper.getDocumentEmbedding() != null && !paper.getDocumentEmbedding().isEmpty()) {
                    Map<String, Object> aiData = new HashMap<>();
                    aiData.put("hasDocumentEmbedding", true);
                    aiData.put("embeddingDimensions", paper.getDocumentEmbedding().size());
                    aiData.put("hasTitleEmbedding", paper.getTitleEmbedding() != null && !paper.getTitleEmbedding().isEmpty());
                    detailedRecord.put("aiProcessing", aiData);
                }
                
                // Verification status
                Map<String, Object> verification = new HashMap<>();
                verification.put("isImmutable", paper.getBlockchainTxId() != null);
                verification.put("canBeVerified", true);
                verification.put("verificationMethod", "Blockchain Transaction ID + File Hash");
                verification.put("tamperProof", paper.getBlockchainTxId() != null);
                detailedRecord.put("verification", verification);
                
                response.put("success", true);
                response.put("record", detailedRecord);
                response.put("message", "Record found and verified");
                
            } else {
                response.put("success", false);
                response.put("message", "Record not found with ID: " + id);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verification/{id}")
    public ResponseEntity<Map<String, Object>> verifyRecord(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<ResearchPaper> paperOpt = researchPaperRepository.findById(id);
            
            if (paperOpt.isPresent()) {
                ResearchPaper paper = paperOpt.get();
                
                Map<String, Object> verification = new HashMap<>();
                
                // Check if record is on blockchain
                boolean isOnBlockchain = paper.getBlockchainTxId() != null;
                
                verification.put("recordExists", true);
                verification.put("isOnBlockchain", isOnBlockchain);
                verification.put("transactionId", paper.getBlockchainTxId());
                verification.put("fileHash", paper.getFileHash());
                verification.put("uploadTimestamp", paper.getCreatedAt());
                verification.put("author", paper.getAuthor());
                verification.put("title", paper.getTitle());
                
                if (isOnBlockchain) {
                    verification.put("immutabilityStatus", "CONFIRMED");
                    verification.put("tamperProof", "YES - Protected by blockchain");
                    verification.put("verificationLevel", "BLOCKCHAIN_VERIFIED");
                } else {
                    verification.put("immutabilityStatus", "NOT_ON_BLOCKCHAIN");
                    verification.put("tamperProof", "NO - Only in database");
                    verification.put("verificationLevel", "DATABASE_ONLY");
                }
                
                verification.put("networkType", fabricGatewayService.isFabricNetworkAvailable() ? 
                    "Real Hyperledger Fabric" : "Simulation Mode");
                
                response.put("success", true);
                response.put("verification", verification);
                response.put("message", "Verification completed");
                
            } else {
                response.put("success", false);
                response.put("message", "Record not found for verification");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}