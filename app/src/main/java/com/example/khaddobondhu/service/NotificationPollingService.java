package com.example.khaddobondhu.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Notification;
import com.example.khaddobondhu.utils.NotificationService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationPollingService extends Service {
    private static final String TAG = "NotificationPolling";
    private static final String CHANNEL_ID = "notification_polling";
    private static final int POLLING_INTERVAL = 30000; // 30 seconds
    private static final int NOTIFICATION_ID = 1001;
    
    private Handler handler;
    private Runnable pollingRunnable;
    private ExecutorService executorService;
    private FirebaseService firebaseService;
    private String lastNotificationId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();
        firebaseService = new FirebaseService(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startPolling();
        return START_STICKY;
    }

    private void startPolling() {
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                checkForNewNotifications();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        handler.post(pollingRunnable);
    }

    private void checkForNewNotifications() {
        String currentUserId = firebaseService.getCurrentUserId();
        if (currentUserId == null) return;

        executorService.execute(() -> {
            firebaseService.getNotificationsForUser(currentUserId, new FirebaseService.OnNotificationsFetchListener() {
                @Override
                public void onSuccess(List<Notification> notifications) {
                    if (!notifications.isEmpty()) {
                        Notification latestNotification = notifications.get(0);
                        
                        // Check if this is a new notification
                        if (!latestNotification.getId().equals(lastNotificationId)) {
                            lastNotificationId = latestNotification.getId();
                            
                            // Show local notification
                            showLocalNotification(
                                latestNotification.getTitle(),
                                latestNotification.getMessage(),
                                latestNotification.getType(),
                                latestNotification.getRelatedId()
                            );
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "Error fetching notifications: " + e.getMessage());
                }
            });
        });
    }

    private void showLocalNotification(String title, String message, String type, String targetId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(new long[]{0, 500, 200, 500});

        // Add click action based on notification type
        if ("food_request".equals(type)) {
            // Navigate to profile tab and open requests
            Intent intent = new Intent(this, com.example.khaddobondhu.MainActivity.class);
            intent.putExtra("navigate_to", "profile");
            intent.putExtra("open_requests_tab", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );
            builder.setContentIntent(pendingIntent);
        } else if ("request_status".equals(type)) {
            // Navigate to post details
            Intent intent = new Intent(this, com.example.khaddobondhu.ui.post.PostDetailActivity.class);
            intent.putExtra("post_id", targetId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE
            );
            builder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Notification Polling",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications from polling service");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
