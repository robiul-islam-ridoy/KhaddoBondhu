package com.example.khaddobondhu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    
    private static final String PREF_NAME = "KhaddoBondhuCache";
    private static final String KEY_FOOD_POSTS = "food_posts";
    private static final String KEY_USER_PROFILE = "user_profile_";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final String KEY_LAST_UPDATE = "last_update";
    
    // Memory cache for food posts
    private final LruCache<String, FoodPost> postCache;
    private final LruCache<String, User> userCache;
    private final LruCache<String, Bitmap> imageCache;
    
    // Disk cache
    private final SharedPreferences preferences;
    private final Gson gson;
    
    // Search history
    private final List<String> searchHistory;
    
    // Cache statistics
    private int cacheHits = 0;
    private int cacheMisses = 0;
    
    public CacheManager(Context context) {
        // Initialize memory caches
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8; // Use 1/8th of available memory
        
        postCache = new LruCache<String, FoodPost>(cacheSize) {
            @Override
            protected int sizeOf(String key, FoodPost post) {
                // Rough estimate of memory usage
                return 1;
            }
        };
        
        userCache = new LruCache<String, User>(cacheSize / 2) {
            @Override
            protected int sizeOf(String key, User user) {
                return 1;
            }
        };
        
        imageCache = new LruCache<String, Bitmap>(cacheSize * 2) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
        
        // Initialize disk cache
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        
        // Load search history
        searchHistory = loadSearchHistory();
    }
    
    // Food Post Caching
    public void cacheFoodPost(String id, FoodPost post) {
        if (post != null && id != null) {
            postCache.put(id, post);
            saveFoodPostsToDisk();
        }
    }
    
    public FoodPost getFoodPost(String id) {
        FoodPost post = postCache.get(id);
        if (post != null) {
            cacheHits++;
        } else {
            cacheMisses++;
        }
        return post;
    }
    
    public void cacheFoodPosts(List<FoodPost> posts) {
        if (posts != null) {
            for (FoodPost post : posts) {
                if (post.getId() != null) {
                    postCache.put(post.getId(), post);
                }
            }
            saveFoodPostsToDisk();
        }
    }
    
    public List<FoodPost> getAllCachedFoodPosts() {
        List<FoodPost> posts = new ArrayList<>();
        for (String key : postCache.snapshot().keySet()) {
            FoodPost post = postCache.get(key);
            if (post != null) {
                posts.add(post);
            }
        }
        return posts;
    }
    
    public void removeFoodPost(String id) {
        postCache.remove(id);
        saveFoodPostsToDisk();
    }
    
    public void clearFoodPosts() {
        postCache.evictAll();
        preferences.edit().remove(KEY_FOOD_POSTS).apply();
    }
    
    // User Caching
    public void cacheUser(String id, User user) {
        if (user != null && id != null) {
            userCache.put(id, user);
            saveUserToDisk(id, user);
        }
    }
    
    public User getUser(String id) {
        User user = userCache.get(id);
        if (user == null) {
            user = loadUserFromDisk(id);
            if (user != null) {
                userCache.put(id, user);
            }
        }
        
        if (user != null) {
            cacheHits++;
        } else {
            cacheMisses++;
        }
        return user;
    }
    
    public void removeUser(String id) {
        userCache.remove(id);
        preferences.edit().remove(KEY_USER_PROFILE + id).apply();
    }
    
    // Image Caching
    public void cacheImage(String url, Bitmap bitmap) {
        if (bitmap != null && url != null) {
            imageCache.put(url, bitmap);
        }
    }
    
    public Bitmap getImage(String url) {
        Bitmap bitmap = imageCache.get(url);
        if (bitmap != null) {
            cacheHits++;
        } else {
            cacheMisses++;
        }
        return bitmap;
    }
    
    public void removeImage(String url) {
        imageCache.remove(url);
    }
    
    public void clearImages() {
        imageCache.evictAll();
    }
    
    // Search History
    public void addSearchQuery(String query) {
        if (query != null && !query.trim().isEmpty()) {
            searchHistory.remove(query); // Remove if exists
            searchHistory.add(0, query); // Add to beginning
            
            // Keep only last 20 searches
            if (searchHistory.size() > 20) {
                searchHistory.subList(20, searchHistory.size()).clear();
            }
            
            saveSearchHistory();
        }
    }
    
    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }
    
    public void clearSearchHistory() {
        searchHistory.clear();
        saveSearchHistory();
    }
    
    // Cache Management
    public void clearAllCaches() {
        postCache.evictAll();
        userCache.evictAll();
        imageCache.evictAll();
        searchHistory.clear();
        
        preferences.edit().clear().apply();
    }
    
    public void setLastUpdateTime(long timestamp) {
        preferences.edit().putLong(KEY_LAST_UPDATE, timestamp).apply();
    }
    
    public long getLastUpdateTime() {
        return preferences.getLong(KEY_LAST_UPDATE, 0);
    }
    
    public boolean isCacheStale(long maxAge) {
        long lastUpdate = getLastUpdateTime();
        return (System.currentTimeMillis() - lastUpdate) > maxAge;
    }
    
    // Cache Statistics
    public int getCacheHits() {
        return cacheHits;
    }
    
    public int getCacheMisses() {
        return cacheMisses;
    }
    
    public double getCacheHitRate() {
        int total = cacheHits + cacheMisses;
        return total > 0 ? (double) cacheHits / total : 0.0;
    }
    
    public int getPostCacheSize() {
        return postCache.size();
    }
    
    public int getUserCacheSize() {
        return userCache.size();
    }
    
    public int getImageCacheSize() {
        return imageCache.size();
    }
    
    // Private helper methods
    private void saveFoodPostsToDisk() {
        List<FoodPost> posts = getAllCachedFoodPosts();
        String json = gson.toJson(posts);
        preferences.edit().putString(KEY_FOOD_POSTS, json).apply();
    }
    
    private void saveUserToDisk(String id, User user) {
        String json = gson.toJson(user);
        preferences.edit().putString(KEY_USER_PROFILE + id, json).apply();
    }
    
    private User loadUserFromDisk(String id) {
        String json = preferences.getString(KEY_USER_PROFILE + id, null);
        if (json != null) {
            try {
                return gson.fromJson(json, User.class);
            } catch (Exception e) {
                // Remove corrupted data
                preferences.edit().remove(KEY_USER_PROFILE + id).apply();
            }
        }
        return null;
    }
    
    private List<String> loadSearchHistory() {
        String json = preferences.getString(KEY_SEARCH_HISTORY, "[]");
        try {
            Type type = new TypeToken<List<String>>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private void saveSearchHistory() {
        String json = gson.toJson(searchHistory);
        preferences.edit().putString(KEY_SEARCH_HISTORY, json).apply();
    }
    
    // Memory management
    public void trimMemory() {
        // Clear image cache first (most memory intensive)
        imageCache.trimToSize(imageCache.size() / 2);
        
        // Clear some user cache
        userCache.trimToSize(userCache.size() / 2);
        
        // Keep post cache as it's most important
        postCache.trimToSize(postCache.size() * 3 / 4);
    }
} 