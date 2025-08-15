package com.example.khaddobondhu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.khaddobondhu.R;

public class HomeCategoriesFragment extends Fragment {

    private EditText searchBar;
    private LinearLayout cardAllPosts, cardDonationPosts, cardSellPosts, cardRequests;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_categories, container, false);

        // Initialize views
        searchBar = root.findViewById(R.id.searchBar);
        cardAllPosts = root.findViewById(R.id.cardAllPosts);
        cardDonationPosts = root.findViewById(R.id.cardDonationPosts);
        cardSellPosts = root.findViewById(R.id.cardSellPosts);
        cardRequests = root.findViewById(R.id.cardRequests);

        // Set click listeners
        setupClickListeners();

        return root;
    }

    private void setupClickListeners() {
        // All Posts
        cardAllPosts.setOnClickListener(v -> {
            navigateToFilteredPosts("all");
        });

        // Donation Posts
        cardDonationPosts.setOnClickListener(v -> {
            navigateToFilteredPosts("donation");
        });

        // Sell Posts
        cardSellPosts.setOnClickListener(v -> {
            navigateToFilteredPosts("sell");
        });

        // Requests
        cardRequests.setOnClickListener(v -> {
            navigateToFilteredPosts("requests");
        });

        // Search functionality
        setupSearchFunctionality();
    }



    private void setupSearchFunctionality() {
        // Remove automatic search on text change
        // Only search when user clicks search icon or presses enter
        
        // Handle search on enter key
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            String searchQuery = searchBar.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                navigateToSearchResults(searchQuery);
                return true;
            }
            return false;
        });
        
        // Handle search on search icon click
        searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                // Check if the touch was on the drawable (search icon)
                if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[2].getBounds().width())) {
                    String searchQuery = searchBar.getText().toString().trim();
                    if (!searchQuery.isEmpty()) {
                        navigateToSearchResults(searchQuery);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void navigateToSearchResults(String searchQuery) {
        // Create bundle with search query
        Bundle args = new Bundle();
        args.putString("search_query", searchQuery);
        
        // Navigate to posts fragment with search
        Navigation.findNavController(requireView())
                .navigate(R.id.navigation_posts, args);
    }

    private void navigateToFilteredPosts(String filterType) {
        // Create bundle with filter type
        Bundle args = new Bundle();
        args.putString("filter_type", filterType);
        
        // Navigate to posts fragment with filter
        Navigation.findNavController(requireView())
                .navigate(R.id.navigation_posts, args);
    }
}
