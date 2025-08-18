package com.example.khaddobondhu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.khaddobondhu.model.Notification;
import com.example.khaddobondhu.service.FirebaseService;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class NotificationChecker {
    private static final String TAG = "NotificationChecker";
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_SHOWN_NOTIFICATIONS = "shown_notifications";
    private static ListenerRegistration realtimeRegistration;
    
    /**
     * Check for unread notifications and show heads-up notifications
     */
    public static void checkAndShowNotifications(Context context) {
        FirebaseService firebaseService = new FirebaseService();
        String currentUserId = firebaseService.getCurrentUserId();
        
        if (currentUserId == null) {
            Log.w(TAG, "User not authenticated, cannot check notifications");
            return;
        }
        
        firebaseService.getNotificationsForUser(currentUserId, new FirebaseService.OnNotificationsFetchListener() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                // Get previously shown notifications
                Set<String> shownNotifications = getShownNotifications(context);
                Set<String> newlyShown = new HashSet<>();
                
                // Find unread notifications that haven't been shown yet
                for (Notification notification : notifications) {
                    if (!notification.isRead() && !shownNotifications.contains(notification.getId())) {
                        showHeadsUpNotification(context, notification);
                        newlyShown.add(notification.getId());
                    }
                }
                
                // Save newly shown notifications
                if (!newlyShown.isEmpty()) {
                    saveShownNotifications(context, newlyShown);
                }
            }
            
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to fetch notifications: " + e.getMessage());
            }
        });
    }

    /**
     * Start real-time listener for unread notifications (shows true heads-up in foreground)
     */
    public static void startRealtimeNotifications(Context context) {
        // Prevent multiple listeners
        stopRealtimeNotifications();

        FirebaseService firebaseService = new FirebaseService();
        String currentUserId = firebaseService.getCurrentUserId();
        if (currentUserId == null) {
            Log.w(TAG, "User not authenticated, cannot start realtime notifications");
            return;
        }

        Set<String> shownNotifications = getShownNotifications(context);

        realtimeRegistration = firebaseService.addUnreadNotificationsListener(currentUserId, (QuerySnapshot snapshots, FirebaseFirestoreException e) -> {
            if (e != null) {
                Log.e(TAG, "Realtime notifications error: " + e.getMessage());
                return;
            }

            if (snapshots == null) return;

            for (DocumentChange change : snapshots.getDocumentChanges()) {
                if (change.getType() != DocumentChange.Type.ADDED) continue;

                try {
                    String id = change.getDocument().getId();
                    if (shownNotifications.contains(id)) continue;

                    // Build a minimal Notification model
                    Notification n = new Notification();
                    n.setId(id);
                    n.setUserId(change.getDocument().getString("userId"));
                    n.setTitle(change.getDocument().getString("title"));
                    n.setMessage(change.getDocument().getString("message"));
                    n.setType(change.getDocument().getString("type"));
                    n.setRelatedId(change.getDocument().getString("relatedId"));
                    n.setSenderId(change.getDocument().getString("senderId"));
                    n.setSenderName(change.getDocument().getString("senderName"));

                    // Show heads-up notification
                    showHeadsUpNotification(context, n);

                    // Mark as shown and as read to avoid duplicates
                    shownNotifications.add(id);
                    saveShownNotifications(context, shownNotifications);

                    FirebaseService svc = new FirebaseService();
                    svc.markNotificationAsRead(id, new FirebaseService.OnNotificationListener() {
                        @Override
                        public void onSuccess() { Log.d(TAG, "Marked notification as read: " + id); }
                        @Override
                        public void onError(String error) { Log.w(TAG, "Failed to mark as read: " + error); }
                    });
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to process realtime notification: " + ex.getMessage());
                }
            }
        });
    }

    /**
     * Stop real-time notifications listener
     */
    public static void stopRealtimeNotifications() {
        if (realtimeRegistration != null) {
            try { realtimeRegistration.remove(); } catch (Exception ignored) {}
            realtimeRegistration = null;
        }
    }
    
    /**
     * Show heads-up notification based on notification type
     */
    private static void showHeadsUpNotification(Context context, Notification notification) {
        Log.d(TAG, "Showing heads-up notification: " + notification.getType() + " - " + notification.getTitle());
        
        switch (notification.getType()) {
            case "REQUEST_RECEIVED":
                // Extract requester name and post title from message
                String message = notification.getMessage();
                String requesterName = notification.getSenderName();
                String postTitle = extractPostTitleFromMessage(message, requesterName);
                String requestType = extractRequestTypeFromMessage(message);
                
                NotificationService.showRequestReceivedNotification(
                    context,
                    requesterName,
                    postTitle,
                    notification.getRelatedId(),
                    requestType
                );
                break;
                
            case "REQUEST_ACCEPTED":
            case "REQUEST_DECLINED":
                // Extract post owner name and post title from message
                String statusMessage = notification.getMessage();
                String postOwnerName = notification.getSenderName();
                String status = notification.getType().equals("REQUEST_ACCEPTED") ? "ACCEPTED" : "DECLINED";
                String postTitle2 = statusMessage.replace(postOwnerName + " has " + status.toLowerCase() + " your request for: ", "");
                
                NotificationService.showRequestResponseNotification(
                    context,
                    postOwnerName,
                    postTitle2,
                    status,
                    notification.getRelatedId()
                );
                break;
                
            default:
                // For other notification types, show general notification
                NotificationService.showGeneralNotification(
                    context,
                    notification.getTitle(),
                    notification.getMessage()
                );
                break;
        }
    }
    
    /**
     * Get previously shown notification IDs
     */
    private static Set<String> getShownNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_SHOWN_NOTIFICATIONS, new HashSet<>());
    }
    
    /**
     * Save newly shown notification IDs
     */
    private static void saveShownNotifications(Context context, Set<String> newNotifications) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> existing = getShownNotifications(context);
        existing.addAll(newNotifications);
        prefs.edit().putStringSet(KEY_SHOWN_NOTIFICATIONS, existing).apply();
    }
    
    /**
     * Extract post title from notification message
     */
    private static String extractPostTitleFromMessage(String message, String requesterName) {
        // Try different patterns to extract post title
        String[] patterns = {
            requesterName + " wants to get your donated food: ",
            requesterName + " wants to buy your food: ",
            requesterName + " wants to donate food for your request: ",
            requesterName + " wants to sell food for your request: ",
            requesterName + " is interested in your post: "
        };
        
        for (String pattern : patterns) {
            if (message.contains(pattern)) {
                return message.replace(pattern, "");
            }
        }
        
        // Fallback: return the last part after the colon
        if (message.contains(": ")) {
            return message.substring(message.lastIndexOf(": ") + 2);
        }
        
        return "Unknown Post";
    }
    
    /**
     * Extract request type from notification message
     */
    private static String extractRequestTypeFromMessage(String message) {
        if (message.contains("wants to get your donated food")) {
            return "REQUEST_TO_GET";
        } else if (message.contains("wants to buy your food")) {
            return "REQUEST_TO_BUY";
        } else if (message.contains("wants to donate food for your request")) {
            return "WANT_TO_DONATE";
        } else if (message.contains("wants to sell food for your request")) {
            return "WANT_TO_SELL";
        } else {
            return "REQUEST_TO_GET"; // Default fallback
        }
    }
    
    /**
     * Clear shown notifications (call this when user logs out)
     */
    public static void clearShownNotifications(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_SHOWN_NOTIFICATIONS).apply();
    }
}
