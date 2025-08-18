package com.example.khaddobondhu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.khaddobondhu.MainActivity;
import com.example.khaddobondhu.ChatActivity;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.ui.post.PostDetailActivity;
import com.example.khaddobondhu.ui.profile.UserProfileViewActivity;
import com.example.khaddobondhu.ui.notification.NotificationActivity;
import com.example.khaddobondhu.model.Notification;
import com.example.khaddobondhu.model.Request;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationService extends FirebaseMessagingService {
    
    private static final String TAG = "NotificationService";
    
    // Notification channels
    public static final String CHANNEL_MESSAGES = "messages";
    public static final String CHANNEL_FOOD_REQUESTS = "food_requests";
    public static final String CHANNEL_REQUEST_STATUS = "request_status_v2";
    public static final String CHANNEL_GENERAL = "general";
    
    // Notification IDs
    private static final int NOTIFICATION_ID_MESSAGE = 1001;
    private static final int NOTIFICATION_ID_FOOD_REQUEST = 1002;
    private static final int NOTIFICATION_ID_REQUEST_STATUS = 1003;
    private static final int NOTIFICATION_ID_GENERAL = 1004;
    
    private static int messageNotificationId = NOTIFICATION_ID_MESSAGE;
    private static int foodRequestNotificationId = NOTIFICATION_ID_FOOD_REQUEST;
    private static int requestStatusNotificationId = NOTIFICATION_ID_REQUEST_STATUS;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    /**
     * Ensure channels exist when posting from app context (not just FCM service)
     */
    private static void ensureNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) return;

            boolean missing = false;
            if (manager.getNotificationChannel(CHANNEL_MESSAGES) == null) missing = true;
            if (manager.getNotificationChannel(CHANNEL_FOOD_REQUESTS) == null) missing = true;
            if (manager.getNotificationChannel(CHANNEL_REQUEST_STATUS) == null) missing = true;
            if (manager.getNotificationChannel(CHANNEL_GENERAL) == null) missing = true;

            if (missing) {
                try {
                    // Create channels using a new service instance context-like method
                    NotificationService temp = new NotificationService();
                    temp.attachBaseContext(context.getApplicationContext());
                    temp.createNotificationChannels();
                } catch (Exception e) {
                    // Fallback: create minimal channels inline
                    try {
                        NotificationChannel fallback = new NotificationChannel(
                                CHANNEL_REQUEST_STATUS,
                                "Request Status",
                                NotificationManager.IMPORTANCE_HIGH
                        );
                        manager.createNotificationChannel(fallback);
                    } catch (Exception ignored) {}
                }
            }
        }
    }
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        
        Map<String, String> data = remoteMessage.getData();
        String title = remoteMessage.getNotification() != null ? 
                      remoteMessage.getNotification().getTitle() : 
                      data.get("title");
        String body = remoteMessage.getNotification() != null ? 
                     remoteMessage.getNotification().getBody() : 
                     data.get("body");
        
        String type = data.get("type");
        String targetId = data.get("target_id");
        
        switch (type) {
            case "message":
                showMessageNotification(this, title, body, targetId);
                break;
            case "food_request":
                showFoodRequestNotification(this, title, body, targetId, "REQUEST_TO_GET");
                break;
            case "request_status":
                // Expecting data keys: owner_name, post_title, status, target_id
                String ownerNameData = data.get("owner_name") != null ? data.get("owner_name") : title;
                String postTitleData = data.get("post_title") != null ? data.get("post_title") : body;
                String statusData = data.get("status") != null ? data.get("status") : "ACCEPTED";
                showRequestStatusNotification(this, ownerNameData, postTitleData, statusData, targetId);
                break;
            case "post_update":
                showPostUpdateNotification(this, title, body, targetId);
                break;
            default:
                showGeneralNotification(this, title, body);
                break;
        }
    }
    
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // TODO: Send token to server
        Log.d(TAG, "New FCM token: " + token);
    }
    
    /**
     * Create notification channels for Android 8.0+
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            
            // Messages channel - High priority for heads-up notifications
            NotificationChannel messagesChannel = new NotificationChannel(
                CHANNEL_MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            );
            messagesChannel.setDescription("Notifications for new messages");
            messagesChannel.enableVibration(true);
            messagesChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            messagesChannel.setShowBadge(true);
            messagesChannel.enableLights(true);
            
            // Food requests channel - Maximum priority for heads-up notifications
            NotificationChannel foodRequestsChannel = new NotificationChannel(
                CHANNEL_FOOD_REQUESTS,
                "Food Requests",
                NotificationManager.IMPORTANCE_HIGH
            );
            foodRequestsChannel.setDescription("High priority notifications for food requests");
            foodRequestsChannel.enableVibration(true);
            foodRequestsChannel.setVibrationPattern(new long[]{0, 300, 200, 300, 200, 300});
            foodRequestsChannel.setShowBadge(true);
            foodRequestsChannel.enableLights(true);
            foodRequestsChannel.setLightColor(android.graphics.Color.GREEN);
            // Set to allow heads-up notifications
            foodRequestsChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            // Note: setLockscreenVisibility and setAllowBubbles are not available in current API level
            
            // Request status channel - Maximum priority for heads-up notifications
            NotificationChannel requestStatusChannel = new NotificationChannel(
                CHANNEL_REQUEST_STATUS,
                "Request Status",
                NotificationManager.IMPORTANCE_HIGH
            );
            requestStatusChannel.setDescription("High priority notifications for request status updates");
            requestStatusChannel.enableVibration(true);
            requestStatusChannel.setVibrationPattern(new long[]{0, 300, 200, 300, 200, 300});
            requestStatusChannel.setShowBadge(true);
            requestStatusChannel.enableLights(true);
            requestStatusChannel.setLightColor(android.graphics.Color.BLUE);
            // Set to allow heads-up notifications
            requestStatusChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            // Note: setLockscreenVisibility and setAllowBubbles are not available in current API level
            
            // General channel
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            generalChannel.setDescription("General app notifications");
            
            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(messagesChannel);
            channels.add(foodRequestsChannel);
            channels.add(requestStatusChannel);
            channels.add(generalChannel);
            notificationManager.createNotificationChannels(channels);
        }
    }
    
    /**
     * Show notification for new message
     */
    public static void showMessageNotification(Context context, String senderName, String message, String chatId) {
        String title = "New message from " + senderName;
        String body = message.length() > 50 ? message.substring(0, 47) + "..." : message;
        
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chat_id", chatId);
        intent.putExtra("sender_name", senderName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            messageNotificationId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MESSAGES)
            .setSmallIcon(R.drawable.ic_message)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(new long[]{0, 500, 200, 500})
            .setDefaults(NotificationCompat.DEFAULT_ALL);
        
        NotificationManagerCompat.from(context).notify(messageNotificationId++, builder.build());
    }
    
    /**
     * Show notification for food request - This goes to POST OWNER
     */
    public static void showFoodRequestNotification(Context context, String requesterName, String postTitle, String postId, String requestType) {
        ensureNotificationChannels(context);
        Log.d(TAG, "Showing food request notification to POST OWNER");
        Log.d(TAG, "Requester: " + requesterName + ", Post: " + postTitle + ", PostId: " + postId + ", Type: " + requestType);
        
        // Generate category-specific notification text
        String title = "🍽️ New Food Request";
        String body = generateRequestNotificationText(requesterName, postTitle, requestType);
        
        // Navigate to Requests tab in Profile
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("navigate_to", "requests");
        intent.putExtra("post_id", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Use unique request code to prevent duplicate notifications
        int requestCode = (int) System.currentTimeMillis();
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            requestCode, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Create heads-up notification that stays in drawer
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_FOOD_REQUESTS)
            .setSmallIcon(R.drawable.ic_request)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(new long[]{0, 500, 200, 500})
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true) // This makes it heads-up
            .setOngoing(false) // Allow user to dismiss
            .setOnlyAlertOnce(true); // Prevent duplicate alerts
        
        NotificationManagerCompat.from(context).notify(requestCode, builder.build());
        
        Log.d(TAG, "Food request notification sent successfully with ID: " + requestCode);
        
        // Trigger enhanced vibration pattern
        triggerEnhancedVibration(context);
    }
    
    /**
     * Show notification for request status update (accepted/declined) - This goes to REQUESTER
     */
    public static void showRequestStatusNotification(Context context, String postOwnerName, String postTitle, String status, String postId) {
        ensureNotificationChannels(context);
        Log.d(TAG, "Showing request status notification to REQUESTER");
        Log.d(TAG, "Post Owner: " + postOwnerName + ", Post: " + postTitle + ", Status: " + status + ", PostId: " + postId);
        
        String title = status.equals("ACCEPTED") ? "✅ Request Accepted!" : "❌ Request Declined";
        String body = generateStatusNotificationText(postOwnerName, postTitle, status);
        
        // Navigate to the specific post that was requested
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Use unique request code to prevent duplicate notifications
        int requestCode = (int) System.currentTimeMillis();
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            requestCode, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        int icon = status.equals("ACCEPTED") ? R.drawable.ic_check : R.drawable.ic_close;
        
        // Create enhanced heads-up notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_REQUEST_STATUS)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(new long[]{0, 500, 200, 500})
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true) // Heads-up
            .setOngoing(false) // Allow user to dismiss
            .setOnlyAlertOnce(true); // Prevent duplicate alerts
        
        NotificationManagerCompat.from(context).notify(requestCode, builder.build());
        
        Log.d(TAG, "Request status notification sent successfully with ID: " + requestCode);
        
        // Trigger enhanced vibration pattern
        triggerEnhancedVibration(context);
    }
    
    /**
     * Show notification for request received (when someone requests your food) - POST OWNER gets this
     */
    public static void showRequestReceivedNotification(Context context, String requesterName, String postTitle, String postId, String requestType) {
        showFoodRequestNotification(context, requesterName, postTitle, postId, requestType);
    }
    
    /**
     * Show notification for request accepted/declined (when your request is responded to) - REQUESTER gets this
     */
    public static void showRequestResponseNotification(Context context, String postOwnerName, String postTitle, String status, String postId) {
        showRequestStatusNotification(context, postOwnerName, postTitle, status, postId);
    }
    
    /**
     * Show notification for current user (for immediate heads-up display)
     */
    public static void showNotificationForCurrentUser(Context context, String title, String message, String type, String relatedId) {
        switch (type) {
            case "REQUEST_RECEIVED":
                // Extract post title from message
                String postTitle = message.replace(" is interested in your post: ", "");
                showRequestReceivedNotification(context, title.replace("New Food Request from ", ""), postTitle, relatedId, "REQUEST_TO_GET");
                break;
                
            case "REQUEST_ACCEPTED":
            case "REQUEST_DECLINED":
                // Extract post title from message
                String status = type.equals("REQUEST_ACCEPTED") ? "ACCEPTED" : "DECLINED";
                String ownerName = title.replace("Request " + status.toLowerCase() + " from ", "");
                String postTitle2 = message.replace(" has " + status.toLowerCase() + " your request for: ", "");
                showRequestResponseNotification(context, ownerName, postTitle2, status, relatedId);
                break;
                
            default:
                showGeneralNotification(context, title, message);
                break;
        }
    }
    
    /**
     * Show notification for post updates
     */
    public static void showPostUpdateNotification(Context context, String title, String body, String postId) {
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            NOTIFICATION_ID_GENERAL, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GENERAL, builder.build());
    }
    
    /**
     * Show general notification
     */
    public static void showGeneralNotification(Context context, String title, String body) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GENERAL, builder.build());
    }
    
    /**
     * Show welcome notification for new users
     */
    public static void showWelcomeNotification(Context context, String userName) {
        String title = "Welcome to KhaddoBondhu!";
        String body = "Hi " + userName + ", start sharing food and helping your community!";
        
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_home)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GENERAL, builder.build());
    }
    
    /**
     * Show reminder notification for expiring posts
     */
    public static void showExpiryReminderNotification(Context context, String postTitle, String postId) {
        String title = "Post Expiring Soon";
        String body = "Your post '" + postTitle + "' will expire soon. Consider extending it!";
        
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_time)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GENERAL, builder.build());
    }
    
    /**
     * Generate category-specific notification text for food requests
     */
    private static String generateRequestNotificationText(String requesterName, String postTitle, String requestType) {
        switch (requestType) {
            case "REQUEST_TO_GET":
                return requesterName + " wants to get your donated food: " + postTitle;
            case "REQUEST_TO_BUY":
                return requesterName + " wants to buy your food: " + postTitle;
            case "WANT_TO_DONATE":
                return requesterName + " wants to donate food for your request: " + postTitle;
            case "WANT_TO_SELL":
                return requesterName + " wants to sell food for your request: " + postTitle;
            default:
                return requesterName + " is interested in your post: " + postTitle;
        }
    }
    
    /**
     * Generate enhanced notification text for request status updates
     */
    private static String generateStatusNotificationText(String postOwnerName, String postTitle, String status) {
        if (status.equals("ACCEPTED")) {
            return postOwnerName + " has accepted your request! 🎉\nYou can now contact them about: " + postTitle;
        } else {
            return postOwnerName + " has declined your request for: " + postTitle + "\nDon't worry, there are other opportunities! 💪";
        }
    }
    
    /**
     * Trigger enhanced device vibration with pattern
     */
    private static void triggerEnhancedVibration(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                // Enhanced vibration pattern: short-long-short
                long[] pattern = {0, 200, 300, 400, 300, 200};
                int[] amplitudes = {0, 255, 0, 255, 0, 255};
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1));
            }
        } else {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                // Fallback for older devices
                vibrator.vibrate(new long[]{0, 200, 300, 400, 300, 200}, -1);
            }
        }
    }
    
    /**
     * Trigger device vibration (legacy method)
     */
    private static void triggerVibration(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(500);
            }
        }
    }
    
    /**
     * Cancel all notifications
     */
    public static void cancelAllNotifications(Context context) {
        NotificationManagerCompat.from(context).cancelAll();
    }
    
    /**
     * Cancel specific notification
     */
    public static void cancelNotification(Context context, int notificationId) {
        NotificationManagerCompat.from(context).cancel(notificationId);
    }
    
    /**
     * Check if notifications are enabled
     */
    public static boolean areNotificationsEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    
    /**
     * Get notification manager
     */
    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
} 