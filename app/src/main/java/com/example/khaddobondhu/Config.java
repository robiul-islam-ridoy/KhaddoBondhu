package com.example.khaddobondhu;

/**
 * Configuration helper class to access API keys and other configuration values
 */
public class Config {
    
    // Cloudinary Configuration
    public static final String CLOUDINARY_CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME;
    public static final String CLOUDINARY_API_KEY = BuildConfig.CLOUDINARY_API_KEY;
    public static final String CLOUDINARY_API_SECRET = BuildConfig.CLOUDINARY_API_SECRET;
    
    // Google Maps Configuration
    public static final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
    
    // User Role Constants
    public static final String USER_TYPE_INDIVIDUAL = "INDIVIDUAL";
    public static final String USER_TYPE_RESTAURANT = "RESTAURANT";
    public static final String USER_TYPE_NGO = "NGO";
    
    // User Role Display Names
    public static final String USER_TYPE_INDIVIDUAL_DISPLAY = "Individual";
    public static final String USER_TYPE_RESTAURANT_DISPLAY = "Restaurant";
    public static final String USER_TYPE_NGO_DISPLAY = "NGO/Charity";
    
    // User Role Descriptions
    public static final String USER_TYPE_INDIVIDUAL_DESC = "Regular user who shares or receives food";
    public static final String USER_TYPE_RESTAURANT_DESC = "Restaurant that donates or sells surplus food";
    public static final String USER_TYPE_NGO_DESC = "Non-profit organization helping the community";
    
    // Firebase Configuration
    public static final String FIREBASE_PROJECT_ID = "test-49640";
    
    // App Configuration
    public static final String APP_NAME = "KhaddoBondhu";
    public static final String APP_VERSION = "1.0.0";
    
    // Feature Flags
    public static final boolean ENABLE_LOCATION_SERVICES = true;
    public static final boolean ENABLE_PUSH_NOTIFICATIONS = true;
    public static final boolean ENABLE_ANALYTICS = true;
} 