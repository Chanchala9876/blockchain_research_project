package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Service for detecting AI-generated content in academic papers
 * Uses multiple heuristics to identify potential AI assistance
 */
@Service
public class AIDetectionService {
    
    private static final Logger log = LoggerFactory.getLogger(AIDetectionService.class);
    
    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;
    
    // Patterns that may indicate AI-generated content
    private static final List<String> AI_PHRASES = Arrays.asList(
        "as an ai", "i'm an ai", "artificial intelligence", "machine learning model",
        "trained on data", "large language model", "neural network", "deep learning",
        "in conclusion, it is evident that", "furthermore, it should be noted that",
        "it is worth noting that", "it is important to acknowledge that",
        "in this regard, it can be observed", "moreover, it is crucial to understand",
        "additionally, it should be emphasized", "consequently, it becomes apparent",
        "undeniably", "unequivocally", "indubitably", "irrefutably",
        "seamlessly integrated", "cutting-edge technology", "paradigm shift",
        "holistic approach", "comprehensive analysis", "multifaceted approach",
        "robust methodology", "innovative framework", "state-of-the-art",
        "unprecedented", "revolutionary breakthrough", "groundbreaking research"
    );
    
    // Common AI writing patterns
    private static final List<Pattern> AI_PATTERNS = Arrays.asList(
        Pattern.compile("\\b(furthermore|moreover|additionally|consequently)\\s+,?\\s*it\\s+(is|should|can|must)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bin\\s+conclusion\\s*,?\\s*(it\\s+is\\s+evident|we\\s+can\\s+see|it\\s+becomes\\s+clear)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bit\\s+is\\s+(worth\\s+noting|important\\s+to\\s+acknowledge|crucial\\s+to\\s+understand)\\s+that\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(comprehensive|robust|innovative|cutting-edge|state-of-the-art|groundbreaking)\\s+(analysis|approach|methodology|framework|research|solution)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(seamlessly|effortlessly|inherently|fundamentally|intrinsically)\\s+(integrated|connected|linked|established)\\b", Pattern.CASE_INSENSITIVE)
    );
    
    /**
     * Analyze text for AI-generated content indicators
     * Returns probability percentage (0-100) that content was AI-generated
     */
    public AIDetectionResult analyzeForAIContent(String documentText, String title, String abstractText) {
        log.info("🤖 Starting AI detection analysis for document...");
        
        try {
            // Combine all text for analysis
            String fullText = combineTextSources(documentText, title, abstractText);
            
            if (fullText.trim().length() < 100) {
                log.warn("Text too short for reliable AI detection");
                return new AIDetectionResult(0.0, "Insufficient text for analysis", new ArrayList<>());
            }
            
            double aiProbability = 0.0;
            List<String> indicators = new ArrayList<>();
            
            // 1. Vocabulary and phrase analysis (25% weight)
            double vocabularyScore = analyzeVocabularyPatterns(fullText, indicators);
            aiProbability += vocabularyScore * 0.25;
            
            // 2. Writing style analysis (30% weight)
            double styleScore = analyzeWritingStyle(fullText, indicators);
            aiProbability += styleScore * 0.30;
            
            // 3. Structure and flow analysis (20% weight)
            double structureScore = analyzeStructuralPatterns(fullText, indicators);
            aiProbability += structureScore * 0.20;
            
            // 4. Content consistency analysis (15% weight)
            double consistencyScore = analyzeContentConsistency(fullText, indicators);
            aiProbability += consistencyScore * 0.15;
            
            // 5. Academic authenticity check (10% weight)
            double authenticityScore = analyzeAcademicAuthenticity(fullText, title, indicators);
            aiProbability += authenticityScore * 0.10;
            
            // Apply confidence adjustments based on text length and quality
            aiProbability = applyConfidenceAdjustments(aiProbability, fullText.length());
            
            // Cap at 100%
            aiProbability = Math.min(100.0, Math.max(0.0, aiProbability));
            
            String conclusion = generateAIDetectionConclusion(aiProbability);
            
            log.info("🤖 AI Detection completed: {}% probability, {} indicators found", 
                    Math.round(aiProbability), indicators.size());
            
            return new AIDetectionResult(aiProbability, conclusion, indicators);
            
        } catch (Exception e) {
            log.error("Error during AI content analysis: {}", e.getMessage(), e);
            return new AIDetectionResult(0.0, "AI detection analysis failed: " + e.getMessage(), 
                                       Arrays.asList("Analysis error occurred"));
        }
    }
    
    /**
     * Combine text sources for analysis
     */
    private String combineTextSources(String documentText, String title, String abstractText) {
        StringBuilder combined = new StringBuilder();
        
        if (title != null && !title.trim().isEmpty()) {
            combined.append(title).append(" ");
        }
        
        if (abstractText != null && !abstractText.trim().isEmpty()) {
            combined.append(abstractText).append(" ");
        }
        
        if (documentText != null && !documentText.trim().isEmpty()) {
            // For long documents, analyze first 5000 characters + random middle section
            if (documentText.length() > 10000) {
                combined.append(documentText.substring(0, 5000));
                int midStart = documentText.length() / 2 - 1000;
                int midEnd = documentText.length() / 2 + 1000;
                if (midStart > 5000 && midEnd < documentText.length()) {
                    combined.append(" ").append(documentText.substring(midStart, midEnd));
                }
            } else {
                combined.append(documentText);
            }
        }
        
        return combined.toString();
    }
    
    /**
     * Analyze vocabulary patterns for AI indicators
     */
    private double analyzeVocabularyPatterns(String text, List<String> indicators) {
        double score = 0.0;
        String lowerText = text.toLowerCase();
        
        // Check for explicit AI phrases
        int aiPhraseCount = 0;
        for (String phrase : AI_PHRASES) {
            if (lowerText.contains(phrase.toLowerCase())) {
                aiPhraseCount++;
                indicators.add("AI phrase detected: '" + phrase + "'");
                if (phrase.contains("ai") || phrase.contains("artificial")) {
                    score += 30.0; // Heavy penalty for explicit AI mentions
                } else {
                    score += 5.0; // Moderate penalty for AI-style phrases
                }
            }
        }
        
        // Check for AI writing patterns
        for (Pattern pattern : AI_PATTERNS) {
            if (pattern.matcher(text).find()) {
                score += 8.0;
                indicators.add("AI writing pattern detected");
            }
        }
        
        // Overuse of complex vocabulary (AI tends to use unnecessarily complex words)
        String[] complexWords = {"utilize", "facilitate", "demonstrate", "comprehensive", "substantial", 
                               "significant", "innovative", "substantial", "extensive", "inherent"};
        int complexWordCount = 0;
        for (String word : complexWords) {
            complexWordCount += countOccurrences(lowerText, word.toLowerCase());
        }
        
        int totalWords = text.split("\\s+").length;
        double complexityRatio = totalWords > 0 ? (double) complexWordCount / totalWords : 0;
        if (complexityRatio > 0.05) { // More than 5% complex words
            score += complexityRatio * 200; // Scale up the penalty
            indicators.add("High complexity vocabulary ratio: " + String.format("%.1f%%", complexityRatio * 100));
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Analyze writing style for AI characteristics
     */
    private double analyzeWritingStyle(String text, List<String> indicators) {
        double score = 0.0;
        
        // Sentence length analysis - AI often produces uniform sentence lengths
        String[] sentences = text.split("[.!?]+");
        if (sentences.length > 5) {
            List<Integer> sentenceLengths = new ArrayList<>();
            for (String sentence : sentences) {
                String trimmed = sentence.trim();
                if (trimmed.length() > 10) { // Skip very short sentences
                    sentenceLengths.add(trimmed.split("\\s+").length);
                }
            }
            
            if (sentenceLengths.size() > 3) {
                double avgLength = sentenceLengths.stream().mapToInt(Integer::intValue).average().orElse(0.0);
                double variance = calculateVariance(sentenceLengths, avgLength);
                
                // Low variance indicates uniform sentence length (AI characteristic)
                if (variance < 10.0 && avgLength > 15) {
                    score += 25.0;
                    indicators.add("Uniform sentence lengths detected (avg: " + String.format("%.1f", avgLength) + " words)");
                }
                
                // Very long average sentences (AI tends to be verbose)
                if (avgLength > 25) {
                    score += 15.0;
                    indicators.add("Excessively long sentences (avg: " + String.format("%.1f", avgLength) + " words)");
                }
            }
        }
        
        // Repetitive transition usage
        String[] transitions = {"furthermore", "moreover", "additionally", "consequently", "therefore", "however"};
        int transitionCount = 0;
        for (String transition : transitions) {
            transitionCount += countOccurrences(text.toLowerCase(), transition);
        }
        
        int totalSentences = sentences.length;
        if (totalSentences > 0) {
            double transitionRatio = (double) transitionCount / totalSentences;
            if (transitionRatio > 0.3) { // More than 30% of sentences have transitions
                score += 20.0;
                indicators.add("Excessive transition word usage: " + String.format("%.1f%%", transitionRatio * 100));
            }
        }
        
        // Perfect grammar indicators (too perfect can indicate AI)
        if (hasUniformPunctuation(text)) {
            score += 10.0;
            indicators.add("Suspiciously uniform punctuation patterns");
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Analyze structural patterns
     */
    private double analyzeStructuralPatterns(String text, List<String> indicators) {
        double score = 0.0;
        
        // Check for overly structured paragraphs (AI tends to be very organized)
        String[] paragraphs = text.split("\n\n+");
        if (paragraphs.length > 3) {
            int similarStartCount = 0;
            for (int i = 1; i < paragraphs.length; i++) {
                if (paragraphs[i].trim().length() > 50) {
                    String start1 = getFirstWords(paragraphs[i-1], 3);
                    String start2 = getFirstWords(paragraphs[i], 3);
                    if (start1.length() > 0 && start2.length() > 0) {
                        // Check if paragraphs start with similar patterns
                        if (start1.toLowerCase().startsWith("the") && start2.toLowerCase().startsWith("the") ||
                            start1.toLowerCase().startsWith("in") && start2.toLowerCase().startsWith("in") ||
                            start1.toLowerCase().startsWith("this") && start2.toLowerCase().startsWith("this")) {
                            similarStartCount++;
                        }
                    }
                }
            }
            
            if (similarStartCount > paragraphs.length * 0.4) {
                score += 15.0;
                indicators.add("Repetitive paragraph structure detected");
            }
        }
        
        // Check for formulaic conclusions
        String lowerText = text.toLowerCase();
        if (lowerText.contains("in conclusion") && 
            (lowerText.contains("it is evident") || lowerText.contains("we can conclude") || 
             lowerText.contains("it becomes clear"))) {
            score += 12.0;
            indicators.add("Formulaic conclusion structure detected");
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Analyze content consistency
     */
    private double analyzeContentConsistency(String text, List<String> indicators) {
        double score = 0.0;
        
        // Check for topic drift (AI sometimes lacks focus)
        // This is a simplified check - in practice, you'd use more sophisticated NLP
        String[] sentences = text.split("[.!?]+");
        if (sentences.length > 10) {
            // Look for abrupt topic changes without proper transitions
            int abruptChanges = 0;
            for (int i = 1; i < Math.min(sentences.length, 20); i++) {
                if (hasAbruptTopicChange(sentences[i-1], sentences[i])) {
                    abruptChanges++;
                }
            }
            
            if (abruptChanges > 3) {
                score += 10.0;
                indicators.add("Potential topic inconsistencies detected");
            }
        }
        
        // Check for overly generic statements
        String[] genericPhrases = {"it is important to note", "it should be mentioned", "one must consider",
                                 "it is worth highlighting", "it cannot be denied"};
        int genericCount = 0;
        for (String phrase : genericPhrases) {
            if (text.toLowerCase().contains(phrase)) {
                genericCount++;
            }
        }
        
        if (genericCount > 2) {
            score += genericCount * 5.0;
            indicators.add("Multiple generic filler phrases detected (" + genericCount + " instances)");
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Analyze academic authenticity
     */
    private double analyzeAcademicAuthenticity(String text, String title, List<String> indicators) {
        double score = 0.0;
        
        // Check for lack of specific details (AI often generates vague content)
        if (!containsSpecificDetails(text)) {
            score += 15.0;
            indicators.add("Lack of specific technical details or examples");
        }
        
        // Check for title-content mismatch
        if (title != null && title.trim().length() > 0) {
            if (!isContentRelevantToTitle(text, title)) {
                score += 10.0;
                indicators.add("Content may not fully align with stated title");
            }
        }
        
        // Check for overuse of superlatives (AI tends to be overly enthusiastic)
        String[] superlatives = {"revolutionary", "groundbreaking", "unprecedented", "extraordinary", 
                               "remarkable", "exceptional", "outstanding", "phenomenal"};
        int superlativeCount = 0;
        for (String superlative : superlatives) {
            superlativeCount += countOccurrences(text.toLowerCase(), superlative);
        }
        
        if (superlativeCount > 2) {
            score += superlativeCount * 3.0;
            indicators.add("Excessive use of superlative language (" + superlativeCount + " instances)");
        }
        
        return Math.min(100.0, score);
    }
    
    /**
     * Apply confidence adjustments based on text characteristics
     */
    private double applyConfidenceAdjustments(double score, int textLength) {
        // Reduce confidence for very short texts
        if (textLength < 500) {
            return score * 0.7; // Reduce by 30%
        } else if (textLength < 1000) {
            return score * 0.85; // Reduce by 15%
        }
        
        // For very long texts, AI detection becomes more reliable
        if (textLength > 5000) {
            return Math.min(100.0, score * 1.1); // Increase by 10%
        }
        
        return score;
    }
    
    /**
     * Generate conclusion based on AI probability score
     */
    private String generateAIDetectionConclusion(double probability) {
        if (probability >= 80.0) {
            return "HIGH PROBABILITY: Strong indicators suggest this content was likely generated with AI assistance";
        } else if (probability >= 60.0) {
            return "MODERATE PROBABILITY: Multiple indicators suggest possible AI assistance in content generation";
        } else if (probability >= 40.0) {
            return "LOW-MODERATE PROBABILITY: Some patterns suggest potential AI assistance, but inconclusive";
        } else if (probability >= 20.0) {
            return "LOW PROBABILITY: Few indicators detected, likely human-authored with minimal AI assistance";
        } else {
            return "VERY LOW PROBABILITY: Content appears to be primarily human-authored";
        }
    }
    
    // Helper methods
    private int countOccurrences(String text, String substring) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
    
    private double calculateVariance(List<Integer> values, double mean) {
        if (values.size() <= 1) return 0.0;
        
        double sum = 0.0;
        for (int value : values) {
            sum += Math.pow(value - mean, 2);
        }
        return sum / (values.size() - 1);
    }
    
    private boolean hasUniformPunctuation(String text) {
        // Count different types of punctuation
        int periods = countOccurrences(text, ".");
        int commas = countOccurrences(text, ",");
        int semicolons = countOccurrences(text, ";");
        
        // Check if punctuation usage is suspiciously uniform
        return (periods > 5 && commas > 5 && Math.abs(periods - commas) < 2);
    }
    
    private String getFirstWords(String text, int wordCount) {
        String[] words = text.trim().split("\\s+");
        if (words.length < wordCount) return text.trim();
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < wordCount && i < words.length; i++) {
            if (i > 0) result.append(" ");
            result.append(words[i]);
        }
        return result.toString();
    }
    
    private boolean hasAbruptTopicChange(String sentence1, String sentence2) {
        // Simplified topic change detection
        // In practice, you'd use more sophisticated NLP techniques
        
        String[] words1 = sentence1.toLowerCase().split("\\s+");
        String[] words2 = sentence2.toLowerCase().split("\\s+");
        
        if (words1.length < 3 || words2.length < 3) return false;
        
        // Check for common words between consecutive sentences
        Set<String> set1 = new HashSet<>(Arrays.asList(words1));
        Set<String> set2 = new HashSet<>(Arrays.asList(words2));
        
        set1.retainAll(set2);
        
        // If less than 10% word overlap, might be abrupt change
        return (double) set1.size() / Math.min(words1.length, words2.length) < 0.1;
    }
    
    private boolean containsSpecificDetails(String text) {
        // Check for numbers, dates, specific technical terms
        return text.matches(".*\\b\\d{4}\\b.*") || // Years
               text.matches(".*\\d+\\.\\d+.*") ||   // Decimal numbers
               text.matches(".*\\b[A-Z]{2,}\\b.*") || // Acronyms
               text.contains("%") ||
               text.contains("algorithm") ||
               text.contains("methodology") ||
               text.contains("experiment") ||
               text.contains("results") ||
               text.contains("analysis");
    }
    
    private boolean isContentRelevantToTitle(String text, String title) {
        // Extract key terms from title
        String[] titleWords = title.toLowerCase().split("\\s+");
        String textLower = text.toLowerCase();
        
        int relevantCount = 0;
        for (String word : titleWords) {
            if (word.length() > 3 && textLower.contains(word)) {
                relevantCount++;
            }
        }
        
        // At least 50% of significant title words should appear in content
        return titleWords.length > 0 && (double) relevantCount / titleWords.length >= 0.5;
    }
    
    /**
     * Result class for AI detection analysis
     */
    public static class AIDetectionResult {
        private final double aiProbabilityPercentage;
        private final String conclusion;
        private final List<String> indicators;
        
        public AIDetectionResult(double aiProbabilityPercentage, String conclusion, List<String> indicators) {
            this.aiProbabilityPercentage = aiProbabilityPercentage;
            this.conclusion = conclusion;
            this.indicators = indicators != null ? new ArrayList<>(indicators) : new ArrayList<>();
        }
        
        public double getAiProbabilityPercentage() { return aiProbabilityPercentage; }
        public String getConclusion() { return conclusion; }
        public List<String> getIndicators() { return new ArrayList<>(indicators); }
        
        @Override
        public String toString() {
            return String.format("AI Detection: %.1f%% probability (%s)", 
                               aiProbabilityPercentage, conclusion);
        }
    }
}