package com.example.khaddobondhu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyPostsFragment extends Fragment implements UserPostAdapter.OnPostActionListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private UserPostAdapter adapter;
    private List<FoodPost> userPosts = new ArrayList<>();
    private FirebaseService firebaseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        
        recyclerView = view.findViewById(R.id.userPostsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        
        firebaseService = new FirebaseService();
        
        setupRecyclerView();
        loadUserPosts();
        
        return view;
    }

    private void setupRecyclerView() {
        adapter = new UserPostAdapter(getContext(), userPosts, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadUserPosts() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showEmptyState("Please sign in to view your posts");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseService.getPostsByUserId(userId, new FirebaseService.OnPostsFetchListener() {
            @Override
            public void onSuccess(List<FoodPost> posts) {
                progressBar.setVisibility(View.GONE);
                userPosts.clear();
                userPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                if (posts.isEmpty()) {
                    showEmptyState("You haven't created any posts yet.\nStart sharing food!");
                } else {
                    hideEmptyState();
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Failed to load posts: " + e.getMessage());
            }
        });
    }

    private void showEmptyState(String message) {
        emptyStateTextView.setText(message);
        emptyStateTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyStateTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEditPost(FoodPost post) {
        // Handle post edit
        Toast.makeText(getContext(), "Edit post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeletePost(FoodPost post) {
        // Handle post delete
        Toast.makeText(getContext(), "Delete post: " + post.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public void refreshPosts() {
        loadUserPosts();
    }
}
