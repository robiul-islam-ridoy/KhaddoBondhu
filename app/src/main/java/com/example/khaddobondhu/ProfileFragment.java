package com.example.khaddobondhu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView userName, userEmail, userDescription;
    private Button editButton;

    private String name = "Ridoy";
    private String email = "ridoy@example.com";
    private String description = "Android developer and foodie!";

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.imageViewProfilePic);
        userName = view.findViewById(R.id.textViewUserName);
        userEmail = view.findViewById(R.id.textViewUserEmail);
        userDescription = view.findViewById(R.id.textViewUserDescription);
        editButton = view.findViewById(R.id.buttonEditProfile);

        profileImage.setImageResource(R.drawable.ic_person); // sample image

        updateProfileUI();

        editButton.setOnClickListener(v -> showEditDialog());

        return view;
    }

    private void updateProfileUI() {
        userName.setText(name);
        userEmail.setText(email);
        userDescription.setText(description);
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Profile");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText inputName = dialogView.findViewById(R.id.editName);
        EditText inputEmail = dialogView.findViewById(R.id.editEmail);
        EditText inputDesc = dialogView.findViewById(R.id.editDescription);

        inputName.setText(name);
        inputEmail.setText(email);
        inputDesc.setText(description);

        builder.setPositiveButton("Save", (dialog, which) -> {
            name = inputName.getText().toString();
            email = inputEmail.getText().toString();
            description = inputDesc.getText().toString();
            updateProfileUI();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
