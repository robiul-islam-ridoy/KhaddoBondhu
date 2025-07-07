package com.example.khaddobondhu;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class KhaddoBondhuApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
} 