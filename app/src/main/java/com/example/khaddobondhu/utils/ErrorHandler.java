package com.example.khaddobondhu.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {
    
    private static final String TAG = "ErrorHandler";
    
    // Error categories
    public static final String CATEGORY_NETWORK = "network";
    public static final String CATEGORY_AUTH = "authentication";
    public static final String CATEGORY_DATABASE = "database";
    public static final String CATEGORY_VALIDATION = "validation";
    public static final String CATEGORY_STORAGE = "storage";
    public static final String CATEGORY_UNKNOWN = "unknown";
    
    // Error tracking
    private static final Map<String, Integer> errorCounts = new HashMap<>();
    private static final Map<String, Long> lastErrorTimes = new HashMap<>();
    
    /**
     * Handle Firebase authentication errors
     */
    public static void handleAuthError(FirebaseAuthException e, Context context) {
        String errorCode = e.getErrorCode();
        String userMessage;
        
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                userMessage = "Invalid email address";
                break;
            case "ERROR_WRONG_PASSWORD":
                userMessage = "Incorrect password";
                break;
            case "ERROR_USER_NOT_FOUND":
                userMessage = "Account not found";
                break;
            case "ERROR_USER_DISABLED":
                userMessage = "Account has been disabled";
                break;
            case "ERROR_TOO_MANY_REQUESTS":
                userMessage = "Too many failed attempts. Please try again later";
                break;
            case "ERROR_OPERATION_NOT_ALLOWED":
                userMessage = "This operation is not allowed";
                break;
            case "ERROR_WEAK_PASSWORD":
                userMessage = "Password is too weak";
                break;
            case "ERROR_EMAIL_ALREADY_IN_USE":
                userMessage = "Email is already registered";
                break;
            default:
                userMessage = "Authentication failed. Please try again";
                break;
        }
        
        logError(CATEGORY_AUTH, errorCode, e.getMessage(), e);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Handle Firestore database errors
     */
    public static void handleFirestoreError(FirebaseFirestoreException e, Context context) {
        String errorCode = e.getCode().name();
        String userMessage;
        
        switch (e.getCode()) {
            case PERMISSION_DENIED:
                userMessage = "You don't have permission to perform this action";
                break;
            case UNAVAILABLE:
                userMessage = "Service temporarily unavailable. Please try again";
                break;
            case DEADLINE_EXCEEDED:
                userMessage = "Request timed out. Please try again";
                break;
            case NOT_FOUND:
                userMessage = "The requested data was not found";
                break;
            case ALREADY_EXISTS:
                userMessage = "This item already exists";
                break;
            case RESOURCE_EXHAUSTED:
                userMessage = "Service limit exceeded. Please try again later";
                break;
            case FAILED_PRECONDITION:
                userMessage = "Invalid operation. Please check your data";
                break;
            case ABORTED:
                userMessage = "Operation was cancelled";
                break;
            case OUT_OF_RANGE:
                userMessage = "Invalid data range";
                break;
            case UNIMPLEMENTED:
                userMessage = "This feature is not available yet";
                break;
            case INTERNAL:
                userMessage = "Internal error. Please try again";
                break;
            case DATA_LOSS:
                userMessage = "Data loss occurred. Please try again";
                break;
            default:
                userMessage = "Database error occurred. Please try again";
                break;
        }
        
        logError(CATEGORY_DATABASE, errorCode, e.getMessage(), e);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Handle network errors
     */
    public static void handleNetworkError(Exception e, Context context) {
        String userMessage = "Network connection error. Please check your internet connection";
        
        logError(CATEGORY_NETWORK, "NETWORK_ERROR", e.getMessage(), e);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Handle validation errors
     */
    public static void handleValidationError(String field, String message, Context context) {
        String userMessage = "Invalid " + field + ": " + message;
        
        logError(CATEGORY_VALIDATION, "VALIDATION_ERROR", message, null);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Handle storage errors
     */
    public static void handleStorageError(Exception e, Context context) {
        String userMessage = "Failed to upload file. Please try again";
        
        logError(CATEGORY_STORAGE, "STORAGE_ERROR", e.getMessage(), e);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Handle unknown errors
     */
    public static void handleUnknownError(Exception e, Context context) {
        String userMessage = "An unexpected error occurred. Please try again";
        
        logError(CATEGORY_UNKNOWN, "UNKNOWN_ERROR", e.getMessage(), e);
        showUserMessage(context, userMessage);
    }
    
    /**
     * Log error with category and details
     */
    private static void logError(String category, String errorCode, String message, Exception e) {
        // Increment error count
        String key = category + ":" + errorCode;
        errorCounts.put(key, errorCounts.getOrDefault(key, 0) + 1);
        lastErrorTimes.put(key, System.currentTimeMillis());
        
        // Log to console
        Log.e(TAG, String.format("Error [%s] [%s]: %s", category, errorCode, message));
        
        if (e != null) {
            Log.e(TAG, "Stack trace:", e);
        }
        
        // TODO: Send to crash reporting service (Firebase Crashlytics)
        // FirebaseCrashlytics.getInstance().recordException(e);
    }
    
    /**
     * Show user-friendly error message
     */
    private static void showUserMessage(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Get error statistics
     */
    public static Map<String, Integer> getErrorCounts() {
        return new HashMap<>(errorCounts);
    }
    
    /**
     * Get last error time for a specific error
     */
    public static Long getLastErrorTime(String category, String errorCode) {
        return lastErrorTimes.get(category + ":" + errorCode);
    }
    
    /**
     * Check if error rate is too high
     */
    public static boolean isErrorRateHigh(String category, int threshold, long timeWindow) {
        long currentTime = System.currentTimeMillis();
        int count = 0;
        
        for (Map.Entry<String, Integer> entry : errorCounts.entrySet()) {
            if (entry.getKey().startsWith(category + ":")) {
                Long lastTime = lastErrorTimes.get(entry.getKey());
                if (lastTime != null && (currentTime - lastTime) < timeWindow) {
                    count += entry.getValue();
                }
            }
        }
        
        return count >= threshold;
    }
    
    /**
     * Clear error tracking data
     */
    public static void clearErrorTracking() {
        errorCounts.clear();
        lastErrorTimes.clear();
    }
    
    /**
     * Get stack trace as string
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * Check if exception is retryable
     */
    public static boolean isRetryable(Exception e) {
        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException fse = (FirebaseFirestoreException) e;
            switch (fse.getCode()) {
                case UNAVAILABLE:
                case DEADLINE_EXCEEDED:
                case RESOURCE_EXHAUSTED:
                case ABORTED:
                    return true;
                default:
                    return false;
            }
        }
        
        // Network errors are generally retryable
        return e instanceof java.net.UnknownHostException ||
               e instanceof java.net.SocketTimeoutException ||
               e instanceof java.net.ConnectException;
    }
} 