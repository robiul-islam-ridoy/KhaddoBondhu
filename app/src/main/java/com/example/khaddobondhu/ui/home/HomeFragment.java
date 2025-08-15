package com.example.khaddobondhu.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodPostAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private FirebaseService firebaseService;
    private List<FoodPost> foodPosts;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabFilter;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase service
        firebaseService = new FirebaseService();
        foodPosts = new ArrayList<>();

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        fabFilter = view.findViewById(R.id.fabFilter);

        // Setup RecyclerView
        setupRecyclerView();
        
        // Check for filter arguments or search query
        String filterType = getArguments() != null ? getArguments().getString("filter_type", "all") : "all";
        String searchQuery = getArguments() != null ? getArguments().getString("search_query", null) : null;
        
        // Load food posts with filter or search
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loadFoodPostsWithSearch(searchQuery);
        } else {
            loadFoodPosts(filterType);
        }
        
        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (searchQuery != null && !searchQuery.isEmpty()) {
                loadFoodPostsWithSearch(searchQuery);
            } else {
                loadFoodPosts(filterType);
            }
        });
        
        // Setup filter button
        fabFilter.setOnClickListener(v -> showFilterDialog(filterType));

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FoodPostAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        
        // Add item decoration for bottom spacing
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 200; // Add bottom spacing for last item
                }
            }
        });
    }

    private void loadFoodPosts(String filterType) {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        
        firebaseService.getAllFoodPosts(new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    List<FoodPost> posts = firebaseService.getFoodPosts();
                    
                    // Apply filter based on type
                    List<FoodPost> filteredPosts = filterPostsByType(posts, filterType);
                    
                    if (filteredPosts.isEmpty()) {
                        emptyStateTextView.setVisibility(View.VISIBLE);
                        emptyStateTextView.setText("No " + filterType + " posts available");
                    } else {
                        adapter.updatePosts(filteredPosts);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setText("Error loading posts: " + error);
                });
            }
        });
    }

    private void loadFoodPostsWithSearch(String searchQuery) {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);
        
        firebaseService.getAllFoodPosts(new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    List<FoodPost> posts = firebaseService.getFoodPosts();
                    
                    // Apply search filter
                    List<FoodPost> searchResults = searchPosts(posts, searchQuery);
                    
                    if (searchResults.isEmpty()) {
                        emptyStateTextView.setVisibility(View.VISIBLE);
                        emptyStateTextView.setText("No posts found for: " + searchQuery);
                    } else {
                        adapter.updatePosts(searchResults);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    emptyStateTextView.setText("Error loading posts: " + error);
                });
            }
        });
    }

    private List<FoodPost> searchPosts(List<FoodPost> posts, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return posts;
        }
        
        String query = searchQuery.toLowerCase().trim();
        List<FoodPost> searchResults = new ArrayList<>();
        
        for (FoodPost post : posts) {
            // Search in title
            if (post.getTitle() != null && post.getTitle().toLowerCase().contains(query)) {
                searchResults.add(post);
                continue;
            }
            
            // Search in description
            if (post.getDescription() != null && post.getDescription().toLowerCase().contains(query)) {
                searchResults.add(post);
                continue;
            }
            
            // Search in location
            if (post.getPickupLocation() != null && post.getPickupLocation().toLowerCase().contains(query)) {
                searchResults.add(post);
                continue;
            }
        }
        
        return searchResults;
    }

    private List<FoodPost> filterPostsByType(List<FoodPost> posts, String filterType) {
        if ("all".equals(filterType)) {
            return posts;
        }
        
        List<FoodPost> filteredPosts = new ArrayList<>();
        for (FoodPost post : posts) {
            switch (filterType) {
                case "donation":
                    if ("DONATE".equals(post.getPostType())) {
                        filteredPosts.add(post);
                    }
                    break;
                case "sell":
                    if ("SELL".equals(post.getPostType())) {
                        filteredPosts.add(post);
                    }
                    break;
                case "requests":
                    if ("REQUEST_DONATION".equals(post.getPostType()) || "REQUEST_TO_BUY".equals(post.getPostType())) {
                        filteredPosts.add(post);
                    }
                    break;
            }
        }
        return filteredPosts;
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        String filterType = getArguments() != null ? getArguments().getString("filter_type", "all") : "all";
        String searchQuery = getArguments() != null ? getArguments().getString("search_query", null) : null;
        
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loadFoodPostsWithSearch(searchQuery);
        } else {
            loadFoodPosts(filterType);
        }
    }

    public void showSearchDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search, null);
        EditText searchInput = dialogView.findViewById(R.id.searchInput);
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Search Posts")
            .setView(dialogView)
            .setPositiveButton("Search", (dialog, which) -> {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchPosts(query);
                } else {
                    Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    public void showFilterDialog(String currentFilterType) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        Spinner postTypeSpinner = dialogView.findViewById(R.id.postTypeSpinner);
        Spinner foodTypeSpinner = dialogView.findViewById(R.id.foodTypeSpinner);
        EditText minPriceInput = dialogView.findViewById(R.id.minPriceInput);
        EditText maxPriceInput = dialogView.findViewById(R.id.maxPriceInput);
        EditText locationInput = dialogView.findViewById(R.id.locationInput);
        
        // Find the labels to hide them when inputs are hidden
        View postTypeLabel = dialogView.findViewById(R.id.postTypeLabel);
        View priceRangeLabel = dialogView.findViewById(R.id.priceRangeLabel);
        
        // Setup food type spinner
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, 
            new String[]{"All Foods", "Rice", "Curry", "Snacks", "Fruits", "Vegetables", "Bread", "Dessert", "Other"});
        foodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodTypeSpinner.setAdapter(foodTypeAdapter);
        
        // Context-aware filter setup based on current filter type
        if ("all".equals(currentFilterType)) {
            // For "All Posts" - show all filter options including post type
            ArrayAdapter<String> postTypeAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, 
                new String[]{"All Types", "DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"});
            postTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            postTypeSpinner.setAdapter(postTypeAdapter);
            postTypeSpinner.setVisibility(View.VISIBLE);
            postTypeLabel.setVisibility(View.VISIBLE);
            minPriceInput.setVisibility(View.VISIBLE);
            maxPriceInput.setVisibility(View.VISIBLE);
            priceRangeLabel.setVisibility(View.VISIBLE);
            
        } else if ("sell".equals(currentFilterType)) {
            // For "Sell Posts" - hide post type filter, show price filter
            postTypeSpinner.setVisibility(View.GONE);
            postTypeLabel.setVisibility(View.GONE);
            minPriceInput.setVisibility(View.VISIBLE);
            maxPriceInput.setVisibility(View.VISIBLE);
            priceRangeLabel.setVisibility(View.VISIBLE);
            
        } else if ("donation".equals(currentFilterType) || "requests".equals(currentFilterType)) {
            // For "Donation Posts" and "Request Posts" - hide post type and price filters
            postTypeSpinner.setVisibility(View.GONE);
            postTypeLabel.setVisibility(View.GONE);
            minPriceInput.setVisibility(View.GONE);
            maxPriceInput.setVisibility(View.GONE);
            priceRangeLabel.setVisibility(View.GONE);
        }

        new AlertDialog.Builder(requireContext())
            .setTitle("Filter Posts")
            .setView(dialogView)
            .setPositiveButton("Apply Filter", (dialog, which) -> {
                String postType = "All Types"; // Default
                String foodType = foodTypeSpinner.getSelectedItem().toString();
                String minPriceFilter = minPriceInput.getText().toString().trim();
                String maxPriceFilter = maxPriceInput.getText().toString().trim();
                String location = locationInput.getText().toString().trim();
                
                // Get post type only if it's visible (All Posts filter)
                if (postTypeSpinner.getVisibility() == View.VISIBLE) {
                    postType = postTypeSpinner.getSelectedItem().toString();
                } else {
                    // Set post type based on current filter
                    switch (currentFilterType) {
                        case "sell":
                            postType = "SELL";
                            break;
                        case "donation":
                            postType = "DONATE";
                            break;
                        case "requests":
                            postType = "REQUEST_DONATION,REQUEST_TO_BUY";
                            break;
                        default:
                            postType = "All Types";
                    }
                }
                
                // Parse price range only if price inputs are visible
                int minPrice = 0;
                int maxPrice = Integer.MAX_VALUE;
                
                if (minPriceInput.getVisibility() == View.VISIBLE && maxPriceInput.getVisibility() == View.VISIBLE) {
                    minPrice = minPriceFilter.isEmpty() ? 0 : Integer.parseInt(minPriceFilter);
                    maxPrice = maxPriceFilter.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxPriceFilter);
                    
                    // Validate price range
                    if (!minPriceFilter.isEmpty() && !maxPriceFilter.isEmpty() && minPrice > maxPrice) {
                        Toast.makeText(requireContext(), "Minimum price cannot be greater than maximum price", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                
                applyFilter(postType, foodType, minPrice, maxPrice, location);
            })
            .setNegativeButton("Clear Filter", (dialog, which) -> {
                clearFilter();
            })
            .setNeutralButton("Cancel", null)
            .show();
    }
    
    private void searchPosts(String query) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.searchFoodPosts(query, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    List<FoodPost> searchResults = firebaseService.getSearchResults();
                    adapter.updatePosts(searchResults);
                    
                    if (searchResults.isEmpty()) {
                        emptyStateTextView.setText("No posts found for: " + query);
                        emptyStateTextView.setVisibility(View.VISIBLE);
                    } else {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Search failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void applyFilter(String postType, String foodType, int minPrice, int maxPrice, String location) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.filterFoodPosts(postType, foodType, minPrice, maxPrice, location, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    List<FoodPost> filteredResults = firebaseService.getFilteredResults();
                    adapter.updatePosts(filteredResults);
                    
                    if (filteredResults.isEmpty()) {
                        emptyStateTextView.setText("No posts match your filter criteria");
                        emptyStateTextView.setVisibility(View.VISIBLE);
                    } else {
                        emptyStateTextView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Filter failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void clearFilter() {
        String filterType = getArguments() != null ? getArguments().getString("filter_type", "all") : "all";
        String searchQuery = getArguments() != null ? getArguments().getString("search_query", null) : null;
        
        if (searchQuery != null && !searchQuery.isEmpty()) {
            loadFoodPostsWithSearch(searchQuery);
        } else {
            loadFoodPosts(filterType);
        }
    }
    
    // Public method to refresh data when navigating to this fragment
    public void refreshData() {
        if (isAdded() && !isDetached()) {
            String filterType = getArguments() != null ? getArguments().getString("filter_type", "all") : "all";
            String searchQuery = getArguments() != null ? getArguments().getString("search_query", null) : null;
            
            if (searchQuery != null && !searchQuery.isEmpty()) {
                loadFoodPostsWithSearch(searchQuery);
            } else {
                loadFoodPosts(filterType);
            }
        }
    }
} 