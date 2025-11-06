package com.example.demo.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PDFTextExtractorService {
    
    private static final Logger log = LoggerFactory.getLogger(PDFTextExtractorService.class);
    
    /**
     * Extract text content from PDF file
     */
    public String extractTextFromPDF(MultipartFile file) throws IOException {
        try {
            log.info("Extracting text from PDF: {}", file.getOriginalFilename());
            
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                
                // Extract text from all pages
                String text = stripper.getText(document);
                
                // Clean up the text
                text = cleanText(text);
                
                log.info("Successfully extracted {} characters from PDF", text.length());
                return text;
            }
            
        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from PDF", e);
        }
    }
    
    /**
     * Clean extracted text
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        
        // Remove excessive whitespace and normalize line breaks
        text = text.replaceAll("\\s+", " ");
        text = text.replaceAll("\\r\\n", " ");
        text = text.replaceAll("\\n", " ");
        text = text.replaceAll("\\r", " ");
        
        // Remove special characters that might interfere with embedding generation
        text = text.replaceAll("[\\x00-\\x1F\\x7F]", "");
        
        return text.trim();
    }
}