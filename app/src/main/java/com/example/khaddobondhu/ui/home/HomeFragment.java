package com.example.khaddobondhu.ui.home;

<<<<<<< HEAD
=======
import android.content.Intent;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
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
=======
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
        fabFilter.setOnClickListener(v -> {
            // TODO: Implement filter functionality
            Toast.makeText(requireContext(), "Filter feature coming soon!", Toast.LENGTH_SHORT).show();
        });

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
        Spinner priceSpinner = dialogView.findViewById(R.id.priceSpinner);
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
                String priceFilter = priceSpinner.getSelectedItem().toString();
                String location = locationInput.getText().toString().trim();
                
                applyFilter(postType, foodType, priceFilter, location);
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
    
    private void applyFilter(String postType, String foodType, String priceFilter, String location) {
        progressBar.setVisibility(View.VISIBLE);
        
        firebaseService.filterFoodPosts(postType, foodType, priceFilter, location, new FirebaseService.Callback() {
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
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }
} 