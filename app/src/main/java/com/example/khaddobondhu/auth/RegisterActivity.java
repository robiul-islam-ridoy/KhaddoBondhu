package com.example.khaddobondhu.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.khaddobondhu.MainActivity;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.databinding.ActivityRegisterBinding;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.utils.UserRoleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firebaseService = new FirebaseService();

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.buttonRegister.setOnClickListener(v -> registerUser());
        binding.buttonBackToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();
        
        // Get selected user type
        String userType = getUserSelectedType();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            binding.editTextName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            binding.editTextPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);

        // Check if Firebase is initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonRegister.setEnabled(true);
            Toast.makeText(RegisterActivity.this, "Firebase not initialized. Please check your configuration.", 
                         Toast.LENGTH_LONG).show();
            return;
        }

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Registration successful, create user profile
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if (firebaseUser != null) {
                        createUserProfile(firebaseUser, name, email, phone, userType);
                    }
                } else {
                    // Registration failed
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonRegister.setEnabled(true);
                    String errorMessage = "Registration failed";
                    if (task.getException() != null) {
                        String exceptionMessage = task.getException().getMessage();
                        if (exceptionMessage != null) {
                            if (exceptionMessage.contains("CONFIGURATION_NOT_FOUND")) {
                                errorMessage = "Firebase configuration error. Please check your setup.";
                            } else if (exceptionMessage.contains("NETWORK")) {
                                errorMessage = "Network error. Please check your internet connection.";
                            } else if (exceptionMessage.contains("INVALID_EMAIL")) {
                                errorMessage = "Invalid email address.";
                            } else if (exceptionMessage.contains("WEAK_PASSWORD")) {
                                errorMessage = "Password is too weak.";
                            } else if (exceptionMessage.contains("EMAIL_ALREADY_IN_USE")) {
                                errorMessage = "An account with this email already exists.";
                            } else {
                                errorMessage = "Registration failed: " + exceptionMessage;
                            }
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void createUserProfile(FirebaseUser firebaseUser, String name, String email, String phone, String userType) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setUserType(userType);
        user.setDescription("Welcome to KhaddoBondhu! Share and discover food in your community.");
        user.setTotalPosts(0);
        user.setTotalDonations(0);
        user.setTotalReceived(0);
        user.setRating(0);

        firebaseService.createUserProfile(user, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonRegister.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                } else {
                    String errorMessage = "Failed to create profile";
                    if (task.getException() != null) {
                        String exceptionMessage = task.getException().getMessage();
                        if (exceptionMessage != null) {
                            if (exceptionMessage.contains("CONFIGURATION_NOT_FOUND")) {
                                errorMessage = "Firebase configuration error. Please check your setup.";
                            } else if (exceptionMessage.contains("NETWORK")) {
                                errorMessage = "Network error. Please check your internet connection.";
                            } else {
                                errorMessage = "Profile creation failed: " + exceptionMessage;
                            }
                        }
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private String getUserSelectedType() {
        if (binding.radioRestaurant.isChecked()) {
            return UserRoleUtils.getAllUserTypes()[1]; // RESTAURANT
        } else if (binding.radioNGO.isChecked()) {
            return UserRoleUtils.getAllUserTypes()[2]; // NGO
        } else {
            return UserRoleUtils.getAllUserTypes()[0]; // INDIVIDUAL (default)
        }
    }
} 