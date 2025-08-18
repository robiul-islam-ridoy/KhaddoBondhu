package com.example.khaddobondhu.ui.post;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.databinding.ActivityPostDetailBinding;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.Message;
import com.example.khaddobondhu.model.Request;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.ui.image.ImagePreviewActivity;
import com.example.khaddobondhu.ui.image.ImageCarouselActivity;
import com.example.khaddobondhu.ui.view.ImageCollageView;
import com.example.khaddobondhu.ui.profile.UserProfileViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private static final int CALL_PERMISSION_REQUEST_CODE = 1001;
    
    private ActivityPostDetailBinding binding;
    private FoodPost foodPost;
    private FirebaseService firebaseService;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        firebaseService = new FirebaseService();
        auth = FirebaseAuth.getInstance();

        // Get post data from intent
        String postId = getIntent().getStringExtra("post_id");
        if (postId == null) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        loadPostDetails(postId);
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void loadPostDetails(String postId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.getFoodPostById(postId, new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
            @Override
            public void onComplete(Task<com.google.firebase.firestore.DocumentSnapshot> task) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    foodPost = task.getResult().toObject(FoodPost.class);
                    if (foodPost != null) {
                        foodPost.setId(task.getResult().getId());
                        displayPostDetails();
                    } else {
                        Toast.makeText(PostDetailActivity.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void displayPostDetails() {
        if (foodPost == null) return;

        // Set basic information
        binding.textViewTitle.setText(foodPost.getTitle());
        binding.textViewDescription.setText(foodPost.getDescription());
        
        // Set price with proper logic
        String priceText = "";
        if ("DONATE".equals(foodPost.getPostType()) || "REQUEST_DONATION".equals(foodPost.getPostType())) {
            priceText = "Free";
        } else if ("SELL".equals(foodPost.getPostType()) || "REQUEST_TO_BUY".equals(foodPost.getPostType())) {
            if (foodPost.getPrice() > 0) {
                priceText = "৳" + String.format("%.0f", foodPost.getPrice());
            } else {
                priceText = "Free";
            }
        }
        
        if (!priceText.isEmpty()) {
            binding.textViewPrice.setText(priceText);
            binding.textViewPrice.setVisibility(View.VISIBLE);
        } else {
            binding.textViewPrice.setVisibility(View.GONE);
        }
        
        binding.textViewQuantity.setText(foodPost.getFormattedQuantity());
        binding.textViewLocation.setText(foodPost.getPickupLocation());
        binding.textViewFoodType.setText(foodPost.getFoodType());
        
        // Set post type with background
        binding.textViewPostType.setText(foodPost.getPostType());
        setPostTypeBackground(foodPost.getPostType());

        // Set time
        if (foodPost.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
            binding.textViewTime.setText(sdf.format(foodPost.getCreatedAt().toDate()));
        } else {
            binding.textViewTime.setText("Just now");
        }

        // Set expiry time
        if (foodPost.getExpiryDate() != null) {
            binding.textViewExpiry.setText(foodPost.getFormattedExpiry());
            binding.expiryLayout.setVisibility(View.VISIBLE);
        } else {
            binding.expiryLayout.setVisibility(View.GONE);
        }

        // Load images using ImageCollageView
        binding.imageCollageView.setImages(foodPost.getImageUrls(), foodPost.getTitle());

        // Load and display user profile data
        fetchUserDataAndSetDisplay(foodPost.getUserId());

        // Show contact buttons for all post types
        binding.buttonContact.setVisibility(View.VISIBLE);
        binding.buttonMessage.setVisibility(View.VISIBLE);
        
        // Set request button text based on post type
        setRequestButtonText(foodPost.getPostType());
        
        // Increment view count (only if not the post owner)
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.getUid().equals(foodPost.getUserId())) {
            firebaseService.incrementPostViews(foodPost.getId());
        }
        
        // Load and display post statistics
        loadPostStatistics();
    }
    
    private void fetchUserDataAndSetDisplay(String userId) {
        if (userId == null || userId.isEmpty()) {
            // Set default name and image if no user ID
            binding.sellerNameTextView.setText("Unknown User");
            binding.profilePictureImageView.setImageResource(R.drawable.ic_person);
            return;
        }
        
        firebaseService.getUserById(userId, new FirebaseService.OnUserFetchListener() {
            @Override
            public void onSuccess(com.example.khaddobondhu.model.User user) {
                // Update UI on main thread
                runOnUiThread(() -> {
                    // Set user name
                    binding.sellerNameTextView.setText(user.getName() != null ? user.getName() : "Unknown User");
                    
                    // Set profile picture
                    if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                        Glide.with(PostDetailActivity.this)
                            .load(user.getProfilePictureUrl())
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.profilePictureImageView);
                    } else {
                        binding.profilePictureImageView.setImageResource(R.drawable.ic_person);
                    }
                    
                    // Set up click listeners for profile navigation
                    setupProfileClickListeners(userId, user.getName());
                });
            }
            
            @Override
            public void onError(Exception e) {
                // Set default values on error
                runOnUiThread(() -> {
                    binding.sellerNameTextView.setText("Unknown User");
                    binding.profilePictureImageView.setImageResource(R.drawable.ic_person);
                });
            }
        });
    }
    
    private void setupProfileClickListeners(String userId, String userName) {
        // Click listener for profile picture
        binding.profilePictureImageView.setOnClickListener(v -> {
            Intent intent = UserProfileViewActivity.newIntent(this, userId, userName);
            startActivity(intent);
        });
        
        // Click listener for user name
        binding.sellerNameTextView.setOnClickListener(v -> {
            Intent intent = UserProfileViewActivity.newIntent(this, userId, userName);
            startActivity(intent);
        });
    }
    
    private void loadPostStatistics() {
        if (foodPost == null) return;
        
        firebaseService.getPostStatistics(foodPost.getId(), new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    int views = firebaseService.getCurrentPostViews();
                    int requests = firebaseService.getCurrentPostRequests();
                    
                    // Update UI with statistics
                    binding.textViewViews.setText(views + " views");
                    binding.textViewRequests.setText(requests + " requests");
                });
            }

            @Override
            public void onError(String error) {
                // Handle error silently
                Log.w("PostDetailActivity", "Failed to load statistics: " + error);
            }
        });
    }

    private void setPostTypeBackground(String postType) {
        switch (postType) {
            case "DONATE":
                binding.textViewPostType.setBackgroundResource(R.drawable.bg_post_type);
                binding.textViewPostType.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "SELL":
                binding.textViewPostType.setBackgroundResource(R.drawable.bg_post_type);
                binding.textViewPostType.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "REQUEST_DONATION":
                binding.textViewPostType.setBackgroundResource(R.drawable.bg_post_type);
                binding.textViewPostType.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case "REQUEST_TO_BUY":
                binding.textViewPostType.setBackgroundResource(R.drawable.bg_post_type);
                binding.textViewPostType.setTextColor(getResources().getColor(android.R.color.white));
                break;
            default:
                binding.textViewPostType.setBackgroundResource(android.R.color.transparent);
                binding.textViewPostType.setTextColor(getResources().getColor(android.R.color.black));
                break;
        }
    }
    
    private void setRequestButtonText(String postType) {
        String buttonText = "";
        switch (postType) {
            case "DONATE":
                buttonText = "Request to Get";
                break;
            case "SELL":
                buttonText = "Request to Buy";
                break;
            case "REQUEST_DONATION":
                buttonText = "Want to Donate";
                break;
            case "REQUEST_TO_BUY":
                buttonText = "Want to Sell";
                break;
            default:
                buttonText = "Accept Request";
                break;
        }
        binding.buttonAccceptRequest.setText(buttonText);
    }

    private void setupClickListeners() {
        binding.buttonContact.setOnClickListener(v -> contactPoster());
        binding.buttonMessage.setOnClickListener(v -> sendMessage());
        binding.buttonShare.setOnClickListener(v -> sharePost());
        binding.buttonAccceptRequest.setOnClickListener(v -> showRequestDialog());
    }

    private void contactPoster() {
        if (foodPost == null) {
            Toast.makeText(this, "Post information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if we have permission to make phone calls
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
            return;
        }

        // Fetch the post owner's phone number and make the call
        fetchUserPhoneAndCall();
    }

    private void fetchUserPhoneAndCall() {
        if (foodPost == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.getUserById(foodPost.getUserId(), new FirebaseService.OnUserFetchListener() {
            @Override
            public void onSuccess(com.example.khaddobondhu.model.User user) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (user != null && user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                    // Show confirmation dialog before making the call
                    showCallConfirmationDialog(user.getPhoneNumber(), user.getName());
                } else {
                    Toast.makeText(PostDetailActivity.this, "Phone number not available for this user", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onError(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PostDetailActivity.this, "Failed to get user information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCallConfirmationDialog(String phoneNumber, String userName) {
        new AlertDialog.Builder(this)
            .setTitle("Make Phone Call")
            .setMessage("Do you want to call " + (userName != null ? userName : "this user") + " at " + phoneNumber + "?")
            .setPositiveButton("Call", (dialog, which) -> {
                makePhoneCall(phoneNumber);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to make phone call", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to send messages", Toast.LENGTH_SHORT).show();
            return;
        }

        if (foodPost == null) return;

        // Create a message
        Message message = new Message();
        message.setSenderId(currentUser.getUid());
        message.setReceiverId(foodPost.getUserId());
        message.setContent("Hi! I'm interested in your post: " + foodPost.getTitle());
        message.setChatId(generateChatId(currentUser.getUid(), foodPost.getUserId()));

        firebaseService.sendMessage(message, new OnCompleteListener<com.google.firebase.firestore.DocumentReference>() {
            @Override
            public void onComplete(Task<com.google.firebase.firestore.DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PostDetailActivity.this, "Message sent successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateChatId(String userId1, String userId2) {
        // Create a consistent chat ID regardless of who initiates
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void sharePost() {
        if (foodPost == null) return;

        String shareText = "Check out this food post on KhaddoBondhu:\n\n" +
                          foodPost.getTitle() + "\n" +
                          foodPost.getDescription() + "\n" +
                          "Price: " + foodPost.getFormattedPrice() + "\n" +
                          "Location: " + foodPost.getPickupLocation();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the call
                fetchUserPhoneAndCall();
            } else {
                // Permission denied
                Toast.makeText(this, "Phone call permission is required to contact the user", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showRequestDialog() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to make a request", Toast.LENGTH_SHORT).show();
            return;
        }

        if (foodPost == null) {
            Toast.makeText(this, "Post information not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is trying to request their own post
        if (currentUser.getUid().equals(foodPost.getUserId())) {
            Toast.makeText(this, "You cannot request your own post", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user has already requested this post
        firebaseService.checkExistingRequest(foodPost.getId(), currentUser.getUid(), new FirebaseService.OnRequestListener() {
            @Override
            public void onSuccess() {
                // No existing request found, show the request dialog
                showRequestInputDialog();
            }

            @Override
            public void onError(String error) {
                if (error.contains("already requested")) {
                    Toast.makeText(PostDetailActivity.this, "You have already requested this post", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showRequestInputDialog() {
        // Create a dialog with an EditText for the request message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make Request");

        // Set up the input
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Add a message (optional)");
        input.setMinLines(3);
        input.setMaxLines(5);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Send Request", (dialog, which) -> {
            String message = input.getText().toString().trim();
            createRequest(message);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createRequest(String message) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || foodPost == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);

        // Get current user's profile information
        firebaseService.getUserById(currentUser.getUid(), new FirebaseService.OnUserFetchListener() {
            @Override
            public void onSuccess(com.example.khaddobondhu.model.User user) {
                // Determine request type based on post type
                String requestType = getRequestTypeFromPostType(foodPost.getPostType());

                // Create the request
                Request request = new Request(
                    foodPost.getId(),
                    foodPost.getTitle(),
                    currentUser.getUid(),
                    user.getName(),
                    user.getProfilePictureUrl(),
                    foodPost.getUserId(),
                    "Unknown User", // Will be fetched dynamically when needed
                    requestType,
                    message
                );

                // Save the request to Firebase
                firebaseService.createRequest(request, new FirebaseService.OnRequestListener() {
                    @Override
                    public void onSuccess() {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostDetailActivity.this, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                        
                        // Increment the request count for the post
                        firebaseService.incrementPostRequests(foodPost.getId());
                        
                        // Send notification to post owner
                        sendRequestNotificationToOwner(request);
                    }

                    @Override
                    public void onError(String error) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostDetailActivity.this, "Failed to send request: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(PostDetailActivity.this, "Failed to get user information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRequestTypeFromPostType(String postType) {
        switch (postType) {
            case "DONATE":
                return "REQUEST_TO_GET";
            case "SELL":
                return "REQUEST_TO_BUY";
            case "REQUEST_DONATION":
                return "WANT_TO_DONATE";
            case "REQUEST_TO_BUY":
                return "WANT_TO_SELL";
            default:
                return "REQUEST_TO_GET";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    /**
     * Generate category-specific notification message
     */
    private String generateNotificationMessage(String requesterName, String postTitle, String requestType) {
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
     * Send notification to post owner when a request is created
     */
    private void sendRequestNotificationToOwner(Request request) {
        Log.d("PostDetailActivity", "Sending notification to post owner: " + request.getPostOwnerId());
        Log.d("PostDetailActivity", "Requester: " + request.getRequesterName() + " (ID: " + request.getRequesterId() + ")");
        Log.d("PostDetailActivity", "Post: " + request.getPostTitle() + " (ID: " + request.getPostId() + ")");
        
        // Create notification for post owner with category-specific text
        String notificationTitle = "🍽️ New Food Request";
        String notificationMessage = generateNotificationMessage(request.getRequesterName(), request.getPostTitle(), request.getRequestType());
        
        com.example.khaddobondhu.model.Notification notification = new com.example.khaddobondhu.model.Notification(
            request.getPostOwnerId(), // recipient
            notificationTitle,
            notificationMessage,
            "REQUEST_RECEIVED",
            request.getPostId(), // related post ID
            request.getRequesterId(), // sender
            request.getRequesterName(),
            request.getRequesterProfilePictureUrl()
        );
        
        // Save notification to Firestore
        firebaseService.createNotification(notification, new FirebaseService.OnNotificationListener() {
            @Override
            public void onSuccess() {
                Log.d("PostDetailActivity", "Notification saved to Firestore successfully");
                // Note: We don't show notification here because we can't show it to another user
                // The notification will be shown when the post owner opens the app or receives FCM
            }
            
            @Override
            public void onError(String error) {
                Log.e("PostDetailActivity", "Failed to create notification: " + error);
            }
        });
    }
} 