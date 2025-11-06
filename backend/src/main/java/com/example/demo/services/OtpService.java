package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    
    // In-memory storage for OTPs (in production, use Redis or database)
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    // For demo purposes - in production, integrate with SMS service like Twilio
    private static final String DEMO_PHONE_NUMBER = "9341428236";
    
    public static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;
        
        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
        
        public String getOtp() { return otp; }
        public LocalDateTime getExpiryTime() { return expiryTime; }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }
    
    public String generateAndSendOtp(String adminId) {
        // Generate 6-digit OTP
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));
        
        // Set expiry time (5 minutes from now)
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
        
        // Store OTP
        otpStorage.put(adminId, new OtpData(otp, expiryTime));
        
        // Send SMS (simulate for now)
        sendSmsOtp(DEMO_PHONE_NUMBER, otp);
        
        log.info("OTP generated for admin: {} (Demo mode - OTP: {})", adminId, otp);
        return otp;
    }
    
    public boolean validateOtp(String adminId, String providedOtp) {
        OtpData otpData = otpStorage.get(adminId);
        
        if (otpData == null) {
            log.warn("No OTP found for admin: {}", adminId);
            return false;
        }
        
        if (otpData.isExpired()) {
            otpStorage.remove(adminId); // Clean up expired OTP
            log.warn("OTP expired for admin: {}", adminId);
            return false;
        }
        
        boolean isValid = otpData.getOtp().equals(providedOtp);
        
        if (isValid) {
            otpStorage.remove(adminId); // Clean up used OTP
            log.info("OTP validated successfully for admin: {}", adminId);
        } else {
            log.warn("Invalid OTP provided for admin: {}", adminId);
        }
        
        return isValid;
    }
    
    private void sendSmsOtp(String phoneNumber, String otp) {
        // TODO: Integrate with real SMS service (Twilio, AWS SNS, etc.)
        // For now, we'll just log it
        String message = String.format("Your admin login OTP is: %s. Valid for 5 minutes. Do not share with anyone.", otp);
        
        log.info("Sending SMS to {}: {}", phoneNumber, message);
        
        // Simulate SMS sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In production, implement actual SMS sending:
        /*
        // Example with Twilio:
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(FROM_PHONE_NUMBER),
                messageBody
        ).create();
        */
    }
    
    public void cleanupExpiredOtps() {
        // Clean up expired OTPs periodically
        otpStorage.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public boolean hasValidOtp(String adminId) {
        OtpData otpData = otpStorage.get(adminId);
        return otpData != null && !otpData.isExpired();
    }
    
    public LocalDateTime getOtpExpiry(String adminId) {
        OtpData otpData = otpStorage.get(adminId);
        return otpData != null ? otpData.getExpiryTime() : null;
    }
}