package com.example.khaddobondhu;

import android.app.Application;
import android.content.Context;
import com.example.khaddobondhu.utils.AnalyticsService;
import com.example.khaddobondhu.utils.CacheManager;
import com.example.khaddobondhu.utils.ErrorHandler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class KhaddoBondhuApplication extends Application {
    
    private static Context context;
    private static CacheManager cacheManager;
    private static AnalyticsService analyticsService;
    
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Initialize Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        
        // Initialize utilities
        initializeUtilities();
        
        // Track app launch
        if (analyticsService != null) {
            analyticsService.trackAppLaunch();
        }
    }
    
    private void initializeUtilities() {
        try {
            // Initialize cache manager
            cacheManager = new CacheManager(this);
            
            // Initialize analytics service
            analyticsService = new AnalyticsService(this);
            
        } catch (Exception e) {
            ErrorHandler.handleUnknownError(e, this);
        }
    }
    
    public static Context getAppContext() {
        return context;
    }
    
    public static CacheManager getCacheManager() {
        return cacheManager;
    }
    
    public static AnalyticsService getAnalyticsService() {
        return analyticsService;
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (cacheManager != null) {
            cacheManager.trimMemory();
        }
    }
    
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (cacheManager != null) {
            cacheManager.trimMemory();
        }
    }
} 