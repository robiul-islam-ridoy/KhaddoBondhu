package com.example.khaddobondhu.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class FoodPost {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String postType; // DONATE, SELL, REQUEST_DONATION, REQUEST_TO_BUY
    private double price;
    private int quantity;
    private String quantityUnit;
    private String foodType;
    private String pickupLocation;
    private List<String> imageUrls;
    private Timestamp createdAt;
    private Timestamp expiryDate;
    private boolean isActive;
    private int views;
    private int requests;
    private String status; // "ACTIVE", "RESERVED", "COMPLETED", "EXPIRED"
    private double latitude;
    private double longitude;
    private boolean isVerified;
    private String contactPhone;
    private String contactEmail;
    private boolean allowContact;

    // Default constructor
    public FoodPost() {
        this.isActive = true;
        this.views = 0;
        this.requests = 0;
    }

    // Constructor with all fields
    public FoodPost(String userId, String title, String description, String postType, 
                   double price, int quantity, String quantityUnit, String foodType, 
                   String pickupLocation, Timestamp expiryDate) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.postType = postType;
        this.price = price;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.foodType = foodType;
        this.pickupLocation = pickupLocation;
        this.expiryDate = expiryDate;
        this.isActive = true;
        this.views = 0;
        this.requests = 0;
        this.status = "ACTIVE";
        this.isVerified = false;
        this.allowContact = true;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPostType() { return postType; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getQuantityUnit() { return quantityUnit; }
    public String getFoodType() { return foodType; }
    public String getPickupLocation() { return pickupLocation; }
    public List<String> getImageUrls() { return imageUrls; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getExpiryDate() { return expiryDate; }
    public boolean isActive() { return isActive; }
    public String getStatus() { return status; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public int getViews() { return views; }
    public int getRequests() { return requests; }
    public boolean isVerified() { return isVerified; }
    public String getContactPhone() { return contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public boolean isAllowContact() { return allowContact; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPostType(String postType) { this.postType = postType; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setQuantityUnit(String quantityUnit) { this.quantityUnit = quantityUnit; }
    public void setFoodType(String foodType) { this.foodType = foodType; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setExpiryDate(Timestamp expiryDate) { this.expiryDate = expiryDate; }
    public void setActive(boolean active) { isActive = active; }
    public void setStatus(String status) { this.status = status; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setAllowContact(boolean allowContact) { this.allowContact = allowContact; }

    // Helper methods
    public String getFormattedPrice() {
        if (postType.equals("DONATE") || postType.equals("REQUEST_DONATION")) {
            return "Free";
        } else {
            return "৳" + String.format("%.0f", price);
        }
    }

    public String getFormattedQuantity() {
        return quantity + " " + quantityUnit;
    }

    public String getFormattedExpiry() {
        if (expiryDate == null) {
            return "No expiry";
        }
        
        long now = System.currentTimeMillis();
        long expiry = expiryDate.toDate().getTime();
        long diff = expiry - now;
        
        if (diff <= 0) {
            return "Expired";
        }
        
        long hours = diff / (1000 * 60 * 60);
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " left";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " left";
        } else {
            return "Less than 1 hour left";
        }
    }

    public boolean isExpired() {
        if (expiryDate == null) return false;
        return System.currentTimeMillis() > expiryDate.toDate().getTime();
    }

    public void incrementViews() {
        this.views++;
    }

    public void incrementRequests() {
        this.requests++;
    }
} 