package com.example.khaddobondhu.ui.notification;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Notification;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.utils.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseService firebaseService;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize Firebase service
        firebaseService = new FirebaseService(this);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);

        // Load notifications
        loadNotifications();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notifications, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(Notification notification) {
                // Mark notification as read
                markNotificationAsRead(notification);
                
                // Handle notification click based on type
                handleNotificationClick(notification);
            }

            @Override
            public void onDeleteClick(Notification notification) {
                deleteNotification(notification);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        String currentUserId = firebaseService.getCurrentUserId();
        if (currentUserId == null) {
            showError("User not authenticated");
            return;
        }

        showLoading(true);
        firebaseService.getNotificationsForUser(currentUserId, new FirebaseService.OnNotificationsFetchListener() {
            @Override
            public void onSuccess(List<Notification> notificationList) {
                runOnUiThread(() -> {
                    showLoading(false);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    notifications.clear();
                    notifications.addAll(notificationList);
                    adapter.notifyDataSetChanged();
                    
                    if (notifications.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    swipeRefreshLayout.setRefreshing(false);
                    showError("Failed to load notifications: " + e.getMessage());
                });
            }
        });
    }

    private void markNotificationAsRead(Notification notification) {
        if (notification.isRead()) return;

        firebaseService.markNotificationAsRead(notification.getId(), new FirebaseService.OnNotificationListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(NotificationActivity.this, "Failed to mark as read", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteNotification(Notification notification) {
        firebaseService.deleteNotification(notification.getId(), new FirebaseService.OnNotificationListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    notifications.remove(notification);
                    adapter.notifyDataSetChanged();
                    
                    if (notifications.isEmpty()) {
                        showEmptyState(true);
                    }
                    
                    Toast.makeText(NotificationActivity.this, "Notification deleted", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(NotificationActivity.this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleNotificationClick(Notification notification) {
        // Handle different notification types
        switch (notification.getType()) {
            case "REQUEST_RECEIVED":
            case "REQUEST_ACCEPTED":
            case "REQUEST_DECLINED":
                // Navigate to the related post
                if (notification.getRelatedId() != null) {
                    // You can implement navigation to post details here
                    Toast.makeText(this, "Navigate to post: " + notification.getRelatedId(), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(this, notification.getMessage(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyText.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showLoading(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
