package com.example.khaddobondhu.utils;

import android.text.TextUtils;
import android.util.Patterns;
import java.util.regex.Pattern;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.User;
import java.util.regex.Pattern;

public class SecurityUtils {
    
    // Input validation patterns
    private static final Pattern PRICE_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Validation limits
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_LOCATION_LENGTH = 200;
    private static final double MAX_PRICE = 10000.0;
    private static final int MAX_QUANTITY = 1000;
    
    /**
     * Validates a FoodPost object for security and data integrity
     */
    public static ValidationResult validateFoodPost(FoodPost post) {
        if (post == null) {
            return new ValidationResult(false, "Post cannot be null");
        }
        
        // Validate title
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            return new ValidationResult(false, "Title is required");
        }
        if (post.getTitle().length() > MAX_TITLE_LENGTH) {
            return new ValidationResult(false, "Title too long (max " + MAX_TITLE_LENGTH + " characters)");
        }
        
        // Validate description
        if (post.getDescription() != null && !post.getDescription().trim().isEmpty() && post.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            return new ValidationResult(false, "Description too long (max " + MAX_DESCRIPTION_LENGTH + " characters)");
        }
        
        // Validate price
        if (post.getPrice() < 0) {
            return new ValidationResult(false, "Price cannot be negative");
        }
        if (post.getPrice() > MAX_PRICE) {
            return new ValidationResult(false, "Price too high (max " + MAX_PRICE + ")");
        }
        
        // Validate quantity
        if (post.getQuantity() <= 0) {
            return new ValidationResult(false, "Quantity must be greater than 0");
        }
        if (post.getQuantity() > MAX_QUANTITY) {
            return new ValidationResult(false, "Quantity too high (max " + MAX_QUANTITY + ")");
        }
        
        // Validate post type
        if (post.getPostType() == null || post.getPostType().trim().isEmpty()) {
            return new ValidationResult(false, "Post type is required");
        }
        
        // Validate location
        if (post.getPickupLocation() != null && !post.getPickupLocation().trim().isEmpty() && post.getPickupLocation().length() > MAX_LOCATION_LENGTH) {
            return new ValidationResult(false, "Location too long (max " + MAX_LOCATION_LENGTH + " characters)");
        }
        
        return new ValidationResult(true, "Valid");
    }
    
    /**
     * Validates user input for security
     */
    public static ValidationResult validateUserInput(User user) {
        if (user == null) {
            return new ValidationResult(false, "User cannot be null");
        }
        
        // Validate name
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return new ValidationResult(false, "Name is required");
        }
        if (user.getName().length() > 50) {
            return new ValidationResult(false, "Name too long (max 50 characters)");
        }
        
        // Validate email
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return new ValidationResult(false, "Invalid email format");
        }
        
        // Validate phone
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty() && !PHONE_PATTERN.matcher(user.getPhoneNumber()).matches()) {
            return new ValidationResult(false, "Invalid phone number format");
        }
        
        return new ValidationResult(true, "Valid");
    }
    
    /**
     * Sanitizes user input to prevent XSS and injection attacks
     */
    public static String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }
        
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"']", "")
                   .replaceAll("javascript:", "")
                   .replaceAll("on\\w+", "")
                   .trim();
    }
    
    /**
     * Validates image URLs for security
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        // Only allow HTTPS URLs
        if (!url.startsWith("https://")) {
            return false;
        }
        
        // Check for valid image extensions
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") || 
               lowerUrl.endsWith(".jpeg") || 
               lowerUrl.endsWith(".png") || 
               lowerUrl.endsWith(".webp") ||
               lowerUrl.contains("cloudinary.com");
    }
    
    /**
     * Rate limiting check (simple implementation)
     */
    public static boolean checkRateLimit(String userId, String action, long lastActionTime) {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastActionTime;
        
        // Different limits for different actions
        switch (action) {
            case "create_post":
                return timeDiff >= 30000; // 30 seconds between posts
            case "send_message":
                return timeDiff >= 5000;  // 5 seconds between messages
            case "search":
                return timeDiff >= 1000;  // 1 second between searches
            default:
                return timeDiff >= 1000;  // Default 1 second
        }
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String message;
        
        public ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getMessage() {
            return message;
        }
    }
} 