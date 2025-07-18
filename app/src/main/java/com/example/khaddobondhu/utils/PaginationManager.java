package com.example.khaddobondhu.utils;

import com.example.khaddobondhu.model.FoodPost;
import java.util.List;

public class PaginationManager {
    
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 50;
    
    private int currentPage = 0;
    private int pageSize;
    private boolean hasMoreData = true;
    private boolean isLoading = false;
    private String lastDocumentId = null;
    
    public PaginationManager() {
        this(DEFAULT_PAGE_SIZE);
    }
    
    public PaginationManager(int pageSize) {
        this.pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
    }
    
    /**
     * Get the current page number
     */
    public int getCurrentPage() {
        return currentPage;
    }
    
    /**
     * Get the page size
     */
    public int getPageSize() {
        return pageSize;
    }
    
    /**
     * Check if there's more data to load
     */
    public boolean hasMoreData() {
        return hasMoreData;
    }
    
    /**
     * Check if currently loading data
     */
    public boolean isLoading() {
        return isLoading;
    }
    
    /**
     * Set loading state
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
    
    /**
     * Get the last document ID for cursor-based pagination
     */
    public String getLastDocumentId() {
        return lastDocumentId;
    }
    
    /**
     * Set the last document ID
     */
    public void setLastDocumentId(String lastDocumentId) {
        this.lastDocumentId = lastDocumentId;
    }
    
    /**
     * Reset pagination to first page
     */
    public void reset() {
        currentPage = 0;
        hasMoreData = true;
        lastDocumentId = null;
        isLoading = false;
    }
    
    /**
     * Increment page number
     */
    public void nextPage() {
        currentPage++;
    }
    
    /**
     * Set if there's more data available
     */
    public void setHasMoreData(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }
    
    /**
     * Check if the received data indicates there's more to load
     */
    public void checkForMoreData(List<FoodPost> receivedData) {
        if (receivedData == null || receivedData.size() < pageSize) {
            hasMoreData = false;
        }
        
        // Update last document ID for cursor-based pagination
        if (receivedData != null && !receivedData.isEmpty()) {
            FoodPost lastPost = receivedData.get(receivedData.size() - 1);
            lastDocumentId = lastPost.getId();
        }
    }
    
    /**
     * Get offset for offset-based pagination
     */
    public int getOffset() {
        return currentPage * pageSize;
    }
    
    /**
     * Check if this is the first page
     */
    public boolean isFirstPage() {
        return currentPage == 0;
    }
    
    /**
     * Get the total number of items loaded so far
     */
    public int getTotalLoaded() {
        return currentPage * pageSize;
    }
} 