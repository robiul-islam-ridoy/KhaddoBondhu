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
        
        // Load food posts
        loadFoodPosts();
        
        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadFoodPosts);
        
        // Setup filter button
        fabFilter.setOnClickListener(v -> showFilterDialog());


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

    private void loadFoodPosts() {
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
                    if (posts.isEmpty()) {
                        emptyStateTextView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.updatePosts(posts);
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
        loadFoodPosts();
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
    
    public void showFilterDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null);
        Spinner postTypeSpinner = dialogView.findViewById(R.id.postTypeSpinner);
        Spinner foodTypeSpinner = dialogView.findViewById(R.id.foodTypeSpinner);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        EditText locationInput = dialogView.findViewById(R.id.locationInput);
        
        // Setup spinners
        ArrayAdapter<String> postTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, 
            new String[]{"All Types", "DONATE", "SELL", "REQUEST_DONATION", "REQUEST_TO_BUY"});
        postTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postTypeSpinner.setAdapter(postTypeAdapter);
        
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, 
            new String[]{"All Foods", "Rice", "Curry", "Snacks", "Fruits", "Vegetables", "Bread", "Dessert", "Other"});
        foodTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodTypeSpinner.setAdapter(foodTypeAdapter);




        new AlertDialog.Builder(requireContext())
            .setTitle("Filter Posts")
            .setView(dialogView)
            .setPositiveButton("Apply Filter", (dialog, which) -> {
                String postType = postTypeSpinner.getSelectedItem().toString();
                String foodType = foodTypeSpinner.getSelectedItem().toString();
                String priceFilter = priceInput.getText().toString().trim();
                int maxPrice = priceFilter.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(priceFilter);
                String location = locationInput.getText().toString().trim();
                
                applyFilter(postType, foodType, maxPrice, location);
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
    
    private void applyFilter(String postType, String foodType, int maxPrice, String location) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.filterFoodPosts(postType, foodType, maxPrice, location, new FirebaseService.Callback() {
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
        loadFoodPosts(); // Reload all posts
    }
} 