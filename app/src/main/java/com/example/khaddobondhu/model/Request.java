package com.example.khaddobondhu.model;

import com.google.firebase.Timestamp;

public class Request {
    private String id;
    private String postId;
    private String postTitle;
    private String requesterId;
    private String requesterName;
    private String requesterProfilePictureUrl;
    private String postOwnerId;
    private String postOwnerName;
    private String status; // PENDING, ACCEPTED, DECLINED
    private String requestType; // REQUEST_TO_GET, REQUEST_TO_BUY, WANT_TO_DONATE, WANT_TO_SELL
    private String message;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor for Firestore
    public Request() {}

    public Request(String postId, String postTitle, String requesterId, String requesterName, 
                   String requesterProfilePictureUrl, String postOwnerId, String postOwnerName, 
                   String requestType, String message) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.requesterProfilePictureUrl = requesterProfilePictureUrl;
        this.postOwnerId = postOwnerId;
        this.postOwnerName = postOwnerName;
        this.requestType = requestType;
        this.message = message;
        this.status = "PENDING";
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterProfilePictureUrl() {
        return requesterProfilePictureUrl;
    }

    public void setRequesterProfilePictureUrl(String requesterProfilePictureUrl) {
        this.requesterProfilePictureUrl = requesterProfilePictureUrl;
    }

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public String getPostOwnerName() {
        return postOwnerName;
    }

    public void setPostOwnerName(String postOwnerName) {
        this.postOwnerName = postOwnerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isAccepted() {
        return "ACCEPTED".equals(status);
    }

    public boolean isDeclined() {
        return "DECLINED".equals(status);
    }

    public String getStatusDisplayText() {
        switch (status) {
            case "PENDING":
                return "Pending";
            case "ACCEPTED":
                return "Accepted";
            case "DECLINED":
                return "Declined";
            default:
                return status;
        }
    }

    public String getRequestTypeDisplayText() {
        switch (requestType) {
            case "REQUEST_TO_GET":
                return "Request to Get";
            case "REQUEST_TO_BUY":
                return "Request to Buy";
            case "WANT_TO_DONATE":
                return "Want to Donate";
            case "WANT_TO_SELL":
                return "Want to Sell";
            default:
                return requestType;
        }
    }
}
