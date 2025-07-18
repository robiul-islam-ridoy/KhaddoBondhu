package com.example.khaddobondhu.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.databinding.ActivityPostDetailBinding;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.Message;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
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
            getSupportActionBar().setTitle("Post Details");
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

        // Load image
        if (foodPost.getImageUrls() != null && !foodPost.getImageUrls().isEmpty()) {
            String imageUrl = foodPost.getImageUrls().get(0);
            
            // Load image from Cloudinary URL
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(binding.imageViewFood);
        } else {
            binding.imageViewFood.setImageResource(R.drawable.placeholder_food);
        }

        // Show contact buttons for all post types
        binding.buttonContact.setVisibility(View.VISIBLE);
        binding.buttonMessage.setVisibility(View.VISIBLE);
        
        // Increment view count (only if not the post owner)
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.getUid().equals(foodPost.getUserId())) {
            firebaseService.incrementPostViews(foodPost.getId());
        }
        
        // Load and display post statistics
        loadPostStatistics();
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

    private void setupClickListeners() {
        binding.buttonContact.setOnClickListener(v -> contactPoster());
        binding.buttonMessage.setOnClickListener(v -> sendMessage());
        binding.buttonShare.setOnClickListener(v -> sharePost());
    }

    private void contactPoster() {
        // For now, just show a toast. In a real app, you'd show contact options
        Toast.makeText(this, "Contact feature coming soon!", Toast.LENGTH_SHORT).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 