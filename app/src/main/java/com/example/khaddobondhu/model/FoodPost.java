package com.example.khaddobondhu.model;

public class FoodPost {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String postType; // "DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"
    private double price;
    private int quantity;
    private String distance;
    private String timeLeft;
    private String userId;
    private String userName;

    public FoodPost(String id, String title, String description, String imageUrl, String postType,
                   double price, int quantity, String distance, String timeLeft, String userId, String userName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.postType = postType;
        this.price = price;
        this.quantity = quantity;
        this.distance = distance;
        this.timeLeft = timeLeft;
        this.userId = userId;
        this.userName = userName;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getPostType() { return postType; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getDistance() { return distance; }
    public String getTimeLeft() { return timeLeft; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
} 