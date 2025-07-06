package com.example.khaddobondhu.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.khaddobondhu.MainActivity;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.databinding.ActivityLoginBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            // User is already signed in, go to main activity
            startMainActivity();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.buttonLogin.setOnClickListener(v -> loginUser());
        binding.buttonRegister.setOnClickListener(v -> startRegisterActivity());
        binding.buttonGuestLogin.setOnClickListener(v -> loginAsGuest());
        binding.textViewForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void loginUser() {
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        // Validate inputs
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

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonLogin.setEnabled(false);

        // Check if Firebase is initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLogin.setEnabled(true);
            Toast.makeText(LoginActivity.this, "Firebase not initialized. Please check your configuration.", 
                         Toast.LENGTH_LONG).show();
            return;
        }

        // Sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonLogin.setEnabled(true);

                if (task.isSuccessful()) {
                    // Sign in success
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                } else {
                    // Sign in failed
                    String errorMessage = "Login failed";
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
                            } else if (exceptionMessage.contains("USER_NOT_FOUND")) {
                                errorMessage = "No account found with this email.";
                            } else if (exceptionMessage.contains("WRONG_PASSWORD")) {
                                errorMessage = "Incorrect password.";
                            } else {
                                errorMessage = "Login failed: " + exceptionMessage;
                            }
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void loginAsGuest() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonGuestLogin.setEnabled(false);

        // Check if Firebase is initialized
        if (FirebaseApp.getApps(this).isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonGuestLogin.setEnabled(true);
            Toast.makeText(LoginActivity.this, "Firebase not initialized. Please check your configuration.", 
                         Toast.LENGTH_LONG).show();
            return;
        }

        auth.signInAnonymously()
            .addOnCompleteListener(this, task -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonGuestLogin.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Guest login successful!", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                } else {
                    String errorMessage = "Guest login failed";
                    if (task.getException() != null) {
                        String exceptionMessage = task.getException().getMessage();
                        if (exceptionMessage != null) {
                            if (exceptionMessage.contains("CONFIGURATION_NOT_FOUND")) {
                                errorMessage = "Firebase configuration error. Please check your setup.";
                            } else if (exceptionMessage.contains("NETWORK")) {
                                errorMessage = "Network error. Please check your internet connection.";
                            } else {
                                errorMessage = "Guest login failed: " + exceptionMessage;
                            }
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showForgotPasswordDialog() {
        String email = binding.editTextEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Please enter your email first");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                binding.progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, 
                                 "Password reset email sent to " + email, 
                                 Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, 
                                 "Failed to send reset email: " + task.getException().getMessage(), 
                                 Toast.LENGTH_LONG).show();
                }
            });
    }
} 