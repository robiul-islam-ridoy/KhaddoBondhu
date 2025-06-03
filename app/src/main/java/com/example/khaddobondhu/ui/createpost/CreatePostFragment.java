package com.example.khaddobondhu.ui.createpost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.khaddobondhu.databinding.FragmentCreatePostBinding;
import com.google.android.material.snackbar.Snackbar;

public class CreatePostFragment extends Fragment {
    private FragmentCreatePostBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupViews();
        setupListeners();

        return root;
    }

    private void setupViews() {
        // TODO: Initialize views and set up any initial state
    }

    private void setupListeners() {
        binding.buttonAddPhotos.setOnClickListener(v -> {
            // TODO: Implement photo selection
            Snackbar.make(binding.getRoot(), "Photo selection coming soon", Snackbar.LENGTH_SHORT).show();
        });

        binding.buttonSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                // TODO: Implement post creation
                Snackbar.make(binding.getRoot(), "Post creation coming soon", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        if (binding.editTextTitle.getText().toString().trim().isEmpty()) {
            binding.editTextTitle.setError("Title is required");
            isValid = false;
        }

        if (binding.editTextDescription.getText().toString().trim().isEmpty()) {
            binding.editTextDescription.setError("Description is required");
            isValid = false;
        }

        if (binding.editTextQuantity.getText().toString().trim().isEmpty()) {
            binding.editTextQuantity.setError("Quantity is required");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 