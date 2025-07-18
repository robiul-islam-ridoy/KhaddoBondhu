package com.example.khaddobondhu.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.khaddobondhu.BuildConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CloudinaryService {
    private static final String TAG = "CloudinaryService";
    private Cloudinary cloudinary;
    private Context context;
    private ExecutorService executorService;

    // Cloudinary configuration
    private static final String CLOUD_NAME = com.example.khaddobondhu.Config.CLOUDINARY_CLOUD_NAME;
    private static final String API_KEY = com.example.khaddobondhu.Config.CLOUDINARY_API_KEY;
    private static final String API_SECRET = com.example.khaddobondhu.Config.CLOUDINARY_API_SECRET;

    public CloudinaryService(Context context) {
        this.context = context;
        this.executorService = Executors.newCachedThreadPool();
        initializeCloudinary();
    }

    private void initializeCloudinary() {
        try {
            Log.d(TAG, "CLOUD_NAME: '" + CLOUD_NAME + "'");
            Log.d(TAG, "API_KEY: '" + API_KEY + "'");
            Log.d(TAG, "API_SECRET: '" + API_SECRET + "'");
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", CLOUD_NAME);
            config.put("api_key", API_KEY);
            config.put("api_secret", API_SECRET);
            
            Log.d(TAG, "Initializing Cloudinary with cloud_name: " + CLOUD_NAME);
            Log.d(TAG, "API Key: " + API_KEY);
            Log.d(TAG, "API Secret: " + (API_SECRET != null ? API_SECRET.substring(0, Math.min(5, API_SECRET.length())) + "..." : "null"));
            
            // Check if credentials are empty
            if (CLOUD_NAME == null || CLOUD_NAME.isEmpty()) {
                Log.e(TAG, "Cloudinary cloud_name is null or empty!");
                cloudinary = null;
                return;
            }
            if (API_KEY == null || API_KEY.isEmpty()) {
                Log.e(TAG, "Cloudinary API key is null or empty!");
                cloudinary = null;
                return;
            }
            if (API_SECRET == null || API_SECRET.isEmpty()) {
                Log.e(TAG, "Cloudinary API secret is null or empty!");
                cloudinary = null;
                return;
            }
            
            cloudinary = new Cloudinary(config);
            Log.d(TAG, "Cloudinary initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Cloudinary", e);
            cloudinary = null;
        }
    }

    public void uploadImage(Uri imageUri, String folder, OnCompleteListener<String> listener) {
        executorService.execute(() -> {
            try {
                // Check if Cloudinary is initialized
                if (cloudinary == null) {
                    Log.e(TAG, "Cloudinary is not initialized. Cannot upload image.");
                    throw new RuntimeException("Cloudinary is not initialized. Please check your credentials.");
                }
                
                // Convert URI to bitmap
                Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), imageUri);
                
                // Convert bitmap to bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();

                // Upload parameters
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("public_id", folder + "/" + System.currentTimeMillis());
                uploadParams.put("overwrite", true);

                Log.d(TAG, "Starting Cloudinary upload with params: " + uploadParams);
                Log.d(TAG, "Image bytes length: " + imageBytes.length);
                
                // Synchronous upload
                Map<String, Object> result = cloudinary.uploader().upload(imageBytes, uploadParams);
                String imageUrl = (String) result.get("secure_url");
                Log.d(TAG, "Cloudinary upload result: " + result);
                
                Log.d(TAG, "Cloudinary upload successful: " + imageUrl);
                
                // Return success on main thread
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        listener.onComplete(new Task<String>() {
                            @Override public boolean isComplete() { return true; }
                            @Override public boolean isSuccessful() { return true; }
                            @Override public boolean isCanceled() { return false; }
                            @Override public String getResult() { return imageUrl; }
                            @Override public Exception getException() { return null; }
                            @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { return imageUrl; }
                            @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                            @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                            @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                        });
                    });
                } else {
                    // Fallback for non-Activity contexts
                    listener.onComplete(new Task<String>() {
                        @Override public boolean isComplete() { return true; }
                        @Override public boolean isSuccessful() { return true; }
                        @Override public boolean isCanceled() { return false; }
                        @Override public String getResult() { return imageUrl; }
                        @Override public Exception getException() { return null; }
                        @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { return imageUrl; }
                        @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                        @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                        @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error uploading image to Cloudinary", e);
                
                // Check for specific Cloudinary errors
                String errorMessage = e.getMessage();
                if (errorMessage != null) {
                    if (errorMessage.contains("cloud_name is disabled")) {
                        Log.e(TAG, "Cloudinary account is disabled. Please check your Cloudinary dashboard.");
                    } else if (errorMessage.contains("401")) {
                        Log.e(TAG, "Cloudinary authentication failed. Please check your API credentials.");
                    } else if (errorMessage.contains("403")) {
                        Log.e(TAG, "Cloudinary access forbidden. Please check your account permissions.");
                    }
                }
                
                // Return failure on main thread
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        listener.onComplete(new Task<String>() {
                            @Override public boolean isComplete() { return true; }
                            @Override public boolean isSuccessful() { return false; }
                            @Override public boolean isCanceled() { return false; }
                            @Override public String getResult() { return null; }
                            @Override public Exception getException() { return e; }
                            @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { throw (X) e; }
                            @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                            @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                            @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                            @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                            @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                        });
                    });
                } else {
                    // Fallback for non-Activity contexts
                    listener.onComplete(new Task<String>() {
                        @Override public boolean isComplete() { return true; }
                        @Override public boolean isSuccessful() { return false; }
                        @Override public boolean isCanceled() { return false; }
                        @Override public String getResult() { return null; }
                        @Override public Exception getException() { return e; }
                        @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { throw (X) e; }
                        @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                        @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                        @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                        @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                        @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                    });
                }
            }
        });
    }

    public void uploadBitmap(Bitmap bitmap, String folder, OnCompleteListener<String> listener) {
        executorService.execute(() -> {
            try {
                // Check if Cloudinary is initialized
                if (cloudinary == null) {
                    Log.e(TAG, "Cloudinary is not initialized. Cannot upload image.");
                    throw new RuntimeException("Cloudinary is not initialized. Please check your credentials.");
                }
                
                // Convert bitmap to bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] imageBytes = baos.toByteArray();

                // Upload parameters
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("public_id", folder + "/" + System.currentTimeMillis());
                uploadParams.put("overwrite", true);

                // Synchronous upload
                Map<String, Object> result = cloudinary.uploader().upload(imageBytes, uploadParams);
                String imageUrl = (String) result.get("secure_url");
                
                Log.d(TAG, "Cloudinary upload successful: " + imageUrl);
                
                // Return success on main thread
                listener.onComplete(new Task<String>() {
                    @Override public boolean isComplete() { return true; }
                    @Override public boolean isSuccessful() { return true; }
                    @Override public boolean isCanceled() { return false; }
                    @Override public String getResult() { return imageUrl; }
                    @Override public Exception getException() { return null; }
                    @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { return imageUrl; }
                    @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                    @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                    @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error uploading bitmap to Cloudinary", e);
                
                // Return failure on main thread
                listener.onComplete(new Task<String>() {
                    @Override public boolean isComplete() { return true; }
                    @Override public boolean isSuccessful() { return false; }
                    @Override public boolean isCanceled() { return false; }
                    @Override public String getResult() { return null; }
                    @Override public Exception getException() { return e; }
                    @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { throw (X) e; }
                    @Override public Task<String> addOnSuccessListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(java.util.concurrent.Executor executor, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(java.util.concurrent.Executor executor, OnCompleteListener<String> listener) { return this; }
                    @Override public Task<String> addOnSuccessListener(android.app.Activity activity, com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(android.app.Activity activity, com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(android.app.Activity activity, OnCompleteListener<String> listener) { return this; }
                    @Override public Task<String> addOnSuccessListener(com.google.android.gms.tasks.OnSuccessListener<? super String> listener) { return this; }
                    @Override public Task<String> addOnFailureListener(com.google.android.gms.tasks.OnFailureListener listener) { return this; }
                    @Override public Task<String> addOnCompleteListener(OnCompleteListener<String> listener) { return this; }
                });
            }
        });
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 