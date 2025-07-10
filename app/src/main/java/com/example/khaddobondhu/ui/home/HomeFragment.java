package com.example.khaddobondhu.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.khaddobondhu.R;
import com.example.khaddobondhu.model.FoodPost;
import com.example.khaddobondhu.service.FirebaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FoodPostAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private FirebaseService firebaseService;
    private List<FoodPost> foodPosts;
    private FloatingActionButton fabFilter;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseService = new FirebaseService();
        foodPosts = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        fabFilter = view.findViewById(R.id.fabFilter);

        setupRecyclerView();
        loadFoodPosts();

        swipeRefreshLayout.setOnRefreshListener(this::loadFoodPosts);

        // ✅ Make FAB open the filter dialog
        fabFilter.setOnClickListener(v -> showFilterDialog());

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FoodPostAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = 200; // space at bottom
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
                        emptyStateTextView.setVisibility(View.GONE);
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
        Spinner priceSpinner = dialogView.findViewById(R.id.priceSpinner);
        EditText locationInput = dialogView.findViewById(R.id.locationInput);

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

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Any Price", "Free Only", "Under ৳50", "Under ৳100", "Under ৳200"});
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Filter Posts")
                .setView(dialogView)
                .setPositiveButton("Apply Filter", (dialog, which) -> {
                    String postType = postTypeSpinner.getSelectedItem().toString();
                    String foodType = foodTypeSpinner.getSelectedItem().toString();
                    String price = priceSpinner.getSelectedItem().toString();
                    String location = locationInput.getText().toString().trim();

                    applyFilter(postType, foodType, price, location);
                })
                .setNegativeButton("Clear Filter", (dialog, which) -> clearFilter())
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
                    List<FoodPost> results = firebaseService.getSearchResults();
                    adapter.updatePosts(results);

                    if (results.isEmpty()) {
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

    private void applyFilter(String postType, String foodType, String price, String location) {
        progressBar.setVisibility(View.VISIBLE);

        firebaseService.filterFoodPosts(postType, foodType, price, location, new FirebaseService.Callback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    List<FoodPost> results = firebaseService.getFilteredResults();
                    adapter.updatePosts(results);

                    if (results.isEmpty()) {
                        emptyStateTextView.setText("No posts match your filter");
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
        loadFoodPosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFoodPosts(); // Always refresh on resume
    }
}
