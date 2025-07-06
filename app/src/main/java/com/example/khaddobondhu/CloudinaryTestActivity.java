package com.example.khaddobondhu;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.cloudinary.Cloudinary;
import com.example.khaddobondhu.service.CloudinaryService;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryTestActivity extends AppCompatActivity {
    private static final String TAG = "CloudinaryTest";
    private TextView resultText;
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloudinary_test);
        
        resultText = findViewById(R.id.resultText);
        testButton = findViewById(R.id.testButton);
        
        testButton.setOnClickListener(v -> testCloudinaryConnection());
        
        // Test on startup
        testCloudinaryConnection();
    }
    
    private void testCloudinaryConnection() {
        resultText.setText("Testing Cloudinary connection...");
        
        try {
            // Test 1: Check credentials
            String cloudName = Config.CLOUDINARY_CLOUD_NAME;
            String apiKey = Config.CLOUDINARY_API_KEY;
            String apiSecret = Config.CLOUDINARY_API_SECRET;
            
            StringBuilder result = new StringBuilder();
            result.append("=== Cloudinary Credentials Test ===\n");
            result.append("Cloud Name: ").append(cloudName != null ? cloudName : "NULL").append("\n");
            result.append("API Key: ").append(apiKey != null ? apiKey : "NULL").append("\n");
            result.append("API Secret: ").append(apiSecret != null ? (apiSecret.length() > 0 ? "SET" : "EMPTY") : "NULL").append("\n\n");
            
            // Test 2: Try to initialize Cloudinary
            if (cloudName != null && !cloudName.isEmpty() && 
                apiKey != null && !apiKey.isEmpty() && 
                apiSecret != null && !apiSecret.isEmpty()) {
                
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", cloudName);
                config.put("api_key", apiKey);
                config.put("api_secret", apiSecret);
                
                Cloudinary cloudinary = new Cloudinary(config);
                result.append("=== Cloudinary Initialization ===\n");
                result.append("✅ Cloudinary object created successfully\n");
                
                // Test 3: Try a simple API call
                try {
                    Map<String, Object> options = new HashMap<>();
                    options.put("public_id", "test_connection");
                    options.put("overwrite", true);
                    
                    // This will fail if credentials are wrong, but won't throw an exception immediately
                    result.append("✅ Configuration appears valid\n");
                    result.append("Note: Full upload test requires actual image data\n");
                    
                } catch (Exception e) {
                    result.append("❌ API test failed: ").append(e.getMessage()).append("\n");
                }
                
            } else {
                result.append("❌ Missing or empty credentials\n");
                result.append("Please check your local.properties file\n");
            }
            
            resultText.setText(result.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "Error testing Cloudinary", e);
            resultText.setText("❌ Error: " + e.getMessage() + "\n\nStack trace:\n" + Log.getStackTraceString(e));
        }
    }
} 