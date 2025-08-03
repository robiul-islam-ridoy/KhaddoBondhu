package com.example.khaddobondhu.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.utils.UserRoleUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProfileViewActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileViewActivity";
    private static final String EXTRA_USER_ID = "user_id";
    private static final String EXTRA_USER_NAME = "user_name";

    // Views
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private TextView textViewName, textViewEmail, textViewPhone, textViewBio, textViewUserType;
    private TextView textViewPostsCount, textViewRating, textViewViews, textViewEmptyPosts;
    private RecyclerView recyclerViewPosts;
    private ImageView profilePictureImageView;

    // Data
    private FirebaseService firebaseService;
    private UserPostAdapter adapter;
    private List<FoodPost> userPosts = new ArrayList<>();
    private String userId;
    private String userName;

    public static Intent newIntent(Context context, String userId, String userName) {
        Intent intent = new Intent(context, UserProfileViewActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_USER_NAME, userName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        // Get user data from intent
        userId = getIntent().getStringExtra(EXTRA_USER_ID);
        userName = getIntent().getStringExtra(EXTRA_USER_NAME);

        if (userId == null) {
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize services
        firebaseService = new FirebaseService();

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Load user data
        loadUserProfile();
        loadUserPosts();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewBio = findViewById(R.id.textViewBio);
        textViewUserType = findViewById(R.id.textViewUserType);
        textViewPostsCount = findViewById(R.id.textViewPostsCount);
        textViewRating = findViewById(R.id.textViewRating);
        textViewViews = findViewById(R.id.textViewViews);
        textViewEmptyPosts = findViewById(R.id.textViewEmptyPosts);
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        profilePictureImageView = findViewById(R.id.profilePictureImageView);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(userName != null ? userName : "User Profile");
        }
    }

    private void setupRecyclerView() {
        adapter = new UserPostAdapter(this, userPosts, new UserPostAdapter.OnPostActionListener() {
            @Override
            public void onEditPost(FoodPost post) {
                // Read-only view - no editing allowed
                Toast.makeText(UserProfileViewActivity.this, "This is a read-only view", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeletePost(FoodPost post) {
                // Read-only view - no deleting allowed
                Toast.makeText(UserProfileViewActivity.this, "This is a read-only view", Toast.LENGTH_SHORT).show();
            }
        }, true); // Set to read-only mode
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPosts.setAdapter(adapter);
    }

    private void loadUserProfile() {
        showProgress(true);
        Log.d(TAG, "Loading profile for user ID: " + userId);
        
        firebaseService.getUserById(userId, new FirebaseService.OnUserFetchListener() {
            @Override
            public void onSuccess(User user) {
                showProgress(false);
                Log.d(TAG, "Successfully loaded user profile: " + user.getName());
                displayUserProfile(user);
            }

            @Override
            public void onError(Exception e) {
                showProgress(false);
                Log.e(TAG, "Error loading user profile for user: " + userId, e);
                Toast.makeText(UserProfileViewActivity.this, 
                    "Error loading user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayUserProfile(User user) {
        Log.d(TAG, "Displaying user profile for: " + user.getName());
        
        // Set user name
        textViewName.setText(user.getName() != null ? user.getName() : "Unknown User");

        // Set email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            textViewEmail.setText(user.getEmail());
        } else {
            textViewEmail.setText("Email not available");
        }

        // Set phone
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            textViewPhone.setText(user.getPhoneNumber());
        } else {
            textViewPhone.setText("Phone not available");
        }

        // Set bio
        if (user.getDescription() != null && !user.getDescription().isEmpty()) {
            textViewBio.setText(user.getDescription());
        } else {
            textViewBio.setText("No bio available");
        }

        // Set user type badge
        if (user.getUserType() != null) {
            textViewUserType.setText(UserRoleUtils.getUserTypeDisplayName(user.getUserType()));
            textViewUserType.setBackgroundResource(UserRoleUtils.getUserTypeBadgeDrawable(user.getUserType()));
        } else {
            textViewUserType.setText("Individual");
            textViewUserType.setBackgroundResource(R.drawable.badge_individual);
        }

        // Set stats - these will be updated when posts are loaded
        textViewPostsCount.setText("0");
        textViewRating.setText("0.0");
        textViewViews.setText("0");

        // Load profile picture
        Log.d(TAG, "Loading profile picture. URL: " + user.getProfilePictureUrl());
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
//            Glide.with(this)
//                    .load(user.getProfilePictureUrl())
//                    .placeholder(R.drawable.ic_person)
//                    .error(R.drawable.ic_person)
//                    .centerCrop()
//                    .into((android.widget.ImageView) profilePictureImageView);
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(profilePictureImageView);
            Log.d(TAG, "Profile picture loaded from URL");
        } else {
            ((android.widget.ImageView) profilePictureImageView).setImageResource(R.drawable.ic_person);
            Log.d(TAG, "Profile picture set to default icon");
        }

        // Update toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(user.getName() != null ? user.getName() : "User Profile");
        }
    }

    private void loadUserPosts() {
        showProgress(true);
        Log.d(TAG, "Loading posts for user ID: " + userId);
        
        firebaseService.getPostsByUserId(userId, new FirebaseService.OnPostsFetchListener() {
            @Override
            public void onSuccess(List<FoodPost> posts) {
                showProgress(false);
                Log.d(TAG, "Successfully loaded " + posts.size() + " posts for user: " + userId);
                userPosts.clear();
                userPosts.addAll(posts);
                adapter.notifyDataSetChanged();
                updatePostsUI();
                updateStats(posts);
            }

            @Override
            public void onError(Exception e) {
                showProgress(false);
                Log.e(TAG, "Error loading user posts for user: " + userId, e);
                Toast.makeText(UserProfileViewActivity.this, 
                    "Error loading user posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                updatePostsUI();
            }
        });
    }

    private void updateStats(List<FoodPost> posts) {
        Log.d(TAG, "Updating stats for " + posts.size() + " posts");
        
        // Update posts count
        textViewPostsCount.setText(String.valueOf(posts.size()));
        
        // Calculate total views
        int totalViews = 0;
        for (FoodPost post : posts) {
            totalViews += post.getViews();
        }
        textViewViews.setText(String.valueOf(totalViews));
        
        // For now, set rating to 0.0 since rating is not available in FoodPost model
        textViewRating.setText("0.0");
        
        Log.d(TAG, "Stats updated - Posts: " + posts.size() + ", Views: " + totalViews);
    }

    private void updatePostsUI() {
        if (userPosts.isEmpty()) {
            textViewEmptyPosts.setVisibility(View.VISIBLE);
            recyclerViewPosts.setVisibility(View.GONE);
        } else {
            textViewEmptyPosts.setVisibility(View.GONE);
            recyclerViewPosts.setVisibility(View.VISIBLE);
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 