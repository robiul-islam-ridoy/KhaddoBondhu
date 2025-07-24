package com.example.khaddobondhu.utils;

import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.User;
import com.google.firebase.Timestamp;
import org.junit.Test;
import static org.junit.Assert.*;

public class SecurityUtilsTest {
    
    @Test
    public void testValidateFoodPost_ValidPost() {
        FoodPost post = new FoodPost();
        post.setTitle("Test Food");
        post.setDescription("Delicious test food");
        post.setPostType("DONATE");
        post.setPrice(0.0);
        post.setQuantity(5);
        post.setQuantityUnit("pieces");
        post.setFoodType("Snacks");
        post.setPickupLocation("Test Location");
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(post);
        assertTrue("Valid post should pass validation", result.isValid());
    }
    
    @Test
    public void testValidateFoodPost_NullPost() {
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(null);
        assertFalse("Null post should fail validation", result.isValid());
        assertEquals("Post cannot be null", result.getMessage());
    }
    
    @Test
    public void testValidateFoodPost_EmptyTitle() {
        FoodPost post = new FoodPost();
        post.setTitle("");
        post.setPostType("DONATE");
        post.setPrice(0.0);
        post.setQuantity(5);
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(post);
        assertFalse("Post with empty title should fail validation", result.isValid());
        assertEquals("Title is required", result.getMessage());
    }
    
    @Test
    public void testValidateFoodPost_TooLongTitle() {
        FoodPost post = new FoodPost();
        post.setTitle("A".repeat(101)); // 101 characters
        post.setPostType("DONATE");
        post.setPrice(0.0);
        post.setQuantity(5);
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(post);
        assertFalse("Post with too long title should fail validation", result.isValid());
        assertTrue("Should mention max length", result.getMessage().contains("100"));
    }
    
    @Test
    public void testValidateFoodPost_NegativePrice() {
        FoodPost post = new FoodPost();
        post.setTitle("Test Food");
        post.setPostType("SELL");
        post.setPrice(-10.0);
        post.setQuantity(5);
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(post);
        assertFalse("Post with negative price should fail validation", result.isValid());
        assertEquals("Price cannot be negative", result.getMessage());
    }
    
    @Test
    public void testValidateFoodPost_ZeroQuantity() {
        FoodPost post = new FoodPost();
        post.setTitle("Test Food");
        post.setPostType("DONATE");
        post.setPrice(0.0);
        post.setQuantity(0);
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateFoodPost(post);
        assertFalse("Post with zero quantity should fail validation", result.isValid());
        assertEquals("Quantity must be greater than 0", result.getMessage());
    }
    
    @Test
    public void testValidateUserInput_ValidUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("+1234567890");
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateUserInput(user);
        assertTrue("Valid user should pass validation", result.isValid());
    }
    
    @Test
    public void testValidateUserInput_EmptyName() {
        User user = new User();
        user.setName("");
        user.setEmail("john@example.com");
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateUserInput(user);
        assertFalse("User with empty name should fail validation", result.isValid());
        assertEquals("Name is required", result.getMessage());
    }
    
    @Test
    public void testValidateUserInput_InvalidEmail() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("invalid-email");
        
        SecurityUtils.ValidationResult result = SecurityUtils.validateUserInput(user);
        assertFalse("User with invalid email should fail validation", result.isValid());
        assertEquals("Invalid email format", result.getMessage());
    }
    
    @Test
    public void testSanitizeInput_NormalInput() {
        String input = "Hello World";
        String sanitized = SecurityUtils.sanitizeInput(input);
        assertEquals("Normal input should remain unchanged", input, sanitized);
    }
    
    @Test
    public void testSanitizeInput_WithHtmlTags() {
        String input = "<script>alert('xss')</script>Hello";
        String sanitized = SecurityUtils.sanitizeInput(input);
        assertFalse("HTML tags should be removed", sanitized.contains("<script>"));
        assertTrue("Text content should remain", sanitized.contains("Hello"));
    }
    
    @Test
    public void testSanitizeInput_WithJavaScript() {
        String input = "javascript:alert('xss')";
        String sanitized = SecurityUtils.sanitizeInput(input);
        assertFalse("JavaScript should be removed", sanitized.contains("javascript:"));
    }
    
    @Test
    public void testSanitizeInput_NullInput() {
        String sanitized = SecurityUtils.sanitizeInput(null);
        assertNull("Null input should return null", sanitized);
    }
    
    @Test
    public void testSanitizeInput_EmptyInput() {
        String sanitized = SecurityUtils.sanitizeInput("");
        assertEquals("Empty input should return empty string", "", sanitized);
    }
    
    @Test
    public void testIsValidImageUrl_ValidUrls() {
        assertTrue("HTTPS URL should be valid", 
            SecurityUtils.isValidImageUrl("https://example.com/image.jpg"));
        assertTrue("Cloudinary URL should be valid", 
            SecurityUtils.isValidImageUrl("https://res.cloudinary.com/image.jpg"));
        assertTrue("PNG URL should be valid", 
            SecurityUtils.isValidImageUrl("https://example.com/image.png"));
        assertTrue("WebP URL should be valid", 
            SecurityUtils.isValidImageUrl("https://example.com/image.webp"));
    }
    
    @Test
    public void testIsValidImageUrl_InvalidUrls() {
        assertFalse("HTTP URL should be invalid", 
            SecurityUtils.isValidImageUrl("http://example.com/image.jpg"));
        assertFalse("Non-image URL should be invalid", 
            SecurityUtils.isValidImageUrl("https://example.com/document.pdf"));
        assertFalse("Null URL should be invalid", 
            SecurityUtils.isValidImageUrl(null));
        assertFalse("Empty URL should be invalid", 
            SecurityUtils.isValidImageUrl(""));
    }
    
    @Test
    public void testCheckRateLimit() {
        String userId = "test_user";
        String action = "create_post";
        long lastActionTime = System.currentTimeMillis() - 60000; // 1 minute ago
        
        assertTrue("Should allow action after sufficient time", 
            SecurityUtils.checkRateLimit(userId, action, lastActionTime));
        
        lastActionTime = System.currentTimeMillis() - 10000; // 10 seconds ago
        assertFalse("Should block action within rate limit", 
            SecurityUtils.checkRateLimit(userId, action, lastActionTime));
    }
} 