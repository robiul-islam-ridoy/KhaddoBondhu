package com.example.khaddobondhu.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.khaddobondhu.MainActivity;
import com.example.khaddobondhu.ChatActivity;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.ui.post.PostDetailActivity;
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
    public static final String CHANNEL_GENERAL = "general";
    
    // Notification IDs
    private static final int NOTIFICATION_ID_MESSAGE = 1001;
    private static final int NOTIFICATION_ID_FOOD_REQUEST = 1002;
    private static final int NOTIFICATION_ID_GENERAL = 1003;
    
    private static int messageNotificationId = NOTIFICATION_ID_MESSAGE;
    private static int foodRequestNotificationId = NOTIFICATION_ID_FOOD_REQUEST;
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
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
                showFoodRequestNotification(this, title, body, targetId);
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
            
            // Messages channel
            NotificationChannel messagesChannel = new NotificationChannel(
                CHANNEL_MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            );
            messagesChannel.setDescription("Notifications for new messages");
            messagesChannel.enableVibration(true);
            messagesChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            
            // Food requests channel
            NotificationChannel foodRequestsChannel = new NotificationChannel(
                CHANNEL_FOOD_REQUESTS,
                "Food Requests",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            foodRequestsChannel.setDescription("Notifications for food requests");
            foodRequestsChannel.enableVibration(true);
            
            // General channel
            NotificationChannel generalChannel = new NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_LOW
            );
            generalChannel.setDescription("General app notifications");
            
            List<NotificationChannel> channels = new ArrayList<>();
            channels.add(messagesChannel);
            channels.add(foodRequestsChannel);
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
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        
        NotificationManagerCompat.from(context).notify(messageNotificationId++, builder.build());
    }
    
    /**
     * Show notification for food request
     */
    public static void showFoodRequestNotification(Context context, String requesterName, String postTitle, String postId) {
        String title = "Food request from " + requesterName;
        String body = "Someone is interested in your post: " + postTitle;
        
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            foodRequestNotificationId, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_FOOD_REQUESTS)
            .setSmallIcon(R.drawable.ic_request)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
        
        NotificationManagerCompat.from(context).notify(foodRequestNotificationId++, builder.build());
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
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
        
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
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);
        
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
            .setContentIntent(pendingIntent);
        
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
            .setContentIntent(pendingIntent);
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_GENERAL, builder.build());
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