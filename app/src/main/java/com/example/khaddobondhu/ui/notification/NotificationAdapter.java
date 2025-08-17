package com.example.khaddobondhu.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onDeleteClick(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private ImageView notificationIcon;
        private ImageView senderProfilePic;
        private TextView titleText;
        private TextView messageText;
        private TextView timeText;
        private TextView senderNameText;
        private ImageView deleteButton;
        private View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            senderProfilePic = itemView.findViewById(R.id.senderProfilePic);
            titleText = itemView.findViewById(R.id.titleText);
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
            senderNameText = itemView.findViewById(R.id.senderNameText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onNotificationClick(notifications.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(notifications.get(position));
                }
            });
        }

        public void bind(Notification notification) {
            titleText.setText(notification.getTitle());
            messageText.setText(notification.getMessage());
            timeText.setText(notification.getTimeAgo());
            
            // Set unread indicator
            unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);
            
            // Set notification icon based on type
            notificationIcon.setImageResource(notification.getNotificationIcon());
            
            // Load sender profile picture
            if (notification.getSenderProfilePictureUrl() != null && !notification.getSenderProfilePictureUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(notification.getSenderProfilePictureUrl())
                        .placeholder(R.drawable.default_profile_pic)
                        .error(R.drawable.default_profile_pic)
                        .circleCrop()
                        .into(senderProfilePic);
            } else {
                senderProfilePic.setImageResource(R.drawable.default_profile_pic);
            }
            
            // Set sender name
            if (notification.getSenderName() != null && !notification.getSenderName().isEmpty()) {
                senderNameText.setText("from " + notification.getSenderName());
                senderNameText.setVisibility(View.VISIBLE);
            } else {
                senderNameText.setVisibility(View.GONE);
            }
        }
    }
}
