package com.example.khaddobondhu.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.Request;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment implements RequestAdapter.OnRequestActionListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private RequestAdapter adapter;
    private List<Request> requests = new ArrayList<>();
    private FirebaseService firebaseService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        
        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        
        firebaseService = new FirebaseService();
        
        setupRecyclerView();
        loadRequests();
        
        return view;
    }

    private void setupRecyclerView() {
        adapter = new RequestAdapter(getContext(), requests, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadRequests() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            showEmptyState("Please sign in to view your requests");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseService.getRequestsForPostOwner(userId, new FirebaseService.OnRequestsFetchListener() {
            @Override
            public void onSuccess(List<Request> requestList) {
                progressBar.setVisibility(View.GONE);
                requests.clear();
                requests.addAll(requestList);
                adapter.notifyDataSetChanged();

                if (requestList.isEmpty()) {
                    showEmptyState("No requests yet.\nRequests from other users will appear here!");
                } else {
                    hideEmptyState();
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Failed to load requests: " + e.getMessage());
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
    public void onRequestAccepted(Request request) {
        // Refresh the requests list after accepting
        loadRequests();
    }

    @Override
    public void onRequestDeclined(Request request) {
        // Refresh the requests list after declining
        loadRequests();
    }

    public void refreshRequests() {
        loadRequests();
    }
}
