package com.example.khaddobondhu.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FirebaseStorageService {
    private static final String TAG = "FirebaseStorageService";
    private FirebaseStorage storage;
    private Context context;

    public FirebaseStorageService(Context context) {
        this.context = context;
        this.storage = FirebaseStorage.getInstance();
    }

    public void uploadImage(Uri imageUri, String folder, OnCompleteListener<String> listener) {
        try {
            // Convert URI to bitmap
            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                context.getContentResolver(), imageUri);
            
            // Convert bitmap to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();

            // Create unique filename
            String filename = folder + "/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storage.getReference().child(filename);

            // Upload the image
            UploadTask uploadTask = imageRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            Log.d(TAG, "Firebase upload successful: " + downloadUri.toString());
                            listener.onComplete(new Task<String>() {
                                @Override public boolean isComplete() { return true; }
                                @Override public boolean isSuccessful() { return true; }
                                @Override public boolean isCanceled() { return false; }
                                @Override public String getResult() { return downloadUri.toString(); }
                                @Override public Exception getException() { return null; }
                                @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { return downloadUri.toString(); }
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Firebase upload failed", e);
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

        } catch (Exception e) {
            Log.e(TAG, "Error uploading image to Firebase", e);
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

    public void uploadBitmap(Bitmap bitmap, String folder, OnCompleteListener<String> listener) {
        try {
            // Convert bitmap to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();

            // Create unique filename
            String filename = folder + "/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storage.getReference().child(filename);

            // Upload the image
            UploadTask uploadTask = imageRef.putBytes(imageBytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            Log.d(TAG, "Firebase upload successful: " + downloadUri.toString());
                            listener.onComplete(new Task<String>() {
                                @Override public boolean isComplete() { return true; }
                                @Override public boolean isSuccessful() { return true; }
                                @Override public boolean isCanceled() { return false; }
                                @Override public String getResult() { return downloadUri.toString(); }
                                @Override public Exception getException() { return null; }
                                @Override public <X extends Throwable> String getResult(Class<X> aClass) throws X { return downloadUri.toString(); }
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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Firebase upload failed", e);
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

        } catch (Exception e) {
            Log.e(TAG, "Error uploading bitmap to Firebase", e);
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
} 