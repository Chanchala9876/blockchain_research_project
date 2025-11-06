package com.example.demo.services;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.BlockchainRecordResponse;
import com.example.demo.dto.PaperResponse;
import com.example.demo.dto.PaperSubmissionRequest;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.BlockchainRecord;
import com.example.demo.models.BlockchainRecord.PaperData;
import com.example.demo.models.FabricPaperRecord;
import com.example.demo.models.Paper;
import com.example.demo.models.User;
import com.example.demo.repositories.BlockchainRecordRepository;
import com.example.demo.repositories.PaperRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.utils.FileStorageService;

import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaperService {

    private static final Logger log = LoggerFactory.getLogger(PaperService.class);
    
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final BlockchainRecordRepository blockchainRepository;
    private final FileStorageService fileStorageService;
    private final FabricGatewayService fabricGatewayService;
    
    @Autowired
    public PaperService(PaperRepository paperRepository, UserRepository userRepository,
                      BlockchainRecordRepository blockchainRepository, FileStorageService fileStorageService,
                      FabricGatewayService fabricGatewayService) {
        this.paperRepository = paperRepository;
        this.userRepository = userRepository;
        this.blockchainRepository = blockchainRepository;
        this.fileStorageService = fileStorageService;
        this.fabricGatewayService = fabricGatewayService;
    }

    public PaperResponse submitPaper(String userId, PaperSubmissionRequest request, MultipartFile pdfFile) {
        try {
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Save PDF file
            String pdfUrl = fileStorageService.storePaperFile(pdfFile);
            
            // Generate paper hash
            String paperHash = generatePaperHash(request.getTitle(), author.getEmail(), pdfFile.getBytes());
            
            // Create paper record
            Paper paper = Paper.builder()
                    .title(request.getTitle())
                    .author(author)
                    .authorId(author.getId())
                    .abstract_(request.getAbstract_())
                    .pdfUrl(pdfUrl)
                    .hash(paperHash)
                    .status("pending")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            
            Paper savedPaper = paperRepository.save(paper);
            
            // Return response
            return mapPaperToResponse(savedPaper);
        } catch (Exception e) {
            log.error("Error submitting paper: {}", e.getMessage());
            throw new RuntimeException("Failed to submit paper", e);
        }
    }
    
    public PaperResponse getPaperById(String paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        
        return mapPaperToResponse(paper);
    }
    
    public Page<PaperResponse> getAllPapers(String status, Pageable pageable) {
        Page<Paper> papers;
        
        if (status != null && !status.isEmpty()) {
            papers = paperRepository.findByStatus(status, pageable);
        } else {
            papers = paperRepository.findAll(pageable);
        }
        
        return papers.map(this::mapPaperToResponse);
    }
    
    public Page<PaperResponse> getUserPapers(String userId, Pageable pageable) {
        Page<Paper> papers = paperRepository.findByAuthorId(userId, pageable);
        return papers.map(this::mapPaperToResponse);
    }
    
    public PaperResponse verifyPaper(String paperId, String adminId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        
        if (!"pending".equals(paper.getStatus())) {
            throw new IllegalStateException("Paper is already " + paper.getStatus());
        }
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        if (!"admin".equals(admin.getRole())) {
            throw new IllegalStateException("User is not an admin");
        }
        
        // Get the author
        User author = userRepository.findById(paper.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));
        
        try {
            // Create record on Hyperledger Fabric blockchain
            String transactionId = fabricGatewayService.createPaperRecord(
                    author.getId(), // Using authorId as studentId
                    paper.getHash(),
                    author.getName(),
                    author.getId(),
                    paper.getCreatedAt().toString()
            );
            
            log.info("Paper record created on Hyperledger Fabric with transaction ID: {}", transactionId);
            
            // Create local blockchain record for compatibility
            Optional<BlockchainRecord> lastBlockOpt = blockchainRepository.findTopByOrderByTimestampDesc();
            String previousHash = lastBlockOpt.isPresent() ? lastBlockOpt.get().getPaperHash() : "0";
            
            PaperData paperData = new PaperData();
            paperData.setTitle(paper.getTitle());
            paperData.setAuthorName(author.getName());
            paperData.setInstitute(author.getInstitute());
            paperData.setTimestamp(LocalDateTime.now());
            
            BlockchainRecord blockchainRecord = BlockchainRecord.builder()
                    .paperHash(paper.getHash())
                    .previousHash(previousHash)
                    .timestamp(LocalDateTime.now())
                    .author(author)
                    .authorId(author.getId())
                    .paperData(paperData)
                    .build();
            
            blockchainRepository.save(blockchainRecord);
            
        } catch (Exception e) {
            log.error("Failed to create record on Hyperledger Fabric: {}", e.getMessage());
            throw new RuntimeException("Failed to verify paper on blockchain", e);
        }
        
        // Update paper status
        paper.setStatus("verified");
        paper.setUpdatedAt(LocalDateTime.now());
        
        Paper updatedPaper = paperRepository.save(paper);
        
        return mapPaperToResponse(updatedPaper);
    }
    
    public BlockchainRecordResponse verifyPaperOnBlockchain(String paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new ResourceNotFoundException("Paper not found"));
        
        try {
            // Verify paper record exists on Hyperledger Fabric
            boolean exists = fabricGatewayService.verifyPaperRecord(paper.getHash());
            
            if (!exists) {
                throw new ResourceNotFoundException("Paper record not found on Hyperledger Fabric blockchain");
            }
            
            // Get the paper record from Hyperledger Fabric
            FabricPaperRecord fabricRecord = fabricGatewayService.getPaperRecord(paper.getHash());
            
            // Convert to BlockchainRecordResponse for compatibility
            return BlockchainRecordResponse.builder()
                    .id(paper.getId())
                    .paperHash(fabricRecord.getPaperHash())
                    .previousHash("N/A") // Fabric doesn't use previous hash concept
                    .timestamp(LocalDateTime.parse(fabricRecord.getTimestamp()))
                    .authorId(fabricRecord.getAuthorId())
                    .authorName(fabricRecord.getAuthor())
                    .paperTitle(paper.getTitle())
                    .institute("N/A") // Will be available if needed
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to verify paper on Hyperledger Fabric: {}", e.getMessage());
            
            // Fallback to local blockchain record
            BlockchainRecord record = blockchainRepository.findByPaperHash(paper.getHash())
                    .orElseThrow(() -> new ResourceNotFoundException("Blockchain record not found for this paper"));
            
            return mapBlockchainRecordToResponse(record);
        }
    }
    
    public Page<BlockchainRecordResponse> getAllBlockchainRecords(Pageable pageable) {
        Page<BlockchainRecord> records = blockchainRepository.findAll(pageable);
        return records.map(this::mapBlockchainRecordToResponse);
    }
    
    private String generatePaperHash(String title, String authorEmail, byte[] fileContent) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            String combinedData = title + authorEmail + LocalDateTime.now().toString();
            byte[] combinedBytes = combinedData.getBytes();
            
            // Combine paper content with metadata for hash
            byte[] allData = new byte[combinedBytes.length + fileContent.length];
            System.arraycopy(combinedBytes, 0, allData, 0, combinedBytes.length);
            System.arraycopy(fileContent, 0, allData, combinedBytes.length, fileContent.length);
            
            byte[] digest = md.digest(allData);
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (Exception e) {
            log.error("Error generating paper hash: {}", e.getMessage());
            throw new RuntimeException("Failed to generate paper hash", e);
        }
    }
    
    private PaperResponse mapPaperToResponse(Paper paper) {
        User author = null;
        if (paper.getAuthor() != null) {
            author = paper.getAuthor();
        } else if (paper.getAuthorId() != null) {
            author = userRepository.findById(paper.getAuthorId()).orElse(null);
        }
        
        return PaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .authorName(author != null ? author.getName() : "Unknown")
                .authorId(paper.getAuthorId())
                .abstract_(paper.getAbstract_())
                .pdfUrl(paper.getPdfUrl())
                .status(paper.getStatus())
                .hash(paper.getHash())
                .createdAt(paper.getCreatedAt())
                .updatedAt(paper.getUpdatedAt())
                .build();
    }
    
    private BlockchainRecordResponse mapBlockchainRecordToResponse(BlockchainRecord record) {
        return BlockchainRecordResponse.builder()
                .id(record.getId())
                .paperHash(record.getPaperHash())
                .previousHash(record.getPreviousHash())
                .timestamp(record.getTimestamp())
                .authorId(record.getAuthorId())
                .authorName(record.getPaperData().getAuthorName())
                .paperTitle(record.getPaperData().getTitle())
                .institute(record.getPaperData().getInstitute())
                .build();
    }
}