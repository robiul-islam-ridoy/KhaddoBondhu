package com.example.khaddobondhu.ui.post;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.service.CloudinaryService;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditPostActivity extends AppCompatActivity {
    
    private EditText titleInput, descriptionInput, priceInput, quantityInput, locationInput, expiryDateEditText;
    private AutoCompleteTextView postTypeSpinner, foodTypeSpinner, quantityUnitSpinner;
    private ImageView postImageView;
    private FloatingActionButton changeImageButton;
    private Button saveButton;
    private ProgressBar progressBar;
    
    private FirebaseService firebaseService;
    private CloudinaryService cloudinaryService;
    private FoodPost currentPost;
    private Uri selectedImageUri;
    private Calendar expiryCalendar;
    private SimpleDateFormat dateFormat;
    
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        
        // Initialize services
        firebaseService = new FirebaseService();
        cloudinaryService = new CloudinaryService(this);
        
        // Initialize date formatter
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        expiryCalendar = Calendar.getInstance();
        
        // Get post data from intent
        String postId = getIntent().getStringExtra("post_id");
        if (postId == null) {
            Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setupViews();
        setupImagePicker();
        loadPostData(postId);
        setupClickListeners();
    }
    
    private void setupViews() {
        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        postTypeSpinner = findViewById(R.id.postTypeSpinner);
        foodTypeSpinner = findViewById(R.id.foodTypeSpinner);
        priceInput = findViewById(R.id.priceInput);
        quantityInput = findViewById(R.id.quantityInput);
        quantityUnitSpinner = findViewById(R.id.quantityUnitSpinner);
        locationInput = findViewById(R.id.locationInput);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        postImageView = findViewById(R.id.postImageView);
        changeImageButton = findViewById(R.id.changeImageButton);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
        
        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Post");
        }
        
        // Setup spinners
        setupSpinners();
    }
    
    private void setupSpinners() {
        // Post Type Spinner
        String[] postTypes = {"DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"};
        ArrayAdapter<String> postTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, postTypes);
        postTypeSpinner.setAdapter(postTypeAdapter);
        
        // Food Type Spinner
        String[] foodTypes = {"Rice", "Curry", "Snacks", "Fruits", "Vegetables", "Bread", "Dessert", "Other"};
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, foodTypes);
        foodTypeSpinner.setAdapter(foodTypeAdapter);
        
        // Quantity Unit Spinner
        String[] units = {"kg", "g", "pieces", "packets", "liters", "ml", "dozen"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, units);
        quantityUnitSpinner.setAdapter(unitAdapter);
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        Glide.with(this)
                            .load(selectedImageUri)
                            .placeholder(R.drawable.placeholder_food)
                            .error(R.drawable.placeholder_food)
                            .centerCrop()
                            .into(postImageView);
                    }
                }
            }
        );
    }
    
    private void loadPostData(String postId) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.getFoodPostById(postId, new OnCompleteListener<com.google.firebase.firestore.DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<com.google.firebase.firestore.DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    currentPost = task.getResult().toObject(FoodPost.class);
                    if (currentPost != null) {
                        currentPost.setId(task.getResult().getId());
                        populateFields();
                    } else {
                        Toast.makeText(EditPostActivity.this, "Failed to load post data", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(EditPostActivity.this, "Post not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
    
    private void populateFields() {
        if (currentPost == null) return;
        
        // Populate text fields
        titleInput.setText(currentPost.getTitle());
        descriptionInput.setText(currentPost.getDescription());
        priceInput.setText(String.valueOf(currentPost.getPrice()));
        quantityInput.setText(String.valueOf(currentPost.getQuantity()));
        locationInput.setText(currentPost.getPickupLocation());
        
        // Set spinner values
        postTypeSpinner.setText(currentPost.getPostType(), false);
        foodTypeSpinner.setText(currentPost.getFoodType(), false);
        quantityUnitSpinner.setText(currentPost.getQuantityUnit(), false);
        
        // Set expiry date
        if (currentPost.getExpiryDate() != null) {
            expiryCalendar.setTime(currentPost.getExpiryDate().toDate());
            expiryDateEditText.setText(dateFormat.format(currentPost.getExpiryDate().toDate()));
        }
        
        // Load image
        if (currentPost.getImageUrls() != null && !currentPost.getImageUrls().isEmpty()) {
            Glide.with(this)
                .load(currentPost.getImageUrls().get(0))
                .placeholder(R.drawable.placeholder_food)
                .error(R.drawable.placeholder_food)
                .centerCrop()
                .into(postImageView);
        }
    }
    
    private void setupClickListeners() {
        // Change image button
        changeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        
        // Expiry date picker
        expiryDateEditText.setOnClickListener(v -> showDateTimePicker());
        
        // Save button
        saveButton.setOnClickListener(v -> saveChanges());
    }
    
    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.HOUR, 1); // Minimum 1 hour from now
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                expiryCalendar.set(Calendar.YEAR, year);
                expiryCalendar.set(Calendar.MONTH, month);
                expiryCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Show time picker after date is selected
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (timeView, hourOfDay, minute) -> {
                        expiryCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        expiryCalendar.set(Calendar.MINUTE, minute);
                        expiryDateEditText.setText(dateFormat.format(expiryCalendar.getTime()));
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
    }
    
    private void saveChanges() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }
        
        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);
        saveButton.setText("Saving...");
        
        // Get form data
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String postType = postTypeSpinner.getText().toString();
        String foodType = foodTypeSpinner.getText().toString();
        String quantityUnit = quantityUnitSpinner.getText().toString();
        String location = locationInput.getText().toString().trim();
        
        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceInput.getText().toString());
            quantity = Integer.parseInt(quantityInput.getText().toString());
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter valid price and quantity");
            return;
        }
        
        // Get expiry date
        Timestamp expiryDate = null;
        if (!expiryDateEditText.getText().toString().isEmpty()) {
            expiryDate = new Timestamp(expiryCalendar.getTime());
        }
        
        // Update post with image if selected
        if (selectedImageUri != null) {
            updatePostWithImage(title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate);
        } else {
            updatePost(title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate);
        }
    }
    
    private void updatePost(String title, String description, String postType, double price, int quantity, String quantityUnit, String foodType, String location, Timestamp expiryDate) {
        updatePostWithData(title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, null);
    }
    
    private void updatePostWithImage(String title, String description, String postType, double price, int quantity, String quantityUnit, String foodType, String location, Timestamp expiryDate) {
        cloudinaryService.uploadImage(selectedImageUri, "food_posts", new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String imageUrl = task.getResult();
                    List<String> imageUrls = Arrays.asList(imageUrl);
                    updatePostWithData(title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, imageUrls);
                } else {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        saveButton.setEnabled(true);
                        saveButton.setText("Save Changes");
                        String errorMsg = "Failed to upload image";
                        if (task.getException() != null) {
                            errorMsg += ": " + task.getException().getMessage();
                        }
                        showErrorMessage(errorMsg);
                    });
                }
            }
        });
    }
    
    private void updatePostWithData(String title, String description, String postType, double price, int quantity, String quantityUnit, String foodType, String location, Timestamp expiryDate, List<String> imageUrls) {
        firebaseService.updateFoodPost(currentPost.getId(), title, description, postType, price, quantity, quantityUnit, foodType, location, expiryDate, imageUrls, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Changes");
                    Toast.makeText(EditPostActivity.this, "Post updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    saveButton.setText("Save Changes");
                    showErrorMessage("Failed to update post: " + error);
                });
            }
        });
    }
    
    private boolean validateInputs() {
        if (titleInput.getText().toString().trim().isEmpty()) {
            titleInput.setError("Title is required");
            return false;
        }
        
        if (descriptionInput.getText().toString().trim().isEmpty()) {
            descriptionInput.setError("Description is required");
            return false;
        }
        
        if (postTypeSpinner.getText().toString().isEmpty()) {
            postTypeSpinner.setError("Post type is required");
            return false;
        }
        
        if (foodTypeSpinner.getText().toString().isEmpty()) {
            foodTypeSpinner.setError("Food type is required");
            return false;
        }
        
        if (quantityUnitSpinner.getText().toString().isEmpty()) {
            quantityUnitSpinner.setError("Quantity unit is required");
            return false;
        }
        
        if (locationInput.getText().toString().trim().isEmpty()) {
            locationInput.setError("Location is required");
            return false;
        }
        
        return true;
    }
    
    private void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("Discard Changes")
            .setMessage("Are you sure you want to discard your changes?")
            .setPositiveButton("Discard", (dialog, which) -> {
                setResult(RESULT_CANCELED);
                super.onBackPressed();
            })
            .setNegativeButton("Keep Editing", null)
            .show();
    }
} 