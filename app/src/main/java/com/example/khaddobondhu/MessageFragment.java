package com.example.khaddobondhu;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
<<<<<<< HEAD

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
=======
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)

public class MessageFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
<<<<<<< HEAD
    private ArrayList<User> userList;
=======
    private List<User> userList;
    private ProgressBar progressBar;
    private TextView emptyStateText;
    private FirebaseService firebaseService;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

<<<<<<< HEAD
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        userList = new ArrayList<>();
        userList = new ArrayList<>();
        userList.add(new User("1", "Ridoy", R.drawable.ic_person));
        userList.add(new User("2", "Ayesha", R.drawable.ic_person));
        userList.add(new User("3", "Rahim", R.drawable.ic_person));


        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);
=======
        // Initialize Firebase service
        firebaseService = new FirebaseService();
        userList = new ArrayList<>();

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateText = view.findViewById(R.id.textViewEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load users
        loadUsers();
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)

        return view;
    }

<<<<<<< HEAD
=======
    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);

        FirebaseUser currentUser = firebaseService.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, show sample data
            loadSampleUsers();
            return;
        }

        firebaseService.getAllUsers(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                
                if (task.isSuccessful() && task.getResult() != null) {
                    userList.clear();
                    
                    for (var document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (user != null && !user.getId().equals(currentUser.getUid())) {
                            userList.add(user);
                        }
                    }
                    
                    if (userList.isEmpty()) {
                        // No users found, show sample data
                        loadSampleUsers();
                    } else {
                        setupAdapter();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                    loadSampleUsers();
                }
            }
        });
    }

    private void loadSampleUsers() {
        userList.clear();
        userList.add(new User("1", "Ridoy", "ridoy@example.com"));
        userList.add(new User("2", "Ayesha", "ayesha@example.com"));
        userList.add(new User("3", "Rahim", "rahim@example.com"));
        userList.add(new User("4", "Fatima", "fatima@example.com"));
        userList.add(new User("5", "Karim", "karim@example.com"));
        
        setupAdapter();
    }

    private void setupAdapter() {
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);
        
        if (userList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("userId", user.getId());
        intent.putExtra("userName", user.getName());
        startActivity(intent);
    }
<<<<<<< HEAD
=======

    @Override
    public void onResume() {
        super.onResume();
        // Refresh users when returning to this fragment
        loadUsers();
    }
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
}
