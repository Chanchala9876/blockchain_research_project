package com.example.demo.services;

import com.example.demo.dto.ThesisVerificationRequest;
import com.example.demo.dto.ThesisVerificationResponse;
import com.example.demo.models.ResearchPaper;
import com.example.demo.repositories.ResearchPaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ThesisVerificationService {
    
    private static final Logger log = LoggerFactory.getLogger(ThesisVerificationService.class);
    
    @Autowired
    private ResearchPaperRepository researchPaperRepository;
    
    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;
    
    @Autowired
    private DocumentTextExtractorService documentTextExtractorService;
    
    @Autowired
    private SimilarityService similarityService;
    
    @Autowired
    private FabricGatewayService fabricGatewayService;
    
    @Autowired
    private AIDetectionService aiDetectionService;
    
    /**
     * Verify thesis against existing papers in database (backward compatibility)
     */
    public ThesisVerificationResponse verifyThesis(ThesisVerificationRequest request, MultipartFile file) {
        return verifyThesis(request, file, "STUDENT"); // Default to student view
    }
    
    /**
     * Verify thesis against existing papers in database with role-based reporting
     */
    public ThesisVerificationResponse verifyThesis(ThesisVerificationRequest request, MultipartFile file, String userType) {
        log.info("Starting thesis verification for: {} by {} (User Type: {})", request.getTitle(), request.getAuthor(), userType);
        
        try {
            log.info("Step 1: Validating file...");
            // Step 1: Validate file
            validateFile(file);
            log.info("Step 1: File validation passed");
            
            log.info("Step 2: Generating file hash...");
            // Step 2: Generate file hash
            String fileHash = generateFileHash(file.getBytes());
            request.setFileHash(fileHash);
            request.setFileName(file.getOriginalFilename());
            request.setFileSize(file.getSize());
            log.info("Step 2: File hash generated: {}", fileHash);
            
            log.info("Step 3: Checking for exact file match...");
            // Step 3: Check for exact file match (for reference, but don't skip AI analysis)
            Optional<ResearchPaper> exactFileMatch = researchPaperRepository.findByFileHash(fileHash);
            if (exactFileMatch.isPresent()) {
                log.info("Found exact file hash match: {}", exactFileMatch.get().getTitle());
                // Still proceed with AI analysis to ensure comprehensive verification
            }
            log.info("Step 3: Exact match check completed");
            
            log.info("Step 4: Extracting text from document...");
            // Step 4: Extract text from document (PDF or DOCX)
            String documentText = documentTextExtractorService.extractTextFromDocument(file);
            log.info("Extracted {} characters from document", documentText.length());
            
            log.info("Step 4.5: Checking for nearly identical content...");
            // Step 4.5: Check for nearly identical content (only for 95%+ similarity)
            // This is for truly identical content - small changes should go through AI analysis
            String contentHash = generateContentHash(documentText);
            Optional<ResearchPaper> identicalMatch = findNearlyIdenticalContent(documentText, contentHash);
            if (identicalMatch.isPresent()) {
                log.warn("🚨 NEARLY IDENTICAL CONTENT DETECTED: Very high similarity (95%+) found");
                return createIdenticalContentResponse(identicalMatch.get());
            }
            log.info("Step 4.5: Nearly identical content check completed");
            
            log.info("Step 5: Generating embeddings...");
            // Step 5: Generate embeddings (with fallback if Ollama unavailable)
            List<Double> titleEmbedding = ollamaEmbeddingService.generateTitleEmbedding(request.getTitle());
            List<Double> documentEmbedding = ollamaEmbeddingService.generateDocumentEmbedding(documentText);
            
            log.info("Generated embeddings - Title: {} dims, Document: {} dims", 
                titleEmbedding.size(), documentEmbedding.size());
            
            // Step 6: Compare with existing papers
            List<ResearchPaper> papersWithEmbeddings = researchPaperRepository.findPapersWithEmbeddings();
            log.info("Comparing against {} papers with embeddings", papersWithEmbeddings.size());
            
            if (papersWithEmbeddings.isEmpty()) {
                // Generate proper report even when database is empty
                // For empty database, still run AI detection
                AIDetectionService.AIDetectionResult aiDetectionResult = 
                    aiDetectionService.analyzeForAIContent(documentText, request.getTitle(), request.getAbstractText());
                
                ThesisVerificationResponse.AIAnalysis aiAnalysis = 
                    new ThesisVerificationResponse.AIAnalysis(0.0, 0.0, 0.0);
                aiAnalysis.setMatchedPapersCount(0);
                aiAnalysis.setTopMatches(new ArrayList<>());
                
                // Add AI detection results
                aiAnalysis.setAiDetectionScore(aiDetectionResult.getAiProbabilityPercentage());
                aiAnalysis.setAiDetectionConclusion(aiDetectionResult.getConclusion());
                aiAnalysis.setAiDetectionIndicators(aiDetectionResult.getIndicators());
                
                ThesisVerificationResponse response = new ThesisVerificationResponse(
                    false, null, 0.0, "FIRST_SUBMISSION", 0.0, aiAnalysis);
                
                String aiInfo = String.format("AI Detection: %.1f%% probability", aiDetectionResult.getAiProbabilityPercentage());
                response.setMessage(String.format("✅ VERIFICATION COMPLETE: Database contains no prior submissions for comparison. " +
                    "Similarity Score: 0.0%% | Plagiarism Risk: 0%% | %s | " +
                    "Status: First submission - No conflicts detected.", aiInfo));
                
                return response;
            }
            
            // Step 7: Calculate similarities using AI embeddings
            log.info("🤖 Starting AI similarity analysis against {} papers...", papersWithEmbeddings.size());
            SimilarityResult bestMatch = findBestMatch(request, titleEmbedding, documentEmbedding, papersWithEmbeddings);
            
            if (bestMatch.bestMatchPaper != null) {
                log.info("🔍 AI Analysis Result: Best match '{}' with {}% similarity", 
                        bestMatch.bestMatchPaper.getTitle(), bestMatch.bestSimilarity);
            } else {
                log.info("🔍 AI Analysis Result: No significant similarities found");
            }
            
            // Step 8: Perform AI detection analysis
            log.info("🤖 Starting AI content detection analysis...");
            AIDetectionService.AIDetectionResult aiDetectionResult = 
                aiDetectionService.analyzeForAIContent(documentText, request.getTitle(), request.getAbstractText());
            
            log.info("🤖 AI Detection completed: {}% probability of AI assistance", 
                    Math.round(aiDetectionResult.getAiProbabilityPercentage()));
            
            // Step 9: Generate verification response with role-based details and AI detection
            return createVerificationResponse(request, bestMatch, papersWithEmbeddings.size(), userType, aiDetectionResult);
            
        } catch (Exception e) {
            log.error("Error during thesis verification: {}", e.getMessage(), e);
            return new ThesisVerificationResponse(false, 
                "Verification failed due to an error: " + e.getMessage());
        }
    }
    
    /**
     * Search for papers in database using various criteria
     */
    public ThesisVerificationResponse searchPapers(String hash, String title, String author, String blockchainTxId) {
        log.info("Searching papers with criteria - hash: {}, title: {}, author: {}, txId: {}", 
                hash, title, author, blockchainTxId);
        
        try {
            ResearchPaper foundPaper = null;
            
            // Search by hash first (most specific)
            if (hash != null && !hash.trim().isEmpty()) {
                Optional<ResearchPaper> paperByHash = researchPaperRepository.findByFileHash(hash.trim());
                if (paperByHash.isPresent()) {
                    foundPaper = paperByHash.get();
                }
            }
            
            // Search by blockchain transaction ID
            if (foundPaper == null && blockchainTxId != null && !blockchainTxId.trim().isEmpty()) {
                List<ResearchPaper> allPapers = researchPaperRepository.findAll();
                foundPaper = allPapers.stream()
                    .filter(p -> blockchainTxId.trim().equals(p.getBlockchainTxId()))
                    .findFirst()
                    .orElse(null);
            }
            
            // Search by title and author (partial match)
            if (foundPaper == null) {
                List<ResearchPaper> candidatePapers = new ArrayList<>();
                
                if (title != null && !title.trim().isEmpty()) {
                    candidatePapers.addAll(researchPaperRepository.findByTitleContainingIgnoreCase(title.trim()));
                }
                
                if (author != null && !author.trim().isEmpty()) {
                    candidatePapers.addAll(researchPaperRepository.findByAuthorContainingIgnoreCase(author.trim()));
                }
                
                // Remove duplicates and find best match
                candidatePapers = candidatePapers.stream().distinct().collect(Collectors.toList());
                
                if (!candidatePapers.isEmpty()) {
                    // For search, return the first match or best match based on criteria
                    foundPaper = candidatePapers.get(0);
                }
            }
            
            if (foundPaper != null) {
                return createSearchMatchResponse(foundPaper);
            } else {
                return new ThesisVerificationResponse(false, 
                    "No papers found matching the search criteria.");
            }
            
        } catch (Exception e) {
            log.error("Error during paper search: {}", e.getMessage(), e);
            return new ThesisVerificationResponse(false, 
                "Search failed due to an error: " + e.getMessage());
        }
    }
    
    /**
     * Find the best matching paper using AI similarity with enhanced title checking
     */
    private SimilarityResult findBestMatch(ThesisVerificationRequest request, 
                                         List<Double> titleEmbedding, 
                                         List<Double> documentEmbedding,
                                         List<ResearchPaper> papersWithEmbeddings) {
        
        double bestSimilarity = 0.0;
        ResearchPaper bestMatch = null;
        double bestTitleSimilarity = 0.0;
        double bestContentSimilarity = 0.0;
        double bestTitleStringSimilarity = 0.0;
        List<ThesisVerificationResponse.SimilarPaper> topMatches = new ArrayList<>();
        boolean exactTitleMatch = false;
        
        for (ResearchPaper paper : papersWithEmbeddings) {
            try {
                double titleSim = 0.0;
                double contentSim = 0.0;
                
                // Check for exact title match first (string comparison)
                boolean isExactTitle = similarityService.isExactTitleMatch(request.getTitle(), paper.getTitle());
                double titleStringSimiarity = similarityService.calculateTitleStringSimilarity(request.getTitle(), paper.getTitle());
                
                // Calculate AI-based title similarity
                if (similarityService.areVectorsValid(titleEmbedding, paper.getTitleEmbedding())) {
                    titleSim = similarityService.calculateSimilarityPercentage(titleEmbedding, paper.getTitleEmbedding());
                }
                
                // Calculate content similarity
                if (similarityService.areVectorsValid(documentEmbedding, paper.getDocumentEmbedding())) {
                    contentSim = similarityService.calculateSimilarityPercentage(documentEmbedding, paper.getDocumentEmbedding());
                }
                
                // If exact title match or very high title similarity, boost content similarity
                double adjustedContentSim = contentSim;
                if (isExactTitle || titleStringSimiarity >= 95.0) {
                    // Exact/near-exact title match significantly increases plagiarism concerns
                    adjustedContentSim = Math.max(contentSim, 95.0);
                    exactTitleMatch = true;
                    log.warn("⚠️ EXACT TITLE MATCH detected: '{}' vs '{}'", request.getTitle(), paper.getTitle());
                } else if (titleStringSimiarity >= 85.0) {
                    // Very similar titles - even with different abstracts, this is suspicious
                    adjustedContentSim = Math.max(contentSim, Math.min(95.0, contentSim + 25.0));
                    log.warn("⚠️ VERY SIMILAR TITLE detected: '{}' vs '{}'", request.getTitle(), paper.getTitle());
                } else if (titleStringSimiarity >= 75.0) {
                    // Moderately similar titles - boost content similarity
                    adjustedContentSim = Math.max(contentSim, Math.min(90.0, contentSim + 15.0));
                    log.warn("⚠️ SIMILAR TITLE detected: '{}' vs '{}'", request.getTitle(), paper.getTitle());
                }
                
                // Enhanced combined similarity calculation
                // If title is very similar, give it more weight and be more aggressive
                double titleWeight = titleStringSimiarity >= 85.0 ? 0.6 : 0.3;  // Increased weight for similar titles
                double contentWeight = titleStringSimiarity >= 85.0 ? 0.4 : 0.7;
                
                double combinedSimilarity = similarityService.calculateCombinedSimilarity(
                    titleEmbedding, paper.getTitleEmbedding(),
                    documentEmbedding, paper.getDocumentEmbedding(),
                    titleWeight, contentWeight);
                
                // Use adjusted content similarity for final calculation when titles are similar
                if (titleStringSimiarity >= 75.0) {
                    combinedSimilarity = (titleStringSimiarity * titleWeight + adjustedContentSim * contentWeight);
                }
                
                // Additional boost for admin duplicate detection - be more strict
                if (titleStringSimiarity >= 80.0 && contentSim >= 50.0) {
                    // Even moderate content similarity with high title similarity is concerning for admin uploads
                    combinedSimilarity = Math.max(combinedSimilarity, 85.0);
                    log.warn("⚠️ ADMIN DUPLICATE ALERT: Title {}% + Content {}% = Combined {}%", 
                            titleStringSimiarity, contentSim, combinedSimilarity);
                }
                
                // Add to top matches if similarity is significant (lowered threshold for admin detection)
                if (combinedSimilarity > 25.0 || titleStringSimiarity >= 75.0) {
                    ThesisVerificationResponse.SimilarPaper similarPaper = 
                        new ThesisVerificationResponse.SimilarPaper(
                            paper.getId(), paper.getTitle(), paper.getAuthor(), combinedSimilarity);
                    similarPaper.setDepartment(paper.getDepartment());
                    similarPaper.setSubmissionDate(paper.getSubmissionDate());
                    topMatches.add(similarPaper);
                }
                
                // Track best match
                if (combinedSimilarity > bestSimilarity) {
                    bestSimilarity = combinedSimilarity;
                    bestMatch = paper;
                    bestTitleSimilarity = titleSim;
                    bestContentSimilarity = adjustedContentSim; // Use adjusted content similarity
                    bestTitleStringSimilarity = titleStringSimiarity;
                }
                
                log.debug("Paper: {} - Title AI: {}%, Title String: {}%, Content: {}% (Adj: {}%), Combined: {}%", 
                    paper.getTitle(), titleSim, titleStringSimiarity, contentSim, adjustedContentSim, combinedSimilarity);
                
            } catch (Exception e) {
                log.warn("Error calculating similarity for paper {}: {}", paper.getId(), e.getMessage());
            }
        }
        
        // Sort top matches by similarity score
        topMatches.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        
        return new SimilarityResult(bestMatch, bestSimilarity, bestTitleSimilarity, 
                                  bestContentSimilarity, bestTitleStringSimilarity, exactTitleMatch, topMatches);
    }
    
    /**
     * Create verification response for exact file match
     */
    private ThesisVerificationResponse createExactMatchResponse(ResearchPaper exactMatch) {
        log.info("Found exact file match: {}", exactMatch.getTitle());
        
        ThesisVerificationResponse.AIAnalysis aiAnalysis = 
            new ThesisVerificationResponse.AIAnalysis(1.0, 1.0, 1.0);
        aiAnalysis.setMatchedPapersCount(1);
        
        ThesisVerificationResponse response = new ThesisVerificationResponse(
            true, exactMatch, 100.0, "EXACT_MATCH", 0.0, aiAnalysis);
        response.setMessage("Exact file match found in database");
        
        return response;
    }
    
    /**
     * Create verification response for search results
     */
    private ThesisVerificationResponse createSearchMatchResponse(ResearchPaper foundPaper) {
        log.info("Found paper in search: {}", foundPaper.getTitle());
        
        ThesisVerificationResponse.AIAnalysis aiAnalysis = 
            new ThesisVerificationResponse.AIAnalysis(0.9, 0.9, 0.9);
        aiAnalysis.setMatchedPapersCount(1);
        
        ThesisVerificationResponse response = new ThesisVerificationResponse(
            true, foundPaper, 90.0, "SEARCH_MATCH", 5.0, aiAnalysis);
        response.setMessage("Paper found in database");
        
        return response;
    }
    
    /**
     * Create verification response based on AI similarity analysis and AI detection
     */
    private ThesisVerificationResponse createVerificationResponse(ThesisVerificationRequest request,
                                                                SimilarityResult bestMatch,
                                                                int totalPapersCompared,
                                                                String userType,
                                                                AIDetectionService.AIDetectionResult aiDetectionResult) {
        
        // Check for exact title match first
        if (bestMatch.exactTitleMatch || bestMatch.bestTitleStringSimilarity >= 95.0) {
            // EXACT TITLE MATCH - High plagiarism concern
            log.warn("🚨 EXACT TITLE MATCH DETECTED: Research with this title already exists!");
            
            ThesisVerificationResponse.AIAnalysis aiAnalysis = new ThesisVerificationResponse.AIAnalysis(
                bestMatch.bestSimilarity / 100.0, 
                bestMatch.bestTitleStringSimilarity / 100.0, 
                bestMatch.bestContentSimilarity / 100.0);
            aiAnalysis.setMatchedPapersCount(totalPapersCompared);
            aiAnalysis.setTopMatches(bestMatch.topMatches.size() > 5 ? 
                bestMatch.topMatches.subList(0, 5) : bestMatch.topMatches);
            
            // Add AI detection results
            aiAnalysis.setAiDetectionScore(aiDetectionResult.getAiProbabilityPercentage());
            aiAnalysis.setAiDetectionConclusion(aiDetectionResult.getConclusion());
            aiAnalysis.setAiDetectionIndicators(aiDetectionResult.getIndicators());
            
            // For exact title match, plagiarism score should be very high (at least 90%)
            double plagiarismScore = Math.max(90.0, bestMatch.bestSimilarity);
            if (bestMatch.bestContentSimilarity >= 80.0) {
                plagiarismScore = Math.max(95.0, plagiarismScore);
            }
            
            ThesisVerificationResponse response = new ThesisVerificationResponse(
                true, bestMatch.bestMatchPaper, bestMatch.bestSimilarity, "EXACT_TITLE_MATCH", plagiarismScore, aiAnalysis);
            
            // Role-based exact title match message
            if ("PROFESSOR".equalsIgnoreCase(userType)) {
                response.setMessage(String.format("🚨 CRITICAL: Research with identical title already exists! " +
                    "Title: '%s' by %s from %s. Plagiarism score: %.1f%%", 
                    bestMatch.bestMatchPaper.getTitle(), bestMatch.bestMatchPaper.getAuthor(), 
                    bestMatch.bestMatchPaper.getInstitution(), plagiarismScore));
            } else {
                response.setMessage(String.format("🚨 CRITICAL: Research with identical title already exists! " +
                    "Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | Status: Duplicate title detected", 
                    bestMatch.bestSimilarity, plagiarismScore));
            }
                
            return response;
        }
        
        if (bestMatch.bestMatchPaper == null || bestMatch.bestSimilarity < 15.0) {
            // Generate comprehensive report even for very low similarity
            double actualSimilarity = bestMatch.bestMatchPaper != null ? bestMatch.bestSimilarity : 0.0;
            
            ThesisVerificationResponse.AIAnalysis aiAnalysis = 
                new ThesisVerificationResponse.AIAnalysis(
                    actualSimilarity / 100.0, 0.0, actualSimilarity / 100.0);
            aiAnalysis.setMatchedPapersCount(totalPapersCompared);
            aiAnalysis.setTopMatches(bestMatch.topMatches);
            
            // Add AI detection results
            aiAnalysis.setAiDetectionScore(aiDetectionResult.getAiProbabilityPercentage());
            aiAnalysis.setAiDetectionConclusion(aiDetectionResult.getConclusion());
            aiAnalysis.setAiDetectionIndicators(aiDetectionResult.getIndicators());
            
            // Always generate professional report with 0% plagiarism for original work
            ThesisVerificationResponse response = new ThesisVerificationResponse(
                false, null, actualSimilarity, "ORIGINAL_WORK", 0.0, aiAnalysis);
            
            if (actualSimilarity <= 5.0) {
                response.setMessage(String.format("✅ VERIFICATION COMPLETE: No similar research found in database. " +
                    "Similarity Score: %.1f%% | Plagiarism Risk: 0%% | " +
                    "Status: Original work detected. No title conflicts found.", actualSimilarity));
            } else {
                response.setMessage(String.format("✅ VERIFICATION COMPLETE: Very low similarity detected. " +
                    "Similarity Score: %.1f%% | Plagiarism Risk: 0%% | " +
                    "Status: Appears to be original work with minimal overlap.", actualSimilarity));
            }
            
            return response;
        }
        
        // Found similar paper - use DIRECT similarity-based plagiarism calculation
        String matchType = similarityService.determineMatchType(bestMatch.bestSimilarity);
        
        // FIXED: Direct plagiarism calculation - high similarity = high plagiarism
        double plagiarismScore = bestMatch.bestSimilarity; // Start with similarity percentage
        
        // Boost plagiarism score based on title similarity
        if (bestMatch.bestTitleStringSimilarity >= 90.0) {
            plagiarismScore = Math.min(100.0, plagiarismScore + 10.0);
        } else if (bestMatch.bestTitleStringSimilarity >= 80.0) {
            plagiarismScore = Math.min(100.0, plagiarismScore + 5.0);
        }
        
        // Ensure minimum plagiarism percentages for high similarity
        if (bestMatch.bestSimilarity >= 95.0) {
            plagiarismScore = Math.max(plagiarismScore, 95.0);
            matchType = "IDENTICAL_CONTENT";
        } else if (bestMatch.bestSimilarity >= 90.0) {
            plagiarismScore = Math.max(plagiarismScore, 90.0);
        } else if (bestMatch.bestSimilarity >= 80.0) {
            plagiarismScore = Math.max(plagiarismScore, 80.0);
        }
        
        log.info("🔍 Plagiarism Analysis: Similarity={}%, Title Similarity={}%, Final Plagiarism={}%", 
                bestMatch.bestSimilarity, bestMatch.bestTitleStringSimilarity, plagiarismScore);
        
        ThesisVerificationResponse.AIAnalysis aiAnalysis = new ThesisVerificationResponse.AIAnalysis(
            bestMatch.bestSimilarity / 100.0, 
            bestMatch.bestTitleStringSimilarity / 100.0, 
            bestMatch.bestContentSimilarity / 100.0);
        aiAnalysis.setMatchedPapersCount(totalPapersCompared);
        aiAnalysis.setTopMatches(bestMatch.topMatches.size() > 5 ? 
            bestMatch.topMatches.subList(0, 5) : bestMatch.topMatches);
        
        // Add AI detection results
        aiAnalysis.setAiDetectionScore(aiDetectionResult.getAiProbabilityPercentage());
        aiAnalysis.setAiDetectionConclusion(aiDetectionResult.getConclusion());
        aiAnalysis.setAiDetectionIndicators(aiDetectionResult.getIndicators());
        
        boolean isVerified = bestMatch.bestSimilarity >= 75.0; // High similarity threshold
        
        ThesisVerificationResponse response = new ThesisVerificationResponse(
            isVerified, bestMatch.bestMatchPaper, bestMatch.bestSimilarity, 
            matchType, plagiarismScore, aiAnalysis);
        
        // Role-based warning messages with AI detection
        response.setMessage(generateRoleBasedMessage(bestMatch, plagiarismScore, isVerified, userType, aiDetectionResult));
        
        return response;
    }
    
    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename is required");
        }
        
        // Check if file type is supported using the document extractor service
        if (!documentTextExtractorService.isSupportedFileType(filename)) {
            throw new IllegalArgumentException("Unsupported file format. " + 
                documentTextExtractorService.getSupportedFileTypesDescription());
        }
        
        // 50MB limit
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 50MB");
        }
        
        log.info("✅ File validation passed for: {} (size: {} bytes)", filename, file.getSize());
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
     * Inner class to hold similarity calculation results
     */
    private static class SimilarityResult {
        final ResearchPaper bestMatchPaper;
        final double bestSimilarity;
        final double bestTitleSimilarity;
        final double bestContentSimilarity;
        final double bestTitleStringSimilarity;
        final boolean exactTitleMatch;
        final List<ThesisVerificationResponse.SimilarPaper> topMatches;
        
        SimilarityResult(ResearchPaper bestMatchPaper, double bestSimilarity, 
                        double bestTitleSimilarity, double bestContentSimilarity,
                        double bestTitleStringSimilarity, boolean exactTitleMatch,
                        List<ThesisVerificationResponse.SimilarPaper> topMatches) {
            this.bestMatchPaper = bestMatchPaper;
            this.bestSimilarity = bestSimilarity;
            this.bestTitleSimilarity = bestTitleSimilarity;
            this.bestContentSimilarity = bestContentSimilarity;
            this.bestTitleStringSimilarity = bestTitleStringSimilarity;
            this.exactTitleMatch = exactTitleMatch;
            this.topMatches = topMatches;
        }
    }
    
    /**
     * Generate content-based hash from extracted text (normalized)
     */
    private String generateContentHash(String text) {
        try {
            // Normalize the text to handle minor formatting differences
            String normalizedText = text
                .replaceAll("\\s+", " ")              // Replace multiple spaces with single space
                .replaceAll("[\\r\\n\\t]", " ")       // Replace line breaks and tabs with space
                .replaceAll("[^\\p{L}\\p{N}\\s]", "") // Keep only letters, numbers, and spaces
                .toLowerCase()                        // Convert to lowercase
                .trim();                              // Remove leading/trailing spaces
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(normalizedText.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to generate content hash", e);
            return "";
        }
    }
    
    /**
     * Find paper with nearly identical content (95%+ similarity) - for truly identical papers only
     * Moderately similar papers (70-94%) will go through normal AI analysis
     */
    private Optional<ResearchPaper> findNearlyIdenticalContent(String documentText, String contentHash) {
        try {
            // First, try to find papers with similar content hash
            List<ResearchPaper> allPapers = researchPaperRepository.findAll();
            
            for (ResearchPaper paper : allPapers) {
                // Skip papers without embeddings (no text was extracted)
                if (paper.getDocumentEmbedding() == null) {
                    continue;
                }
                
                // Try to match content hash if we had stored it (future enhancement)
                // For now, we'll do text similarity comparison
                
                // Quick text length comparison (identical content should have very similar lengths)
                if (paper.getAbstractText() != null) {
                    double lengthRatio = (double) Math.min(documentText.length(), paper.getAbstractText().length()) 
                                       / Math.max(documentText.length(), paper.getAbstractText().length());
                    
                    if (lengthRatio > 0.98) { // Nearly identical lengths (98%+)
                        log.info("Found paper with nearly identical text length: {} vs {}", 
                               documentText.length(), paper.getAbstractText().length());
                        
                        // Additional check: compare first 1000 characters (normalized) for better accuracy
                        int compareLength = Math.min(1000, Math.min(documentText.length(), paper.getAbstractText().length()));
                        String submittedStart = normalizeTextForComparison(documentText.substring(0, compareLength));
                        String existingStart = normalizeTextForComparison(paper.getAbstractText().substring(0, compareLength));
                        
                        double textSimilarity = calculateSimpleTextSimilarity(submittedStart, existingStart);
                        log.info("Text similarity check: {}% similarity detected", textSimilarity);
                        
                        // Only return match for VERY high similarity (95%+) - truly identical content
                        if (textSimilarity > 95.0) {
                            log.warn("🚨 NEARLY IDENTICAL CONTENT: {}% similarity - treating as identical", textSimilarity);
                            return Optional.of(paper);
                        } else if (textSimilarity > 70.0) {
                            log.info("📋 MODERATE SIMILARITY DETECTED: {}% - will proceed to AI analysis", textSimilarity);
                            // Don't return - let it go through AI analysis for proper scoring
                        }
                    }
                }
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error in content similarity check", e);
            return Optional.empty();
        }
    }
    
    /**
     * Normalize text for comparison
     */
    private String normalizeTextForComparison(String text) {
        return text.replaceAll("\\s+", " ")
                  .replaceAll("[^\\p{L}\\p{N}\\s]", "")
                  .toLowerCase()
                  .trim();
    }
    
    /**
     * Calculate simple text similarity percentage
     */
    private double calculateSimpleTextSimilarity(String text1, String text2) {
        if (text1.equals(text2)) {
            return 100.0;
        }
        
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
     * Create response for identical content detection
     */
    private ThesisVerificationResponse createIdenticalContentResponse(ResearchPaper matchedPaper) {
        log.warn("🚨 IDENTICAL CONTENT FOUND: {}", matchedPaper.getTitle());
        
        ThesisVerificationResponse.AIAnalysis aiAnalysis = 
            new ThesisVerificationResponse.AIAnalysis(1.0, 1.0, 1.0);
        aiAnalysis.setMatchedPapersCount(1);
        
        ThesisVerificationResponse response = new ThesisVerificationResponse(
            true, matchedPaper, 100.0, "IDENTICAL_CONTENT", 100.0, aiAnalysis);
        response.setMessage("🚨 CRITICAL: Identical content detected! This document appears to be the same as an existing paper in our database.");
        
        return response;
    }
    
    /**
     * Generate role-based verification message with AI detection results
     */
    private String generateRoleBasedMessage(SimilarityResult bestMatch, double plagiarismScore, boolean isVerified, String userType, AIDetectionService.AIDetectionResult aiDetectionResult) {
        boolean isProfessor = "PROFESSOR".equalsIgnoreCase(userType);
        ResearchPaper matchedPaper = bestMatch.bestMatchPaper;
        
        // Format AI detection info
        String aiInfo = String.format("AI Detection: %.1f%% probability", aiDetectionResult.getAiProbabilityPercentage());
        
        if (plagiarismScore >= 95.0) {
            if (isProfessor) {
                return String.format("🚨 SEVERE PLAGIARISM DETECTED: %.1f%% similarity with '%s' by %s from %s (%s). " +
                    "This appears to be copied or nearly identical content! | %s", 
                    bestMatch.bestSimilarity, matchedPaper.getTitle(), matchedPaper.getAuthor(), 
                    matchedPaper.getInstitution(), matchedPaper.getDepartment(), aiInfo);
            } else {
                return String.format("🚨 SEVERE PLAGIARISM DETECTED: Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | %s | " +
                    "Status: Nearly identical content found. This appears to be copied material.", 
                    bestMatch.bestSimilarity, plagiarismScore, aiInfo);
            }
        } else if (plagiarismScore >= 85.0) {
            if (isProfessor) {
                return String.format("⚠️ HIGH PLAGIARISM RISK: %.1f%% similarity detected with '%s' by %s from %s. " +
                    "Significant overlap found. Department: %s | Plagiarism Score: %.1f%% | %s", 
                    bestMatch.bestSimilarity, matchedPaper.getTitle(), matchedPaper.getAuthor(), 
                    matchedPaper.getInstitution(), matchedPaper.getDepartment(), plagiarismScore, aiInfo);
            } else {
                return String.format("⚠️ HIGH PLAGIARISM RISK: Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | %s | " +
                    "Status: Significant overlap detected with existing research", 
                    bestMatch.bestSimilarity, plagiarismScore, aiInfo);
            }
        } else if (plagiarismScore >= 70.0) {
            if (isProfessor) {
                return String.format("⚠️ MODERATE PLAGIARISM RISK: %.1f%% similarity with '%s' by %s (%s). " +
                    "Please review for potential plagiarism. Institution: %s | Score: %.1f%% | %s", 
                    bestMatch.bestSimilarity, matchedPaper.getTitle(), matchedPaper.getAuthor(), 
                    matchedPaper.getDepartment(), matchedPaper.getInstitution(), plagiarismScore, aiInfo);
            } else {
                return String.format("⚠️ MODERATE PLAGIARISM RISK: Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | %s | " +
                    "Status: Moderate overlap detected - please review content carefully", 
                    bestMatch.bestSimilarity, plagiarismScore, aiInfo);
            }
        } else if (isVerified) {
            if (isProfessor) {
                return String.format("📊 SIMILARITY DETECTED: %.1f%% similarity with existing research '%s' by %s from %s. " +
                    "Department: %s | Plagiarism Score: %.1f%% | %s | Recommended: Review for citations and references", 
                    bestMatch.bestSimilarity, matchedPaper.getTitle(), matchedPaper.getAuthor(), 
                    matchedPaper.getInstitution(), matchedPaper.getDepartment(), plagiarismScore, aiInfo);
            } else {
                return String.format("📊 SIMILARITY DETECTED: Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | %s | " +
                    "Status: Some similarity found with existing research", 
                    bestMatch.bestSimilarity, plagiarismScore, aiInfo);
            }
        } else {
            return String.format("✅ LOW SIMILARITY: Similarity Score: %.1f%% | Plagiarism Risk: %.1f%% | %s | " +
                "Status: Minimal overlap detected - appears to be largely original work", 
                bestMatch.bestSimilarity, plagiarismScore, aiInfo);
        }
    }
}