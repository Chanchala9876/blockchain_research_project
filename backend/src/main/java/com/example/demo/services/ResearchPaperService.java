package com.example.demo.services;

import com.example.demo.models.Admin;
import com.example.demo.models.Institute;
import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.AdminRepository;
import com.example.demo.repositories.InstituteRepository;
import com.example.demo.repositories.ResearchPaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResearchPaperService {
    
    private static final Logger log = LoggerFactory.getLogger(ResearchPaperService.class);
    private static final String UPLOAD_DIR = "uploads/papers/";
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private InstituteRepository instituteRepository;
    
    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;
    
    @Autowired
    private DocumentTextExtractorService documentTextExtractorService;
    
    @Autowired
    private FabricGatewayService fabricGatewayService;
    
    /**
     * Upload and process a research paper with embeddings
     */
    public ResearchPaper uploadResearchPaper(
            String title,
            String author,
            String department,
            String institution,
            String supervisor,
            String coSupervisor,
            String abstractText,
            List<String> keywords,
            MultipartFile file,
            String uploadedBy) throws IOException {
        
        log.info("Starting research paper upload for title: {} (Admin: {})", title, uploadedBy);
        
        // Validate admin and get institute information (with graceful fallback)
        String adminInstituteId = null;
        String adminInstituteName = "Default Institute";
        
        try {
            Optional<Admin> adminOpt = adminRepository.findById(uploadedBy);
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                log.info("📚 Found admin: {}", admin.getName());
                
                if (admin.getInstituteId() != null && !admin.getInstituteId().trim().isEmpty()) {
                    // Get institute details
                    Optional<Institute> instituteOpt = instituteRepository.findById(admin.getInstituteId());
                    if (instituteOpt.isPresent()) {
                        Institute adminInstitute = instituteOpt.get();
                        adminInstituteId = adminInstitute.getId();
                        adminInstituteName = adminInstitute.getName();
                        log.info("✅ Admin '{}' uploading paper for institute: {}", admin.getName(), adminInstitute.getName());
                    } else {
                        log.warn("⚠️ Admin's institute ID '{}' not found, using default", admin.getInstituteId());
                    }
                } else {
                    log.warn("⚠️ Admin '{}' not associated with any institute, using default", admin.getName());
                }
            } else {
                log.warn("⚠️ Admin with ID '{}' not found, proceeding with default institute", uploadedBy);
            }
        } catch (Exception e) {
            log.error("❌ Error during institute validation: {}, proceeding with default", e.getMessage());
        }
        
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        String filename = file.getOriginalFilename();
        if (!documentTextExtractorService.isSupportedFileType(filename)) {
            throw new IllegalArgumentException("Unsupported file format. " + 
                documentTextExtractorService.getSupportedFileTypesDescription());
        }
        
        // Generate file hash
        String fileHash = generateFileHash(file.getBytes());
        
        // Extract text for similarity checking
        String documentText = documentTextExtractorService.extractTextFromDocument(file);
        
        // Check for duplicate files (exact same file) - more lenient for admin uploads
        Optional<ResearchPaper> exactFileDuplicate = researchPaperRepository.findByFileHash(fileHash);
        if (exactFileDuplicate.isPresent()) {
            log.warn("⚠️ DUPLICATE FILE WARNING: File hash matches existing paper '{}' by {}", 
                    exactFileDuplicate.get().getTitle(), exactFileDuplicate.get().getAuthor());
            // For now, we'll allow admin uploads even if file exists (admin might be re-uploading legitimately)
            // In future, we can show this as a warning in UI instead of blocking
        }
        
        // Check for content similarity (same content, different file) - warning only
        Optional<ResearchPaper> contentDuplicate = checkForContentSimilarity(documentText, title);
        if (contentDuplicate.isPresent()) {
            log.warn("⚠️ SIMILAR CONTENT WARNING: Content appears similar to existing thesis '{}' by {}", 
                    contentDuplicate.get().getTitle(), contentDuplicate.get().getAuthor());
            // For now, we'll allow admin uploads with similar content (might be legitimate)
            // In future, we can show similarity report in UI
        }
        
        // Save file to disk
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String filePath = saveFile(file, fileName);
        
        // Generate embeddings
        List<Double> titleEmbedding = null;
        List<Double> documentEmbedding = null;
        String embeddingModel = null;
        LocalDateTime embeddingGeneratedAt = null;
        
        if (ollamaEmbeddingService.isOllamaAvailable()) {
            try {
                log.info("Generating embeddings with Ollama...");
                titleEmbedding = ollamaEmbeddingService.generateTitleEmbedding(title);
                documentEmbedding = ollamaEmbeddingService.generateDocumentEmbedding(documentText);
                embeddingModel = "nomic-embed-text";
                embeddingGeneratedAt = LocalDateTime.now();
                log.info("Successfully generated embeddings");
            } catch (Exception e) {
                log.warn("Failed to generate embeddings, continuing without them: {}", e.getMessage());
            }
        } else {
            log.warn("Ollama service not available, skipping embedding generation");
        }
        
        // REMOVED: Direct blockchain submission - now handled through multi-admin approval workflow
        // Blockchain submission will only happen after all admins approve via PendingThesisService
        String blockchainTransactionId = null;
        
        // Create ResearchPaper entity with auto-assigned institute
        ResearchPaper researchPaper = ResearchPaper.builder()
                .title(title)
                .author(author)
                .department(department)
                .institution(adminInstituteName) // Use admin's institute name (or default)
                .instituteId(adminInstituteId) // Auto-assign admin's institute ID (or null for default)
                .supervisor(supervisor)
                .coSupervisor(coSupervisor)
                .submissionDate(LocalDateTime.now())
                .uploadedDate(LocalDateTime.now())
                .fileHash(fileHash)
                .fileName(fileName)
                .fileSize(file.getSize())
                .filePath(filePath)
                .abstractText(abstractText)
                .keywords(keywords)
                .status("VERIFIED") // Admin uploaded, so it's already verified
                .uploadedBy(uploadedBy)
                .blockchainTxId(blockchainTransactionId) // Store blockchain transaction ID
                .blockchainHash(fileHash) // Use file hash as blockchain reference
                .documentEmbedding(documentEmbedding)
                .titleEmbedding(titleEmbedding)
                .embeddingModel(embeddingModel)
                .embeddingGeneratedAt(embeddingGeneratedAt)
                .verificationDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Save to database
        ResearchPaper savedPaper = researchPaperRepository.save(researchPaper);
        log.info("✅ Successfully saved research paper with ID: {} for institute: {} ({})", 
                savedPaper.getId(), adminInstituteName, adminInstituteId);
        
        return savedPaper;
    }
    
    /**
     * Get all research papers with pagination
     */
    public Page<ResearchPaper> getAllResearchPapers(Pageable pageable) {
        return researchPaperRepository.findAll(pageable);
    }
    
    /**
     * Get research papers by status
     */
    public Page<ResearchPaper> getResearchPapersByStatus(String status, Pageable pageable) {
        return researchPaperRepository.findByStatus(status, pageable);
    }
    
    /**
     * Find research paper by ID
     */
    public Optional<ResearchPaper> getResearchPaperById(String id) {
        return researchPaperRepository.findById(id);
    }
    
    /**
     * Search research papers by title or author
     */
    public List<ResearchPaper> searchResearchPapers(String query) {
        return researchPaperRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }
    
    /**
     * Update research paper status
     */
    public ResearchPaper updateStatus(String id, String status) {
        ResearchPaper paper = researchPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Research paper not found"));
        
        paper.setStatus(status);
        paper.setUpdatedAt(LocalDateTime.now());
        
        if ("VERIFIED".equals(status)) {
            paper.setVerificationDate(LocalDateTime.now());
        }
        
        return researchPaperRepository.save(paper);
    }
    
    /**
     * Verify paper exists on blockchain
     */
    public boolean verifyPaperOnBlockchain(String id) {
        ResearchPaper paper = researchPaperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Research paper not found"));
        
        if (paper.getBlockchainHash() == null) {
            return false;
        }
        
        try {
            return fabricGatewayService.verifyPaperRecord(paper.getBlockchainHash());
        } catch (Exception e) {
            log.error("Error verifying paper on blockchain: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate unique file name
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + extension;
    }
    
    /**
     * Get file extension
     */
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    
    /**
     * Save file to disk
     */
    private String saveFile(MultipartFile file, String fileName) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filePath.toString();
    }
    
    /**
     * Generate SHA-256 hash of file content
     */
    private String generateFileHash(byte[] fileContent) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileContent);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate file hash", e);
        }
    }
    
    /**
     * Check for content similarity with existing papers (for admin upload duplicate prevention)
     * This method checks across ALL institutes to prevent uploading duplicate content
     */
    private Optional<ResearchPaper> checkForContentSimilarity(String documentText, String title) {
        try {
            List<ResearchPaper> allPapers = researchPaperRepository.findAll();
            
            for (ResearchPaper paper : allPapers) {
                // Skip papers without text content
                if (paper.getAbstractText() == null || paper.getAbstractText().trim().isEmpty()) {
                    continue;
                }
                
                // Check title similarity first (exact or very similar titles)
                if (isTitleVerySimilar(title, paper.getTitle())) {
                    log.warn("Very similar title detected: '{}' vs existing '{}'", title, paper.getTitle());
                    return Optional.of(paper);
                }
                
                // Check content length similarity (identical content should have very similar lengths)
                double lengthRatio = (double) Math.min(documentText.length(), paper.getAbstractText().length()) 
                                   / Math.max(documentText.length(), paper.getAbstractText().length());
                
                if (lengthRatio > 0.95) { // Very similar lengths (95%+)
                    // Compare first 1000 characters for content similarity
                    int compareLength = Math.min(1000, Math.min(documentText.length(), paper.getAbstractText().length()));
                    String newContent = normalizeTextForComparison(documentText.substring(0, compareLength));
                    String existingContent = normalizeTextForComparison(paper.getAbstractText().substring(0, compareLength));
                    
                    double contentSimilarity = calculateTextSimilarity(newContent, existingContent);
                    
                    if (contentSimilarity > 90.0) { // Very high content similarity
                        log.warn("High content similarity detected: {}% with existing paper '{}'", 
                                contentSimilarity, paper.getTitle());
                        return Optional.of(paper);
                    }
                }
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error checking content similarity: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Check if two titles are very similar (accounting for minor variations)
     */
    private boolean isTitleVerySimilar(String title1, String title2) {
        if (title1 == null || title2 == null) return false;
        
        String normalized1 = normalizeTextForComparison(title1);
        String normalized2 = normalizeTextForComparison(title2);
        
        // Exact match after normalization
        if (normalized1.equals(normalized2)) {
            return true;
        }
        
        // High similarity (90%+)
        double similarity = calculateTextSimilarity(normalized1, normalized2);
        return similarity > 90.0;
    }
    
    /**
     * Normalize text for comparison (remove special chars, extra spaces, convert to lowercase)
     */
    private String normalizeTextForComparison(String text) {
        if (text == null) return "";
        return text.replaceAll("\\s+", " ")
                  .replaceAll("[^\\p{L}\\p{N}\\s]", "")
                  .toLowerCase()
                  .trim();
    }
    
    /**
     * Calculate text similarity percentage using Levenshtein distance
     */
    private double calculateTextSimilarity(String text1, String text2) {
        if (text1.equals(text2)) return 100.0;
        
        int maxLen = Math.max(text1.length(), text2.length());
        if (maxLen == 0) return 0.0;
        
        int editDistance = calculateLevenshteinDistance(text1, text2);
        return ((double)(maxLen - editDistance) / maxLen) * 100.0;
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Get research papers by institute ID for institute-specific similarity checking
     */
    public List<ResearchPaper> getPapersByInstituteId(String instituteId) {
        return researchPaperRepository.findAll()
                .stream()
                .filter(paper -> instituteId.equals(paper.getInstituteId()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get all research papers (for cross-institute similarity checking)
     */
    public List<ResearchPaper> getAllPapersWithEmbeddings() {
        return researchPaperRepository.findPapersWithEmbeddings();
    }
    
    /**
     * Update paper viewability status (admin function)
     */
    public boolean updatePaperViewability(String paperId, boolean viewable) {
        log.info("Updating paper {} viewability to: {}", paperId, viewable);
        
        try {
            Optional<ResearchPaper> paperOpt = researchPaperRepository.findById(paperId);
            
            if (paperOpt.isPresent()) {
                ResearchPaper paper = paperOpt.get();
                paper.setViewable(viewable);
                researchPaperRepository.save(paper);
                
                log.info("Successfully updated paper {} viewability to: {}", paperId, viewable);
                return true;
            } else {
                log.warn("Paper with ID {} not found for viewability update", paperId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error updating paper {} viewability", paperId, e);
            return false;
        }
    }
}