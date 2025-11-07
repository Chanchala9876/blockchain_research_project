package com.example.demo.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class DocumentTextExtractorService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentTextExtractorService.class);
    
    // Supported file extensions
    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList("pdf", "docx", "doc");
    private static final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/msword"
    );
    
    /**
     * Extract text content from supported document types (PDF, DOCX)
     */
    public String extractTextFromDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename is required");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        String contentType = file.getContentType();
        
        log.info("📄 Extracting text from document: {} (type: {}, extension: {})", 
                filename, contentType, extension);
        
        // Validate file type
        validateFileType(extension, contentType);
        
        String extractedText;
        
        try {
            switch (extension) {
                case "pdf":
                    extractedText = extractTextFromPDF(file);
                    break;
                case "docx":
                    extractedText = extractTextFromDOCX(file);
                    break;
                case "doc":
                    throw new IOException("Legacy DOC format is not supported. Please convert to DOCX format.");
                default:
                    throw new IOException("Unsupported file format: " + extension);
            }
            
            // Clean and validate extracted text
            extractedText = cleanText(extractedText);
            
            if (extractedText.isEmpty()) {
                throw new IOException("No text content could be extracted from the document");
            }
            
            log.info("✅ Successfully extracted {} characters from {}", extractedText.length(), filename);
            return extractedText;
            
        } catch (Exception e) {
            log.error("❌ Error extracting text from {}: {}", filename, e.getMessage());
            throw new IOException("Failed to extract text from document: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from PDF file
     */
    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            log.error("❌ Failed to extract text from PDF: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Extract text from DOCX file
     */
    private String extractTextFromDOCX(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            
            return extractor.getText();
            
        } catch (IOException e) {
            log.error("❌ Failed to extract text from DOCX: {}", e.getMessage(), e);
            throw new IOException("Failed to extract text from DOCX: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate file type
     */
    private void validateFileType(String extension, String contentType) throws IOException {
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new IOException("Unsupported file extension: " + extension + 
                ". Supported formats: " + String.join(", ", SUPPORTED_EXTENSIONS));
        }
        
        // Additional content type validation (optional, as browsers may send different MIME types)
        if (contentType != null && !contentType.isEmpty()) {
            boolean validContentType = SUPPORTED_CONTENT_TYPES.stream()
                .anyMatch(supportedType -> contentType.toLowerCase().contains(supportedType.toLowerCase()));
            
            if (!validContentType) {
                log.warn("⚠️ Unexpected content type: {} for extension: {}", contentType, extension);
                // Don't throw error, just log warning as content type detection can be unreliable
            }
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
        
        // Remove common document artifacts
        text = text.replaceAll("\\u00A0", " "); // Non-breaking space
        text = text.replaceAll("\\u2000-\\u200F", " "); // Various Unicode spaces
        text = text.replaceAll("\\u2028-\\u202F", " "); // Line and paragraph separators
        
        return text.trim();
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * Check if file type is supported
     */
    public boolean isSupportedFileType(String filename) {
        if (filename == null) {
            return false;
        }
        String extension = getFileExtension(filename).toLowerCase();
        return SUPPORTED_EXTENSIONS.contains(extension);
    }
    
    /**
     * Get list of supported file extensions
     */
    public List<String> getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS;
    }
    
    /**
     * Get supported file types description for user
     */
    public String getSupportedFileTypesDescription() {
        return "Supported formats: PDF (.pdf), Word Document (.docx)";
    }
}