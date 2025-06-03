package com.example.khaddobondhu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.khaddobondhu.databinding.FragmentHomeBinding;
import com.example.khaddobondhu.model.FoodPost;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements FoodPostAdapter.OnFoodPostClickListener {
    private FragmentHomeBinding binding;
    private FoodPostAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        loadSampleData();
        setupSwipeRefresh();
        setupFilterButton();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new FoodPostAdapter(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadSampleData() {
        List<FoodPost> samplePosts = new ArrayList<>();
        
        samplePosts.add(new FoodPost(
            "1",
            "Delicious Biryani",
            "Fresh homemade biryani with special spices and tender meat. Made with premium quality ingredients.",
            "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8",
            "DONATE",
            0.0,
            4,
            "2.3 km away",
            "Expires in 2 hours",
            "user1",
            "Rahul's Kitchen"
        ));

        samplePosts.add(new FoodPost(
            "2",
            "Vegetable Pulao",
            "Healthy vegetable pulao with fresh seasonal vegetables. Perfect for a nutritious meal.",
            "https://images.unsplash.com/photo-1603133872878-684f208fb84b",
            "SELL",
            150.0,
            2,
            "1.5 km away",
            "Expires in 4 hours",
            "user2",
            "Green Bites"
        ));

        samplePosts.add(new FoodPost(
            "3",
            "Chicken Curry",
            "Spicy chicken curry with rich gravy. Served with rice or roti.",
            "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398",
            "REQUEST_DONATION",
            0.0,
            1,
            "3.7 km away",
            "Expires in 1 hour",
            "user3",
            "Community Kitchen"
        ));

        samplePosts.add(new FoodPost(
            "4",
            "Fresh Fruits",
            "Assorted fresh fruits including apples, oranges, and bananas. Perfect for a healthy snack.",
            "https://images.unsplash.com/photo-1610832958506-aa56368176cf",
            "DONATE",
            0.0,
            5,
            "0.8 km away",
            "Expires in 6 hours",
            "user4",
            "Fruit Paradise"
        ));

        adapter.setFoodPosts(samplePosts);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Simulate refresh
            binding.swipeRefreshLayout.postDelayed(() -> {
                loadSampleData();
                binding.swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(binding.getRoot(), "Posts refreshed", Snackbar.LENGTH_SHORT).show();
            }, 1000);
        });
    }

    private void setupFilterButton() {
        binding.fabFilter.setOnClickListener(v -> {
            Snackbar.make(binding.getRoot(), "Filter options coming soon", Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onFoodPostClick(FoodPost foodPost) {
        Snackbar.make(binding.getRoot(), "Selected: " + foodPost.getTitle(), Snackbar.LENGTH_SHORT).show();
        // TODO: Navigate to food detail screen
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 