package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilarityService {
    
    private static final Logger log = LoggerFactory.getLogger(SimilarityService.class);
    
    /**
     * Calculate cosine similarity between two embedding vectors
     * Returns a value between -1 and 1, where 1 means identical, 0 means orthogonal, -1 means opposite
     */
    public double calculateCosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null) {
            log.warn("One or both vectors are null, returning 0 similarity");
            return 0.0;
        }
        
        if (vectorA.size() != vectorB.size()) {
            log.warn("Vector dimensions don't match: {} vs {}, returning 0 similarity", 
                    vectorA.size(), vectorB.size());
            return 0.0;
        }
        
        if (vectorA.isEmpty()) {
            log.warn("Vectors are empty, returning 0 similarity");
            return 0.0;
        }
        
        try {
            double dotProduct = 0.0;
            double normA = 0.0;
            double normB = 0.0;
            
            for (int i = 0; i < vectorA.size(); i++) {
                double valueA = vectorA.get(i);
                double valueB = vectorB.get(i);
                
                dotProduct += valueA * valueB;
                normA += valueA * valueA;
                normB += valueB * valueB;
            }
            
            if (normA == 0.0 || normB == 0.0) {
                log.warn("One or both vectors have zero magnitude, returning 0 similarity");
                return 0.0;
            }
            
            double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
            
            // Clamp to [-1, 1] to handle floating point precision issues
            similarity = Math.max(-1.0, Math.min(1.0, similarity));
            
            log.debug("Calculated cosine similarity: {}", similarity);
            return similarity;
            
        } catch (Exception e) {
            log.error("Error calculating cosine similarity: {}", e.getMessage(), e);
            return 0.0;
        }
    }
    
    /**
     * Calculate similarity as a percentage (0-100%)
     * Converts cosine similarity from [-1,1] to [0,100] range
     */
    public double calculateSimilarityPercentage(List<Double> vectorA, List<Double> vectorB) {
        double cosineSim = calculateCosineSimilarity(vectorA, vectorB);
        
        // Convert from [-1,1] to [0,100]
        // We use (cosine + 1) / 2 * 100 to map [-1,1] to [0,100]
        double percentage = ((cosineSim + 1.0) / 2.0) * 100.0;
        
        return Math.round(percentage * 100.0) / 100.0; // Round to 2 decimal places
    }
    
    /**
     * Calculate Euclidean distance between two vectors
     * Lower distance means more similar
     */
    public double calculateEuclideanDistance(List<Double> vectorA, List<Double> vectorB) {
        if (vectorA == null || vectorB == null || vectorA.size() != vectorB.size()) {
            return Double.MAX_VALUE;
        }
        
        double sum = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            double diff = vectorA.get(i) - vectorB.get(i);
            sum += diff * diff;
        }
        
        return Math.sqrt(sum);
    }
    
    /**
     * Determine match type based on similarity score
     */
    public String determineMatchType(double similarityPercentage) {
        if (similarityPercentage >= 95.0) {
            return "EXACT_MATCH";
        } else if (similarityPercentage >= 75.0) {
            return "HIGH_SIMILARITY";
        } else if (similarityPercentage >= 50.0) {
            return "PARTIAL_MATCH";
        } else {
            return "NO_MATCH";
        }
    }
    
    /**
     * Calculate plagiarism score based on similarity
     * Returns percentage of potential plagiarism (0-100%)
     * FIXED: Direct correlation - high similarity = high plagiarism
     */
    public double calculatePlagiarismScore(double similarityPercentage) {
        // SIMPLE DIRECT CORRELATION: Similarity % = Plagiarism %
        if (similarityPercentage >= 95.0) {
            return 100.0; // Very high similarity = 100% plagiarism
        } else if (similarityPercentage >= 90.0) {
            return 95.0 + (similarityPercentage - 90.0); // 95-100% plagiarism
        } else if (similarityPercentage >= 80.0) {
            return 85.0 + (similarityPercentage - 80.0); // 85-95% plagiarism
        } else if (similarityPercentage >= 70.0) {
            return 70.0 + (similarityPercentage - 70.0); // 70-85% plagiarism
        } else if (similarityPercentage >= 50.0) {
            return 50.0 + (similarityPercentage - 50.0); // 50-70% plagiarism
        } else {
            // For low similarity, use direct percentage but minimum 5%
            return Math.max(5.0, similarityPercentage);
        }
    }
    
    /**
     * Calculate plagiarism score with title similarity factor
     * If titles are very similar, increase plagiarism score significantly
     */
    public double calculateAdvancedPlagiarismScore(double contentSimilarity, double titleSimilarity) {
        double basePlagiarism = calculatePlagiarismScore(contentSimilarity);
        
        // If title similarity is very high, boost plagiarism score
        if (titleSimilarity >= 95.0) {
            // Very similar titles indicate potential same research topic
            basePlagiarism = Math.min(100.0, basePlagiarism + 20.0);
        } else if (titleSimilarity >= 85.0) {
            basePlagiarism = Math.min(100.0, basePlagiarism + 15.0);
        } else if (titleSimilarity >= 75.0) {
            basePlagiarism = Math.min(100.0, basePlagiarism + 10.0);
        }
        
        return basePlagiarism;
    }
    
    /**
     * Calculate combined similarity score from title and content embeddings
     */
    public double calculateCombinedSimilarity(
            List<Double> titleA, List<Double> titleB,
            List<Double> contentA, List<Double> contentB,
            double titleWeight, double contentWeight) {
        
        double titleSim = calculateSimilarityPercentage(titleA, titleB);
        double contentSim = calculateSimilarityPercentage(contentA, contentB);
        
        // Weighted average
        double totalWeight = titleWeight + contentWeight;
        if (totalWeight == 0) {
            return 0.0;
        }
        
        double combinedSimilarity = (titleSim * titleWeight + contentSim * contentWeight) / totalWeight;
        
        log.debug("Combined similarity - Title: {}% (weight: {}), Content: {}% (weight: {}), Combined: {}%",
                titleSim, titleWeight, contentSim, contentWeight, combinedSimilarity);
        
        return combinedSimilarity;
    }
    
    /**
     * Check if vectors are valid for similarity calculation
     */
    public boolean areVectorsValid(List<Double> vectorA, List<Double> vectorB) {
        return vectorA != null && vectorB != null && 
               !vectorA.isEmpty() && !vectorB.isEmpty() && 
               vectorA.size() == vectorB.size();
    }
    
    /**
     * Calculate batch similarities between one vector and multiple vectors
     * Returns array of similarity scores in the same order as input vectors
     */
    public double[] calculateBatchSimilarities(List<Double> sourceVector, List<List<Double>> targetVectors) {
        if (sourceVector == null || targetVectors == null) {
            return new double[0];
        }
        
        double[] similarities = new double[targetVectors.size()];
        
        for (int i = 0; i < targetVectors.size(); i++) {
            similarities[i] = calculateSimilarityPercentage(sourceVector, targetVectors.get(i));
        }
        
        return similarities;
    }
    
    /**
     * Check for exact or near-exact title matches using string comparison
     */
    public boolean isExactTitleMatch(String title1, String title2) {
        if (title1 == null || title2 == null) {
            return false;
        }
        
        // Normalize titles for comparison
        String normalized1 = normalizeTitle(title1);
        String normalized2 = normalizeTitle(title2);
        
        return normalized1.equals(normalized2);
    }
    
    /**
     * Calculate title similarity percentage using string comparison
     */
    public double calculateTitleStringSimilarity(String title1, String title2) {
        if (title1 == null || title2 == null) {
            return 0.0;
        }
        
        String normalized1 = normalizeTitle(title1);
        String normalized2 = normalizeTitle(title2);
        
        // Check for exact match first
        if (normalized1.equals(normalized2)) {
            return 100.0;
        }
        
        // Calculate Levenshtein distance similarity
        int distance = levenshteinDistance(normalized1, normalized2);
        int maxLength = Math.max(normalized1.length(), normalized2.length());
        
        if (maxLength == 0) {
            return 100.0;
        }
        
        double similarity = ((double) (maxLength - distance) / maxLength) * 100.0;
        return Math.max(0.0, similarity);
    }
    
    /**
     * Normalize title for comparison (remove extra spaces, punctuation, convert to lowercase)
     */
    private String normalizeTitle(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-zA-Z0-9\\s]", "") // Remove punctuation
            .replaceAll("\\s+", " ") // Normalize spaces
            .trim();
    }
    
    /**
     * Calculate Levenshtein distance between two strings
     */
    private int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        
        return dp[str1.length()][str2.length()];
    }
}