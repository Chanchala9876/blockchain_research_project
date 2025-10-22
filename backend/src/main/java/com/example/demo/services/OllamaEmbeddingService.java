package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class OllamaEmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(OllamaEmbeddingService.class);
    
    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;
    
    @Value("${ollama.model:nomic-embed-text}")
    private String embeddingModel;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public OllamaEmbeddingService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generate embeddings for text using Ollama
     */
    public List<Double> generateEmbedding(String text) {
        try {
            log.info("Generating embedding for text (length: {})", text.length());
            
            // Prepare request body
            EmbeddingRequest request = new EmbeddingRequest(embeddingModel, text);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<EmbeddingRequest> httpEntity = new HttpEntity<>(request, headers);
            
            // Make request to Ollama
            String url = ollamaUrl + "/api/embeddings";
            ResponseEntity<EmbeddingResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, httpEntity, EmbeddingResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Double> embedding = response.getBody().getEmbedding();
                log.info("Successfully generated embedding with {} dimensions", embedding.size());
                return embedding;
            } else {
                throw new RuntimeException("Failed to generate embedding: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error generating embedding: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate embedding", e);
        }
    }
    
    /**
     * Generate embeddings for title text
     */
    public List<Double> generateTitleEmbedding(String title) {
        log.info("Generating title embedding for: {}", title);
        return generateEmbedding("Title: " + title);
    }
    
    /**
     * Generate embeddings for document content
     */
    public List<Double> generateDocumentEmbedding(String documentText) {
        log.info("Generating document embedding for content (length: {})", documentText.length());
        
        // Truncate text if too long (Ollama has input limits)
        String truncatedText = documentText.length() > 8000 ? 
            documentText.substring(0, 8000) + "..." : documentText;
            
        return generateEmbedding("Document: " + truncatedText);
    }
    
    /**
     * Check if Ollama service is available
     */
    public boolean isOllamaAvailable() {
        try {
            String url = ollamaUrl + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("Ollama service not available at {}: {}", ollamaUrl, e.getMessage());
            return false;
        }
    }
    
    // Request/Response DTOs
    static class EmbeddingRequest {
        private String model;
        private String prompt;
        
        public EmbeddingRequest(String model, String prompt) {
            this.model = model;
            this.prompt = prompt;
        }
        
        public String getModel() { return model; }
        public String getPrompt() { return prompt; }
    }
    
    static class EmbeddingResponse {
        @JsonProperty("embedding")
        private List<Double> embedding;
        
        public List<Double> getEmbedding() { return embedding; }
        public void setEmbedding(List<Double> embedding) { this.embedding = embedding; }
    }
}