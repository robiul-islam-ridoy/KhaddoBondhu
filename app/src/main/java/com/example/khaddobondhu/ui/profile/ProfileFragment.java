package com.example.khaddobondhu.ui.profile;

<<<<<<< HEAD
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.khaddobondhu.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
=======
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        
        firebaseService = new FirebaseService();
        
        setupRecyclerView();
        loadUserProfile();
        loadUserPosts();
        
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
        return binding.getRoot();
    }

    @Override
<<<<<<< HEAD
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
=======
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
    }

    private void updateProfileWithImage(String name, String phone, String bio, String imagePath, AlertDialog dialog, Button saveButton) {
        cloudinaryService.uploadImage(Uri.parse(imagePath), "profile_pics", new com.google.android.gms.tasks.OnCompleteListener<String>() {
            public void onComplete(com.google.android.gms.tasks.Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult();
                    // Update profile on main thread
                    requireActivity().runOnUiThread(() -> {
                        firebaseService.updateUserProfile(name, phone, bio, imageUrl, new FirebaseService.Callback() {
                            public void onSuccess() {
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
        showEditPostDialog(post);
    }
    
    private void showEditPostDialog(FoodPost post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_post, null);
        builder.setView(dialogView);

        // Initialize dialog views
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        AutoCompleteTextView postTypeSpinner = dialogView.findViewById(R.id.postTypeSpinner);
        AutoCompleteTextView foodTypeSpinner = dialogView.findViewById(R.id.foodTypeSpinner);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText quantityInput = dialogView.findViewById(R.id.quantityInput);
        AutoCompleteTextView quantityUnitSpinner = dialogView.findViewById(R.id.quantityUnitSpinner);
        EditText locationInput = dialogView.findViewById(R.id.locationInput);
        EditText expiryDateEditText = dialogView.findViewById(R.id.expiryDateEditText);
        ImageView postImageView = dialogView.findViewById(R.id.postImageView);
        FloatingActionButton changeImageButton = dialogView.findViewById(R.id.changeImageButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        // Pre-fill current data
        titleInput.setText(post.getTitle());
        descriptionInput.setText(post.getDescription());
        priceInput.setText(String.valueOf(post.getPrice()));
        quantityInput.setText(String.valueOf(post.getQuantity()));
        locationInput.setText(post.getPickupLocation());

        // Setup spinners
        String[] postTypes = {"DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"};
        String[] foodTypes = {"Rice", "Curry", "Snacks", "Fruits", "Vegetables", "Bread", "Dessert", "Other"};
        String[] quantityUnits = {"servings", "pieces", "kg", "grams", "liters", "packets"};

        ArrayAdapter<String> postTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, postTypes);
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, foodTypes);
        ArrayAdapter<String> quantityUnitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, quantityUnits);

        postTypeSpinner.setAdapter(postTypeAdapter);
        foodTypeSpinner.setAdapter(foodTypeAdapter);
        quantityUnitSpinner.setAdapter(quantityUnitAdapter);

        postTypeSpinner.setText(post.getPostType(), false);
        foodTypeSpinner.setText(post.getFoodType(), false);
        quantityUnitSpinner.setText(post.getQuantityUnit(), false);
        
        // Set threshold to 0 to show dropdown immediately
        postTypeSpinner.setThreshold(0);
        foodTypeSpinner.setThreshold(0);
        quantityUnitSpinner.setThreshold(0);
        
        // Set click listeners to show dropdown
        postTypeSpinner.setOnClickListener(v -> postTypeSpinner.showDropDown());
        foodTypeSpinner.setOnClickListener(v -> foodTypeSpinner.showDropDown());
        quantityUnitSpinner.setOnClickListener(v -> quantityUnitSpinner.showDropDown());

        // Set expiry date if available
        if (post.getExpiryDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            expiryDateEditText.setText(sdf.format(post.getExpiryDate().toDate()));
        }

        // Load current post image
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            Glide.with(this)
                .load(post.getImageUrls().get(0))
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .into(postImageView);
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Image selection
        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        // Expiry date picker
        expiryDateEditText.setOnClickListener(v -> {
            Calendar currentDate = Calendar.getInstance();
            Calendar minDate = Calendar.getInstance();
            minDate.add(Calendar.HOUR, 1);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                        requireContext(),
                        (timeView, hourOfDay, minute) -> {
                            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            selectedDate.set(Calendar.MINUTE, minute);
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                            expiryDateEditText.setText(sdf.format(selectedDate.getTime()));
                        },
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        true
                    );
                    timePickerDialog.show();
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            datePickerDialog.show();
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Save button
        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String postType = postTypeSpinner.getText().toString();
            String foodType = foodTypeSpinner.getText().toString();
            String quantityUnit = quantityUnitSpinner.getText().toString();
            String location = locationInput.getText().toString().trim();
            String expiryDate = expiryDateEditText.getText().toString();

            // Validation
            if (title.isEmpty()) {
                titleInput.setError("Title is required");
                return;
            }
            if (description.isEmpty()) {
                descriptionInput.setError("Description is required");
                return;
            }
            if (postType.isEmpty()) {
                postTypeSpinner.setError("Post type is required");
                return;
            }
            if (foodType.isEmpty()) {
                foodTypeSpinner.setError("Food type is required");
                return;
            }
            if (quantityUnit.isEmpty()) {
                quantityUnitSpinner.setError("Quantity unit is required");
                return;
            }
            if (location.isEmpty()) {
                locationInput.setError("Location is required");
                return;
            }

            double price;
            int quantity;
            try {
                price = Double.parseDouble(priceInput.getText().toString());
                quantity = Integer.parseInt(quantityInput.getText().toString());
            } catch (NumberFormatException e) {
                showErrorMessage("Please enter valid price and quantity");
                return;
            }

            // Show progress
            saveButton.setEnabled(false);
            saveButton.setText("Saving...");

            // Update post with image if selected
            Uri selectedUri = getSelectedImageUri();
            if (selectedUri != null) {
                updatePostWithImage(post.getId(), title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, selectedUri.toString(), dialog, saveButton);
            } else {
                updatePost(post.getId(), title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, dialog, saveButton);
            }
        });

        dialog.show();
    }
    
    private void updatePost(String postId, String title, String description, String postType, 
                           double price, int quantity, String quantityUnit, String foodType, String location, String expiryDate, AlertDialog dialog, Button saveButton) {
        updatePostWithData(postId, title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, null, dialog, saveButton);
    }

    private void updatePostWithImage(String postId, String title, String description, String postType, double price, int quantity, String quantityUnit, String foodType, String location, String expiryDate, String imagePath, AlertDialog dialog, Button saveButton) {
        cloudinaryService.uploadImage(Uri.parse(imagePath), "food_posts", new com.google.android.gms.tasks.OnCompleteListener<String>() {
            public void onComplete(com.google.android.gms.tasks.Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult();
                    // Update post data on main thread
                    requireActivity().runOnUiThread(() -> {
                        updatePostWithData(postId, title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, Arrays.asList(imageUrl), dialog, saveButton);
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

    private void updatePostWithData(String postId, String title, String description, String postType, double price, int quantity, String quantityUnit, String foodType, String location, String expiryDate, List<String> imageUrls, AlertDialog dialog, Button saveButton) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Timestamp expiryTimestamp = null;
        if (expiryDate != null && !expiryDate.isEmpty()) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                java.util.Date date = sdf.parse(expiryDate);
                expiryTimestamp = new Timestamp(date);
            } catch (Exception e) {
                // Ignore parse error, keep expiryTimestamp null
            }
        }
        firebaseService.updateFoodPost(postId, title, description, postType, price, quantity, quantityUnit, foodType, location, expiryTimestamp, imageUrls, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    dialog.dismiss();
                    loadUserPosts(); // Refresh the posts list
                    showSuccessMessage("Post updated successfully!");
                });
            }
            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Changes");
                    showErrorMessage("Failed to update post: " + error);
                });
            }
        });
    }

    @Override
    public void onDeletePost(FoodPost post) {
        binding.progressBar.setVisibility(View.VISIBLE);
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
                // Store the selected image path for later use
                // This will be used in the save button click handlers
                // For now, we'll just show a preview
                ImageView imageView = null;
                
                // Find the current dialog's image view
                if (getActivity() != null) {
                    View currentFocus = getActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        ViewParent parent = currentFocus.getParent();
                        while (parent != null) {
                            if (parent instanceof ViewGroup) {
                                ViewGroup viewGroup = (ViewGroup) parent;
                                ImageView foundImageView = viewGroup.findViewById(R.id.profilePictureImageView);
                                if (foundImageView != null) {
                                    imageView = foundImageView;
                                    break;
                                }
                                foundImageView = viewGroup.findViewById(R.id.postImageView);
                                if (foundImageView != null) {
                                    imageView = foundImageView;
                                    break;
                                }
                            }
                            parent = parent.getParent();
                        }
                    }
                }
                
                if (imageView != null) {
                    Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .into(imageView);
                }
                
                // Store the selected image path in a way that can be accessed by the save button
                // We'll use a static variable for simplicity
                ProfileFragment.selectedImageUri = selectedImageUri;
            }
        }
    }
    
    // Method to get and clear the selected image URI
    private Uri getSelectedImageUri() {
        Uri uri = selectedImageUri;
        selectedImageUri = null; // Clear after use
        return uri;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }
} 