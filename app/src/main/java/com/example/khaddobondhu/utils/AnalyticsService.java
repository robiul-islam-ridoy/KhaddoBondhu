package com.example.khaddobondhu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.khaddobondhu.model.FoodPost;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsService {
    
    private static final String TAG = "AnalyticsService";
    private static final String PREF_NAME = "KhaddoBondhuAnalytics";
    private static final String KEY_EVENTS = "analytics_events";
    private static final String KEY_USER_STATS = "user_statistics";
    private static final String KEY_APP_STATS = "app_statistics";
    
    private final Context context;
    private final SharedPreferences preferences;
    private final Gson gson;
    private final ExecutorService executor;
    
    // Event tracking
    private final List<AnalyticsEvent> events;
    private final Map<String, Integer> eventCounts;
    
    // User statistics
    private UserStatistics userStats;
    private AppStatistics appStats;
    
    public AnalyticsService(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
        
        // Load existing data
        this.events = loadEvents();
        this.eventCounts = calculateEventCounts();
        this.userStats = loadUserStatistics();
        this.appStats = loadAppStatistics();
    }
    
    // Event Tracking
    public void trackEvent(String eventName, Map<String, Object> parameters) {
        AnalyticsEvent event = new AnalyticsEvent(eventName, parameters, System.currentTimeMillis());
        events.add(event);
        
        // Update event counts
        eventCounts.put(eventName, eventCounts.getOrDefault(eventName, 0) + 1);
        
        // Save to disk asynchronously
        executor.execute(() -> saveEvents());
        
        Log.d(TAG, "Tracked event: " + eventName);
    }
    
    public void trackFoodPostCreated(String foodType, String postType, double price) {
        Map<String, Object> params = new HashMap<>();
        params.put("food_type", foodType);
        params.put("post_type", postType);
        params.put("price", price);
        
        trackEvent("food_post_created", params);
        updateUserStats("posts_created", 1);
    }
    
    public void trackFoodPostViewed(String postId, String foodType) {
        Map<String, Object> params = new HashMap<>();
        params.put("post_id", postId);
        params.put("food_type", foodType);
        
        trackEvent("food_post_viewed", params);
        updateUserStats("posts_viewed", 1);
    }
    
    public void trackSearchPerformed(String query, String filterType) {
        Map<String, Object> params = new HashMap<>();
        params.put("search_query", query);
        params.put("filter_type", filterType);
        
        trackEvent("search_performed", params);
        updateUserStats("searches_performed", 1);
    }
    
    // Messaging removed: trackMessageSent no longer used
    
    public void trackUserRegistration(String registrationMethod) {
        Map<String, Object> params = new HashMap<>();
        params.put("registration_method", registrationMethod);
        
        trackEvent("user_registration", params);
        updateAppStats("total_registrations", 1);
    }
    
    public void trackUserLogin(String loginMethod) {
        Map<String, Object> params = new HashMap<>();
        params.put("login_method", loginMethod);
        
        trackEvent("user_login", params);
        updateUserStats("logins", 1);
    }
    
    public void trackAppLaunch() {
        trackEvent("app_launch", new HashMap<>());
        updateAppStats("app_launches", 1);
    }
    
    public void trackError(String errorType, String errorMessage) {
        Map<String, Object> params = new HashMap<>();
        params.put("error_type", errorType);
        params.put("error_message", errorMessage);
        
        trackEvent("app_error", params);
        updateAppStats("total_errors", 1);
    }
    
    public void trackFoodWasteImpact(int quantity, String unit) {
        Map<String, Object> params = new HashMap<>();
        params.put("quantity", quantity);
        params.put("unit", unit);
        
        trackEvent("food_waste_prevented", params);
        updateAppStats("total_food_saved", quantity);
    }
    
    // User Statistics
    private void updateUserStats(String metric, int increment) {
        if (userStats == null) {
            userStats = new UserStatistics();
        }
        
        switch (metric) {
            case "posts_created":
                userStats.postsCreated += increment;
                break;
            case "posts_viewed":
                userStats.postsViewed += increment;
                break;
            case "searches_performed":
                userStats.searchesPerformed += increment;
                break;
            case "logins":
                userStats.logins += increment;
                break;
        }
        
        saveUserStatistics();
    }
    
    // App Statistics
    private void updateAppStats(String metric, int increment) {
        if (appStats == null) {
            appStats = new AppStatistics();
        }
        
        switch (metric) {
            case "total_registrations":
                appStats.totalRegistrations += increment;
                break;
            case "app_launches":
                appStats.appLaunches += increment;
                break;
            case "total_errors":
                appStats.totalErrors += increment;
                break;
            case "total_food_saved":
                appStats.totalFoodSaved += increment;
                break;
        }
        
        saveAppStatistics();
    }
    
    // Data Retrieval
    public List<AnalyticsEvent> getEvents() {
        return new ArrayList<>(events);
    }
    
    public Map<String, Integer> getEventCounts() {
        return new HashMap<>(eventCounts);
    }
    
    public UserStatistics getUserStatistics() {
        return userStats != null ? userStats.clone() : new UserStatistics();
    }
    
    public AppStatistics getAppStatistics() {
        return appStats != null ? appStats.clone() : new AppStatistics();
    }
    
    public List<AnalyticsEvent> getEventsByType(String eventType) {
        List<AnalyticsEvent> filteredEvents = new ArrayList<>();
        for (AnalyticsEvent event : events) {
            if (eventType.equals(event.getEventName())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }
    
    public List<AnalyticsEvent> getEventsInTimeRange(long startTime, long endTime) {
        List<AnalyticsEvent> filteredEvents = new ArrayList<>();
        for (AnalyticsEvent event : events) {
            if (event.getTimestamp() >= startTime && event.getTimestamp() <= endTime) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }
    
    // Data Export
    public String exportAnalyticsData() {
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("events", events);
        exportData.put("event_counts", eventCounts);
        exportData.put("user_statistics", userStats);
        exportData.put("app_statistics", appStats);
        exportData.put("export_timestamp", System.currentTimeMillis());
        
        return gson.toJson(exportData);
    }
    
    // Data Management
    public void clearAnalyticsData() {
        events.clear();
        eventCounts.clear();
        userStats = new UserStatistics();
        appStats = new AppStatistics();
        
        preferences.edit().clear().apply();
    }
    
    public void cleanupOldEvents(long maxAge) {
        long currentTime = System.currentTimeMillis();
        events.removeIf(event -> (currentTime - event.getTimestamp()) > maxAge);
        saveEvents();
    }
    
    // Private helper methods
    private List<AnalyticsEvent> loadEvents() {
        String json = preferences.getString(KEY_EVENTS, "[]");
        try {
            Type type = new TypeToken<List<AnalyticsEvent>>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error loading events", e);
            return new ArrayList<>();
        }
    }
    
    private Map<String, Integer> calculateEventCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (AnalyticsEvent event : events) {
            counts.put(event.getEventName(), counts.getOrDefault(event.getEventName(), 0) + 1);
        }
        return counts;
    }
    
    private UserStatistics loadUserStatistics() {
        String json = preferences.getString(KEY_USER_STATS, null);
        if (json != null) {
            try {
                return gson.fromJson(json, UserStatistics.class);
            } catch (Exception e) {
                Log.e(TAG, "Error loading user statistics", e);
            }
        }
        return new UserStatistics();
    }
    
    private AppStatistics loadAppStatistics() {
        String json = preferences.getString(KEY_APP_STATS, null);
        if (json != null) {
            try {
                return gson.fromJson(json, AppStatistics.class);
            } catch (Exception e) {
                Log.e(TAG, "Error loading app statistics", e);
            }
        }
        return new AppStatistics();
    }
    
    private void saveEvents() {
        String json = gson.toJson(events);
        preferences.edit().putString(KEY_EVENTS, json).apply();
    }
    
    private void saveUserStatistics() {
        if (userStats != null) {
            String json = gson.toJson(userStats);
            preferences.edit().putString(KEY_USER_STATS, json).apply();
        }
    }
    
    private void saveAppStatistics() {
        if (appStats != null) {
            String json = gson.toJson(appStats);
            preferences.edit().putString(KEY_APP_STATS, json).apply();
        }
    }
    
    // Data classes
    public static class AnalyticsEvent {
        private String eventName;
        private Map<String, Object> parameters;
        private long timestamp;
        
        public AnalyticsEvent(String eventName, Map<String, Object> parameters, long timestamp) {
            this.eventName = eventName;
            this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getEventName() { return eventName; }
        public Map<String, Object> getParameters() { return new HashMap<>(parameters); }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class UserStatistics {
        public int postsCreated = 0;
        public int postsViewed = 0;
        public int searchesPerformed = 0;
        public int logins = 0;
        
        public UserStatistics clone() {
            UserStatistics clone = new UserStatistics();
            clone.postsCreated = this.postsCreated;
            clone.postsViewed = this.postsViewed;
            clone.searchesPerformed = this.searchesPerformed;
            clone.logins = this.logins;
            return clone;
        }
    }
    
    public static class AppStatistics {
        public int totalRegistrations = 0;
        public int appLaunches = 0;
        public int totalErrors = 0;
        public int totalFoodSaved = 0;
        
        public AppStatistics clone() {
            AppStatistics clone = new AppStatistics();
            clone.totalRegistrations = this.totalRegistrations;
            clone.appLaunches = this.appLaunches;
            clone.totalErrors = this.totalErrors;
            clone.totalFoodSaved = this.totalFoodSaved;
            return clone;
        }
    }
} 