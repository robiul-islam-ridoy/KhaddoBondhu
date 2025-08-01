package com.example.khaddobondhu.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import java.util.List;

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private boolean isVerified;
    private String userType; // "INDIVIDUAL", "RESTAURANT", "NGO"
    private int rating;
    private int totalRatings;
    private List<String> badges;
    private Timestamp createdAt;
    private Timestamp lastActive;
    private int totalPosts;
    private int totalDonations;
    private int totalReceived;
    private String profilePictureUrl;

    // Default constructor for Firebase
    public User() {
        this.userType = "INDIVIDUAL"; // Default user type
        this.rating = 0;
        this.totalRatings = 0;
        this.totalPosts = 0;
        this.totalDonations = 0;
        this.totalReceived = 0;
        this.isVerified = false;
    }

    public User(String id, String name, String email, String phone, String bio, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phone;
        this.description = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.createdAt = Timestamp.now();
        this.lastActive = Timestamp.now();
        this.isVerified = false;
        this.userType = "INDIVIDUAL";
        this.rating = 0;
        this.totalRatings = 0;
        this.totalPosts = 0;
        this.totalDonations = 0;
        this.totalReceived = 0;
    }

    // Legacy constructor for demo/test users
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = "";
        this.description = "";
        this.profilePictureUrl = "";
        this.createdAt = null;
        this.lastActive = null;
        this.isVerified = false;
        this.userType = "INDIVIDUAL";
        this.rating = 0;
        this.totalRatings = 0;
        this.totalPosts = 0;
        this.totalDonations = 0;
        this.totalReceived = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isVerified() { return isVerified; }
    public String getUserType() { return userType; }
    public int getRating() { return rating; }
    public int getTotalRatings() { return totalRatings; }
    public List<String> getBadges() { return badges; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getLastActive() { return lastActive; }
    public int getTotalPosts() { return totalPosts; }
    public int getTotalDonations() { return totalDonations; }
    public int getTotalReceived() { return totalReceived; }
    public String getProfilePictureUrl() { return profilePictureUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setRating(int rating) { this.rating = rating; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }
    public void setBadges(List<String> badges) { this.badges = badges; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setLastActive(Timestamp lastActive) { this.lastActive = lastActive; }
    public void setTotalPosts(int totalPosts) { this.totalPosts = totalPosts; }
    public void setTotalDonations(int totalDonations) { this.totalDonations = totalDonations; }
    public void setTotalReceived(int totalReceived) { this.totalReceived = totalReceived; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    // Helper methods
    public double getAverageRating() {
        return totalRatings > 0 ? (double) rating / totalRatings : 0.0;
    }

    public void updateLastActive() {
        this.lastActive = Timestamp.now();
    }
} 