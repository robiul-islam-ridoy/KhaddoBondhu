package com.example.khaddobondhu.model;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String userId; // Recipient of the notification
    private String title;
    private String message;
    private String type; // REQUEST_RECEIVED, REQUEST_ACCEPTED, REQUEST_DECLINED, MESSAGE, GENERAL
    private String relatedId; // ID of related item (request ID, post ID, etc.)
    private boolean isRead;
    private Timestamp createdAt;
    private String senderId; // ID of the user who triggered the notification
    private String senderName;
    private String senderProfilePictureUrl;

    // Default constructor for Firestore
    public Notification() {
        this.isRead = false;
        this.createdAt = Timestamp.now();
    }

    public Notification(String userId, String title, String message, String type, String relatedId, String senderId, String senderName, String senderProfilePictureUrl) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedId = relatedId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfilePictureUrl = senderProfilePictureUrl;
        this.isRead = false;
        this.createdAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderProfilePictureUrl() {
        return senderProfilePictureUrl;
    }

    public void setSenderProfilePictureUrl(String senderProfilePictureUrl) {
        this.senderProfilePictureUrl = senderProfilePictureUrl;
    }

    // Helper methods
    public String getTimeAgo() {
        if (createdAt == null) return "Just now";
        
        long now = System.currentTimeMillis();
        long created = createdAt.toDate().getTime();
        long diff = now - created;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }

    public int getNotificationIcon() {
        switch (type) {
            case "REQUEST_RECEIVED":
                return com.example.khaddobondhu.R.drawable.ic_request;
            case "REQUEST_ACCEPTED":
                return com.example.khaddobondhu.R.drawable.ic_check;
            case "REQUEST_DECLINED":
                return com.example.khaddobondhu.R.drawable.ic_close;
            // MESSAGE type removed
            default:
                return com.example.khaddobondhu.R.drawable.ic_notification;
        }
    }
}
