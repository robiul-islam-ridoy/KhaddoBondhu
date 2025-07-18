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
    
    // Firebase Configuration
    public static final String FIREBASE_PROJECT_ID = "khaddobondhu-86011";
    
    // App Configuration
    public static final String APP_NAME = "KhaddoBondhu";
    public static final String APP_VERSION = "1.0.0";
    
    // Feature Flags
    public static final boolean ENABLE_LOCATION_SERVICES = true;
    public static final boolean ENABLE_PUSH_NOTIFICATIONS = true;
    public static final boolean ENABLE_ANALYTICS = true;
} 