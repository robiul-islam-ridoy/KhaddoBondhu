package com.example.khaddobondhu.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Request;
import com.example.khaddobondhu.service.FirebaseService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private List<Request> requests;
    private Context context;
    private FirebaseService firebaseService;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onRequestAccepted(Request request);
        void onRequestDeclined(Request request);
    }

    public RequestAdapter(Context context, List<Request> requests, OnRequestActionListener listener) {
        this.context = context;
        this.requests = requests;
        this.listener = listener;
        this.firebaseService = new FirebaseService();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateRequests(List<Request> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView requesterProfileImageView;
        private TextView requesterNameTextView;
        private TextView postTitleTextView;
        private TextView requestTypeTextView;
        private TextView requestMessageTextView;
        private TextView requestTimeTextView;
        private TextView statusTextView;
        private Button acceptButton;
        private Button declineButton;
        private View actionButtonsLayout;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requesterProfileImageView = itemView.findViewById(R.id.requesterProfileImageView);
            requesterNameTextView = itemView.findViewById(R.id.requesterNameTextView);
            postTitleTextView = itemView.findViewById(R.id.postTitleTextView);
            requestTypeTextView = itemView.findViewById(R.id.requestTypeTextView);
            requestMessageTextView = itemView.findViewById(R.id.requestMessageTextView);
            requestTimeTextView = itemView.findViewById(R.id.requestTimeTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout);
        }

        public void bind(Request request) {
            // Set requester name
            requesterNameTextView.setText(request.getRequesterName());

            // Set post title
            postTitleTextView.setText(request.getPostTitle());

            // Set request type
            requestTypeTextView.setText(request.getRequestTypeDisplayText());

            // Set request message
            if (request.getMessage() != null && !request.getMessage().isEmpty()) {
                requestMessageTextView.setText(request.getMessage());
                requestMessageTextView.setVisibility(View.VISIBLE);
            } else {
                requestMessageTextView.setVisibility(View.GONE);
            }

            // Set request time
            if (request.getCreatedAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
                requestTimeTextView.setText(sdf.format(request.getCreatedAt().toDate()));
            } else {
                requestTimeTextView.setText("Just now");
            }

            // Set status
            statusTextView.setText(request.getStatusDisplayText());
            setStatusColor(request.getStatus());

            // Load requester profile picture
            if (request.getRequesterProfilePictureUrl() != null && !request.getRequesterProfilePictureUrl().isEmpty()) {
                Glide.with(context)
                    .load(request.getRequesterProfilePictureUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(requesterProfileImageView);
            } else {
                requesterProfileImageView.setImageResource(R.drawable.ic_person);
            }

            // Handle action buttons based on status
            if (request.isPending()) {
                actionButtonsLayout.setVisibility(View.VISIBLE);
                statusTextView.setVisibility(View.GONE);
                
                acceptButton.setOnClickListener(v -> showAcceptConfirmationDialog(request));
                declineButton.setOnClickListener(v -> showDeclineConfirmationDialog(request));
            } else {
                actionButtonsLayout.setVisibility(View.GONE);
                statusTextView.setVisibility(View.VISIBLE);
            }
        }

        private void setStatusColor(String status) {
            switch (status) {
                case "PENDING":
                    statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
                    break;
                case "ACCEPTED":
                    statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    break;
                case "DECLINED":
                    statusTextView.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    break;
                default:
                    statusTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }

        private void showAcceptConfirmationDialog(Request request) {
            new AlertDialog.Builder(context)
                .setTitle("Accept Request")
                .setMessage("Are you sure you want to accept this request from " + request.getRequesterName() + "?")
                .setPositiveButton("Accept", (dialog, which) -> {
                    acceptRequest(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }

        private void showDeclineConfirmationDialog(Request request) {
            new AlertDialog.Builder(context)
                .setTitle("Decline Request")
                .setMessage("Are you sure you want to decline this request from " + request.getRequesterName() + "?")
                .setPositiveButton("Decline", (dialog, which) -> {
                    declineRequest(request);
                })
                .setNegativeButton("Cancel", null)
                .show();
        }

        private void acceptRequest(Request request) {
            firebaseService.updateRequestStatus(request.getId(), "ACCEPTED", new FirebaseService.OnRequestListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Request accepted successfully!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRequestAccepted(request);
                    }
                    
                    // Send notification to requester
                    sendRequestStatusNotification(request, "ACCEPTED");
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Failed to accept request: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void declineRequest(Request request) {
            firebaseService.updateRequestStatus(request.getId(), "DECLINED", new FirebaseService.OnRequestListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "Request declined successfully!", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onRequestDeclined(request);
                    }
                    
                    // Send notification to requester
                    sendRequestStatusNotification(request, "DECLINED");
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Failed to decline request: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * Send notification to requester when request status is updated
     */
    private void sendRequestStatusNotification(Request request, String status) {
        // Get current user (post owner) information
        String currentUserId = firebaseService.getCurrentUserId();
        String currentUserName = firebaseService.getCurrentUserName();
        String currentUserProfilePictureUrl = firebaseService.getCurrentUserProfilePictureUrl();
        
        // Create notification for requester (not the post owner)
        com.example.khaddobondhu.model.Notification notification = new com.example.khaddobondhu.model.Notification(
            request.getRequesterId(), // recipient (the person who made the request)
            "Request " + status.toLowerCase(),
            currentUserName + " has " + status.toLowerCase() + " your request for: " + request.getPostTitle(),
            "REQUEST_" + status,
            request.getPostId(), // related post ID
            currentUserId, // sender (post owner)
            currentUserName,
            currentUserProfilePictureUrl
        );
        
        // Save notification to Firestore
        firebaseService.createNotification(notification, new FirebaseService.OnNotificationListener() {
            @Override
            public void onSuccess() {
                // Send push notification to the requester
                com.example.khaddobondhu.utils.NotificationService.sendPushNotificationToUser(
                    context,
                    request.getRequesterId(),
                    "Request " + status.toLowerCase(),
                    currentUserName + " has " + status.toLowerCase() + " your request for: " + request.getPostTitle(),
                    "request_status",
                    request.getPostId()
                );
            }
            
            @Override
            public void onError(String error) {
                android.util.Log.e("RequestAdapter", "Failed to create notification: " + error);
            }
        });
    }
}
