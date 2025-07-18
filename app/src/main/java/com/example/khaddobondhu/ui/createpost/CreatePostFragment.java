package com.example.khaddobondhu.ui.createpost;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import android.graphics.Bitmap;
import android.text.TextUtils;
import java.io.IOException;
import androidx.appcompat.app.AlertDialog;
import com.example.khaddobondhu.service.CloudinaryService;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.widget.AutoCompleteTextView;

public class CreatePostFragment extends Fragment {

    private EditText titleInput, descriptionInput, priceInput, quantityInput, locationInput, expiryDateEditText;
    private AutoCompleteTextView postTypeSpinner, foodTypeSpinner, quantityUnitSpinner;
    private ImageView foodImageView;
    private Button addImageButton, createPostButton;
    private ProgressBar progressBar;
    private View uploadPlaceholder;

    private FirebaseService firebaseService;
    private CloudinaryService cloudinaryService;
    private List<Uri> selectedImages;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Calendar expiryCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    // Sample data for spinners
    private String[] postTypes = {"DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"};
    private String[] foodTypes = {"Rice", "Curry", "Snacks", "Fruits", "Vegetables", "Bread", "Dessert", "Other"};
    private String[] quantityUnits = {"servings", "pieces", "kg", "grams", "liters", "packets"};

    public CreatePostFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);

        // Initialize services
        firebaseService = new FirebaseService();
        cloudinaryService = new CloudinaryService(requireContext());
        selectedImages = new ArrayList<>();

        // Initialize views
        initializeViews(view);
        setupSpinners();
        setupImagePicker();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        priceInput = view.findViewById(R.id.priceInput);
        quantityInput = view.findViewById(R.id.quantityInput);
        locationInput = view.findViewById(R.id.locationInput);
        expiryDateEditText = view.findViewById(R.id.expiryDateEditText);
        
        postTypeSpinner = view.findViewById(R.id.postTypeSpinner);
        foodTypeSpinner = view.findViewById(R.id.foodTypeSpinner);
        quantityUnitSpinner = view.findViewById(R.id.quantityUnitSpinner);
        
        foodImageView = view.findViewById(R.id.foodImageView);
        addImageButton = view.findViewById(R.id.addImageButton);
        createPostButton = view.findViewById(R.id.createPostButton);
        progressBar = view.findViewById(R.id.progressBar);
        uploadPlaceholder = view.findViewById(R.id.uploadPlaceholder);
    }

    private void setupSpinners() {
        // Setup post type spinner
        ArrayAdapter<String> postTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, postTypes);
        postTypeSpinner.setAdapter(postTypeAdapter);
        postTypeSpinner.setText(postTypes[0], false);
        postTypeSpinner.setThreshold(0); // Show dropdown immediately on focus
        
        // Setup food type spinner
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, foodTypes);
        foodTypeSpinner.setAdapter(foodTypeAdapter);
        foodTypeSpinner.setText(foodTypes[0], false);
        foodTypeSpinner.setThreshold(0); // Show dropdown immediately on focus
        
        // Setup quantity unit spinner
        ArrayAdapter<String> quantityUnitAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, quantityUnits);
        quantityUnitSpinner.setAdapter(quantityUnitAdapter);
        quantityUnitSpinner.setText(quantityUnits[0], false);
        quantityUnitSpinner.setThreshold(0); // Show dropdown immediately on focus
        
        // Set click listeners to show dropdown
        postTypeSpinner.setOnClickListener(v -> postTypeSpinner.showDropDown());
        foodTypeSpinner.setOnClickListener(v -> foodTypeSpinner.showDropDown());
        quantityUnitSpinner.setOnClickListener(v -> quantityUnitSpinner.showDropDown());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        selectedImages.add(selectedImage);
                        updateImageDisplay();
                    }
                }
            }
        );
    }

    private void setupClickListeners() {
        addImageButton.setOnClickListener(v -> openImagePicker());
        createPostButton.setOnClickListener(v -> createPost());
        
        // Expiry date picker
        expiryDateEditText.setOnClickListener(v -> showDateTimePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void updateImageDisplay() {
        if (!selectedImages.isEmpty()) {
            foodImageView.setVisibility(View.VISIBLE);
            uploadPlaceholder.setVisibility(View.GONE);
            foodImageView.setImageURI(selectedImages.get(0));
        } else {
            foodImageView.setVisibility(View.GONE);
            uploadPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.HOUR, 1); // Minimum 1 hour from now
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                expiryCalendar.set(Calendar.YEAR, year);
                expiryCalendar.set(Calendar.MONTH, month);
                expiryCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Show time picker after date is selected
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
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

    private void createPost() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        createPostButton.setEnabled(false);

        // Handle image upload and post creation
        if (!selectedImages.isEmpty()) {
            uploadImageAndCreatePost();
        } else {
            createFoodPostWithImages(new ArrayList<>());
        }
    }

    private void uploadImageAndCreatePost() {
        if (selectedImages.isEmpty()) {
            // No image selected, create post without image
            createFoodPostWithImages(new ArrayList<>());
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        createPostButton.setEnabled(false);

        // Upload image to Cloudinary
        cloudinaryService.uploadImage(selectedImages.get(0), "food_images", new OnCompleteListener<String>() {
            @Override
            public void onComplete(Task<String> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    // Image uploaded successfully to Cloudinary
                    String imageUrl = task.getResult();
                    List<String> imageUrls = new ArrayList<>();
                    imageUrls.add(imageUrl);
                    
                    // Create food post with Cloudinary image URL
                    createFoodPostWithImages(imageUrls);
                } else {
                    // Failed to upload image, create post without image
                    progressBar.setVisibility(View.GONE);
                    createPostButton.setEnabled(true);
                    String errorMessage = "Failed to upload image";
                    if (task.getException() != null) {
                        errorMessage += ": " + task.getException().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    
                    // Show dialog to continue without image or retry
                    new AlertDialog.Builder(requireContext())
                        .setTitle("Image Upload Failed")
                        .setMessage("Would you like to create the post without an image?")
                        .setPositiveButton("Continue", (dialog, which) -> {
                            createFoodPostWithImages(new ArrayList<>());
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // User cancelled, do nothing
                        })
                        .show();
                }
            }
        });
    }

    private void createFoodPostWithImages(List<String> imageUrls) {
        try {
            // Get form data
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String postType = postTypeSpinner.getText().toString();
            double price = 0.0;
            if (!priceInput.getText().toString().isEmpty()) {
                price = Double.parseDouble(priceInput.getText().toString());
            }
            int quantity = Integer.parseInt(quantityInput.getText().toString());
            String quantityUnit = quantityUnitSpinner.getText().toString();
            String foodType = foodTypeSpinner.getText().toString();
            String pickupLocation = locationInput.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(title)) {
                titleInput.setError("Title is required");
                progressBar.setVisibility(View.GONE);
                createPostButton.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(description)) {
                descriptionInput.setError("Description is required");
                progressBar.setVisibility(View.GONE);
                createPostButton.setEnabled(true);
                return;
            }

            if (TextUtils.isEmpty(pickupLocation)) {
                locationInput.setError("Pickup location is required");
                progressBar.setVisibility(View.GONE);
                createPostButton.setEnabled(true);
                return;
            }

            // Get expiry date
            Timestamp expiryDate = null;
            if (!expiryDateEditText.getText().toString().isEmpty()) {
                expiryDate = new Timestamp(expiryCalendar.getTime());
            }

            // Create FoodPost object
            FoodPost foodPost = new FoodPost();
            foodPost.setUserId(firebaseService.getCurrentUser().getUid());
            foodPost.setTitle(title);
            foodPost.setDescription(description);
            foodPost.setPostType(postType);
            foodPost.setPrice(price);
            foodPost.setQuantity(quantity);
            foodPost.setQuantityUnit(quantityUnit);
            foodPost.setFoodType(foodType);
            foodPost.setPickupLocation(pickupLocation);
            foodPost.setImageUrls(imageUrls);
            foodPost.setExpiryDate(expiryDate);

            // Save to Firestore
            firebaseService.createFoodPost(foodPost, new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(Task<DocumentReference> task) {
                    progressBar.setVisibility(View.GONE);
                    createPostButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                        clearForm();
                        // Navigate back to home
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else {
                        String errorMessage = "Failed to create post";
                        if (task.getException() != null) {
                            errorMessage += ": " + task.getException().getMessage();
                        }
                        
                        // Show error in dialog for long messages
                        if (errorMessage.length() > 100) {
                            showErrorDialog("Error Creating Post", errorMessage);
                        } else {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            createPostButton.setEnabled(true);
            String errorMessage = "Error creating post: " + e.getMessage();
            showErrorDialog("Error", errorMessage);
        }
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
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
        
        if (quantityInput.getText().toString().trim().isEmpty()) {
            quantityInput.setError("Quantity is required");
            return false;
        }
        
        if (locationInput.getText().toString().trim().isEmpty()) {
            locationInput.setError("Location is required");
            return false;
        }
        
        String postType = postTypeSpinner.getText().toString();
        if (postType.equals("SELL") && priceInput.getText().toString().trim().isEmpty()) {
            priceInput.setError("Price is required for selling");
            return false;
        }
        
        return true;
    }

    private void clearForm() {
        titleInput.setText("");
        descriptionInput.setText("");
        priceInput.setText("");
        quantityInput.setText("");
        locationInput.setText("");
        expiryDateEditText.setText("");
        selectedImages.clear();
        updateImageDisplay();
        postTypeSpinner.setText(postTypes[0], false);
        foodTypeSpinner.setText(foodTypes[0], false);
        quantityUnitSpinner.setText(quantityUnits[0], false);
    }
} 