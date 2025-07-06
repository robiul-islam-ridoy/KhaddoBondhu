package com.example.khaddobondhu.model;

import com.google.firebase.Timestamp;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String messageType; // "TEXT", "IMAGE", "LOCATION"
    private String imageUrl;
    private double latitude;
    private double longitude;
    private boolean isRead;
    private Timestamp timestamp;
    private String chatId;

    // Default constructor for Firebase
    public Message() {}

    public Message(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.messageType = "TEXT";
        this.isRead = false;
        this.timestamp = Timestamp.now();
        this.chatId = generateChatId(senderId, receiverId);
    }

    public Message(String senderId, String receiverId, String content, String messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.messageType = messageType;
        this.isRead = false;
        this.timestamp = Timestamp.now();
        this.chatId = generateChatId(senderId, receiverId);
    }

    private String generateChatId(String userId1, String userId2) {
        // Create a consistent chat ID regardless of sender/receiver order
        return userId1.compareTo(userId2) < 0 ? 
            userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    // Getters
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public String getMessageType() { return messageType; }
    public String getImageUrl() { return imageUrl; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean isRead() { return isRead; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getChatId() { return chatId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setContent(String content) { this.content = content; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setRead(boolean read) { isRead = read; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public void setChatId(String chatId) { this.chatId = chatId; }
} 