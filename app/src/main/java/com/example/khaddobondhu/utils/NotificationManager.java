package com.example.khaddobondhu.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Notification;

public class NotificationManager {
    
    private static NotificationManager instance;
    private Context context;
    private MediaPlayer mediaPlayer;
    
    private NotificationManager(Context context) {
        this.context = context.getApplicationContext();
    }
    
    public static NotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationManager(context);
        }
        return instance;
    }
    
    /**
     * Show in-app notification popup
     */
    public void showInAppNotification(Activity activity, String title, String message, String type, String senderName, String senderProfilePictureUrl) {
        if (activity == null || activity.isFinishing()) {
            // Fallback to toast if activity is not available
            Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
            playNotificationSound();
            triggerVibration();
            return;
        }
        
        // Play sound and vibration
        playNotificationSound();
        triggerVibration();
        
        // Create custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_in_app_notification, null);
        builder.setView(dialogView);
        
        // Initialize views
        ImageView senderImageView = dialogView.findViewById(R.id.senderImageView);
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        TextView senderNameTextView = dialogView.findViewById(R.id.senderNameTextView);
        
        // Set content
        titleTextView.setText(title);
        messageTextView.setText(message);
        senderNameTextView.setText(senderName);
        
        // Load sender profile picture
        if (senderProfilePictureUrl != null && !senderProfilePictureUrl.isEmpty()) {
            Glide.with(activity)
                .load(senderProfilePictureUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(senderImageView);
        } else {
            senderImageView.setImageResource(R.drawable.ic_person);
        }
        
        // Set notification icon based on type
        ImageView notificationIcon = dialogView.findViewById(R.id.notificationIcon);
        switch (type) {
            case "REQUEST_RECEIVED":
                notificationIcon.setImageResource(R.drawable.ic_request);
                break;
            case "REQUEST_ACCEPTED":
                notificationIcon.setImageResource(R.drawable.ic_check);
                break;
            case "REQUEST_DECLINED":
                notificationIcon.setImageResource(R.drawable.ic_close);
                break;
            case "MESSAGE":
                notificationIcon.setImageResource(R.drawable.ic_message);
                break;
            default:
                notificationIcon.setImageResource(R.drawable.ic_notification);
                break;
        }
        
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        
        // Auto-dismiss after 5 seconds
        dialog.show();
        new android.os.Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 5000);
    }
    
    /**
     * Show notification for request received
     */
    public void showRequestReceivedNotification(Activity activity, String requesterName, String postTitle, String requesterProfilePictureUrl) {
        String title = "New Food Request";
        String message = requesterName + " is interested in your post: " + postTitle;
        showInAppNotification(activity, title, message, "REQUEST_RECEIVED", requesterName, requesterProfilePictureUrl);
    }
    
    /**
     * Show notification for request status update
     */
    public void showRequestStatusNotification(Activity activity, String postOwnerName, String postTitle, String status, String postOwnerProfilePictureUrl) {
        String title = "Request " + status.toLowerCase();
        String message = postOwnerName + " has " + status.toLowerCase() + " your request for: " + postTitle;
        showInAppNotification(activity, title, message, "REQUEST_" + status, postOwnerName, postOwnerProfilePictureUrl);
    }
    
    /**
     * Play notification sound
     */
    private void playNotificationSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, notificationSound);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_NOTIFICATION);
            }
            
            mediaPlayer.prepare();
            mediaPlayer.start();
            
            // Release after playing
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
            
        } catch (Exception e) {
            // Fallback to system notification sound
            try {
                Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                android.media.Ringtone ringtone = RingtoneManager.getRingtone(context, notificationSound);
                ringtone.play();
            } catch (Exception ex) {
                // Ignore if sound cannot be played
            }
        }
    }
    
    /**
     * Trigger device vibration
     */
    private void triggerVibration() {
        try {
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
        } catch (Exception e) {
            // Ignore if vibration cannot be triggered
        }
    }
    
    /**
     * Show simple toast notification
     */
    public void showToastNotification(String title, String message) {
        Toast.makeText(context, title + ": " + message, Toast.LENGTH_LONG).show();
        playNotificationSound();
        triggerVibration();
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
