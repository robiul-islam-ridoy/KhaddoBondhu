package com.example.khaddobondhu.ui.profile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.databinding.FragmentProfileBinding;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.CloudinaryService;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.auth.LoginActivity;
import com.example.khaddobondhu.ui.image.ImagePreviewActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.Timestamp;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.widget.AutoCompleteTextView;
import android.widget.NumberPicker;
import android.app.DatePickerDialog;
import android.app.Activity;
import android.view.ViewParent;
import android.app.TimePickerDialog;

public class ProfileFragment extends Fragment implements UserPostAdapter.OnPostActionListener {
    private FragmentProfileBinding binding;
    private FirebaseService firebaseService;
    private UserPostAdapter adapter;
    private List<FoodPost> userPosts = new ArrayList<>();
    private static final int REQUEST_IMAGE_PICK = 1001;
    private CloudinaryService cloudinaryService;
    private static Uri selectedImageUri = null;
    private ImageView currentDialogImageView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        
        firebaseService = new FirebaseService();
        
        setupRecyclerView();
        loadUserProfile();
        loadUserPosts();
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        cloudinaryService = new CloudinaryService(requireContext());
        
        // Setup click listeners
        binding.buttonEditProfile.setOnClickListener(v -> {
            showEditProfileDialog();
        });
    }
    

    
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void logout() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                firebaseService.signOut();
                // Navigate to login activity
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        // Initialize dialog views
        EditText nameInput = dialogView.findViewById(R.id.nameInput);
        EditText phoneInput = dialogView.findViewById(R.id.phoneInput);
        EditText bioInput = dialogView.findViewById(R.id.bioInput);
        ImageView profilePictureImageView = dialogView.findViewById(R.id.profilePictureImageView);
        FloatingActionButton changePictureButton = dialogView.findViewById(R.id.changePictureButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);
        
        // Store reference to the dialog's ImageView for preview updates
        currentDialogImageView = profilePictureImageView;

        // Pre-fill current data
        nameInput.setText(firebaseService.getCurrentUserName());
        phoneInput.setText(firebaseService.getCurrentUserPhone());
        bioInput.setText(firebaseService.getCurrentUserBio());
        String profilePicUrl = firebaseService.getCurrentUserProfilePictureUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Glide.with(this)
                .load(profilePicUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(profilePictureImageView);
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Profile picture selection
        changePictureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Save button
        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String bio = bioInput.getText().toString().trim();

            if (name.isEmpty()) {
                nameInput.setError("Name is required");
                return;
            }

            // Show progress
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");

            // Update profile with image if selected
            Uri selectedUri = getSelectedImageUri();
            if (selectedUri != null) {
                updateProfileWithImage(name, phone, bio, selectedUri.toString(), dialog, saveButton);
            } else {
                updateProfile(name, phone, bio, dialog, saveButton);
            }
        });

        dialog.show();
        
        // Clear the reference when dialog is dismissed
        dialog.setOnDismissListener(dialogInterface -> {
            currentDialogImageView = null;
        });
    }

    private void updateProfileWithImage(String name, String phone, String bio, String imagePath, AlertDialog dialog, Button saveButton) {
        cloudinaryService.uploadImage(Uri.parse(imagePath), "profile_pics", new com.google.android.gms.tasks.OnCompleteListener<String>() {
            public void onComplete(com.google.android.gms.tasks.Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult();
                    
                    // Get current profile picture URL for deletion
                    String currentProfilePic = firebaseService.getCurrentUserProfilePictureUrl();
                    
                    // Update profile on main thread
                    requireActivity().runOnUiThread(() -> {
                        firebaseService.updateUserProfile(name, phone, bio, imageUrl, new FirebaseService.Callback() {
                            public void onSuccess() {
                                // Delete old profile picture if it exists and is different
                                if (currentProfilePic != null && !currentProfilePic.isEmpty() && !currentProfilePic.equals(imageUrl)) {
                                    cloudinaryService.deleteImage(currentProfilePic, new com.google.android.gms.tasks.OnCompleteListener<Boolean>() {
                                        @Override
                                        public void onComplete(com.google.android.gms.tasks.Task<Boolean> deleteTask) {
                                            if (deleteTask.isSuccessful() && deleteTask.getResult()) {
                                                Log.d("ProfileFragment", "Old profile picture deleted successfully");
                                            } else {
                                                Log.w("ProfileFragment", "Failed to delete old profile picture: " + (deleteTask.getException() != null ? deleteTask.getException().getMessage() : "Unknown error"));
                                            }
                                        }
                                    });
                                }
                                
                                requireActivity().runOnUiThread(() -> {
                                    dialog.dismiss();
                                    loadUserProfile();
                                    showSuccessMessage("Profile updated successfully!");
                                });
                            }
                            public void onError(String error) {
                                requireActivity().runOnUiThread(() -> {
                                    saveButton.setEnabled(true);
                                    saveButton.setText("Save Changes");
                                    showErrorMessage("Failed to update profile: " + error);
                                });
                            }
                        });
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        saveButton.setEnabled(true);
                        saveButton.setText("Save Changes");
                        showErrorMessage("Failed to upload image: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    });
                }
            }
        });
    }

    private void updateProfile(String name, String phone, String bio, AlertDialog dialog, Button saveButton) {
        firebaseService.updateUserProfile(name, phone, bio, null, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    dialog.dismiss();
                    loadUserProfile();
                    showSuccessMessage("Profile updated successfully!");
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Changes");
                    showErrorMessage("Failed to update profile: " + error);
                });
            }
        });
    }

    private void setupRecyclerView() {
        binding.userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new UserPostAdapter(requireContext(), userPosts, this);
        binding.userPostsRecyclerView.setAdapter(adapter);
        
        // Add item decoration for bottom spacing
        binding.userPostsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 200; // Add bottom spacing for last item
                }
            }
        });
    }

    private void loadUserProfile() {
        String currentUserId = firebaseService.getCurrentUserId();
        if (currentUserId == null) {
            // User not logged in, redirect to login
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            return;
        }

        // Load user profile from Firestore
        firebaseService.getUserProfileData(currentUserId, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    // Update UI with user profile data
                    String userName = firebaseService.getCurrentUserName();
                    String userEmail = firebaseService.getCurrentUserEmail();
                    String userPhone = firebaseService.getCurrentUserPhone();
                    String userBio = firebaseService.getCurrentUserBio();
                    String userProfilePic = firebaseService.getCurrentUserProfilePictureUrl();
                    
                    binding.textViewName.setText(userName);
                    binding.textViewEmail.setText(userEmail);
                    
                    // Show phone number
                    if (userPhone != null && !userPhone.isEmpty() && !"No phone".equals(userPhone)) {
                        binding.textViewPhone.setText(userPhone);
                        binding.textViewPhone.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewPhone.setText("No phone");
                        binding.textViewPhone.setVisibility(View.VISIBLE);
                    }
                    
                    // Show bio
                    if (userBio != null && !userBio.isEmpty() && !"No bio".equals(userBio)) {
                        binding.textViewBio.setText(userBio);
                        binding.textViewBio.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewBio.setText("No bio available");
                        binding.textViewBio.setVisibility(View.VISIBLE);
                    }
                    
                    // Load profile picture
                    if (userProfilePic != null && !userProfilePic.isEmpty()) {
                        Glide.with(ProfileFragment.this)
                            .load(userProfilePic)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(binding.profilePictureImageView);
                        
                        // Add click listener for full preview
                        binding.profilePictureImageView.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), ImagePreviewActivity.class);
                            intent.putExtra("image_url", userProfilePic);
                            intent.putExtra("image_title", "Profile Picture");
                            startActivity(intent);
                        });
                    } else {
                        // Remove click listener if no image
                        binding.profilePictureImageView.setOnClickListener(null);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to load profile: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loadUserPosts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyStateTextView.setVisibility(View.GONE);
        
        String currentUserId = firebaseService.getCurrentUserId();
        if (currentUserId == null) return;

        firebaseService.getUserFoodPosts(currentUserId, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    userPosts = firebaseService.getUserPosts();
                    
                    if (userPosts.isEmpty()) {
                        binding.emptyStateTextView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.updatePosts(userPosts);
                        updateStats();
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.emptyStateTextView.setVisibility(View.VISIBLE);
                    binding.emptyStateTextView.setText("Error loading posts: " + error);
                });
            }
        });
    }

    private void updateStats() {
        int totalPosts = userPosts.size();
        int totalViews = 0;
        int totalRequests = 0;
        
        for (FoodPost post : userPosts) {
            totalViews += post.getViews();
            totalRequests += post.getRequests();
        }
        
        binding.totalPostsTextView.setText(String.valueOf(totalPosts));
        binding.totalViewsTextView.setText(String.valueOf(totalViews));
        binding.totalRequestsTextView.setText(String.valueOf(totalRequests));
        // binding.postCountTextView.setText(totalPosts + " posts");
    }

    @Override
    public void onEditPost(FoodPost post) {
        // This method is no longer used since we now use EditPostActivity
        // But keeping it for interface compatibility
    }
    
    // Removed showEditPostDialog and related methods - now using EditPostActivity

    @Override
    public void onDeletePost(FoodPost post) {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // Delete images first if they exist
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            deletePostImages(post.getImageUrls(), () -> {
                // After images are deleted, delete the post
                deletePostFromFirebase(post);
            });
        } else {
            // No images to delete, just delete the post
            deletePostFromFirebase(post);
        }
    }
    
    private void deletePostImages(List<String> imageUrls, Runnable onComplete) {
        final int[] deletedCount = {0};
        final int totalImages = imageUrls.size();
        
        for (String imageUrl : imageUrls) {
            cloudinaryService.deleteImage(imageUrl, new com.google.android.gms.tasks.OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(com.google.android.gms.tasks.Task<Boolean> task) {
                    deletedCount[0]++;
                    if (task.isSuccessful() && task.getResult()) {
                        Log.d("ProfileFragment", "Post image deleted successfully: " + imageUrl);
                    } else {
                        Log.w("ProfileFragment", "Failed to delete post image: " + imageUrl);
                    }
                    
                    // Check if all images have been processed
                    if (deletedCount[0] >= totalImages) {
                        onComplete.run();
                    }
                }
            });
        }
    }
    
    private void deletePostFromFirebase(FoodPost post) {
        firebaseService.deleteFoodPost(post.getId(), task -> {
            requireActivity().runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUserPosts(); // Reload posts
                } else {
                    Toast.makeText(requireContext(), "Failed to delete post: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to the fragment
        loadUserProfile();
        loadUserPosts();
    }

    private void showSuccessMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Store the selected image URI for later use
                ProfileFragment.selectedImageUri = selectedImageUri;
                
                // Show preview in the current dialog
                showImagePreviewInDialog(selectedImageUri);
            }
        }
    }
    
    private void showImagePreviewInDialog(Uri imageUri) {
        // Update the dialog's ImageView directly using the stored reference
        if (currentDialogImageView != null) {
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .circleCrop()
                .into(currentDialogImageView);
        }
    }
    
    // Method to get and clear the selected image URI
    private Uri getSelectedImageUri() {
        Uri uri = selectedImageUri;
        selectedImageUri = null; // Clear after use
        return uri;
    }
    
    // Public method to refresh data when navigating to this fragment
    public void refreshData() {
        if (isAdded() && !isDetached()) {
            loadUserProfile();
            loadUserPosts();
        }
    }
} 