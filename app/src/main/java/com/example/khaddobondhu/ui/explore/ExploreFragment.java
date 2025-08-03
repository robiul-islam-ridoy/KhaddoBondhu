package com.example.khaddobondhu.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.khaddobondhu.model.User;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.utils.UserRoleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.example.khaddobondhu.ui.profile.UserProfileViewActivity;

public class ExploreFragment extends Fragment {
    // Search related views
    private EditText searchEditText;
    private ImageView searchButton;
    private ImageView filterButton;
    private TextView searchResultsCount;
    private RecyclerView searchResultsRecyclerView;
    private View defaultContentScrollView;
    
    // Default content views
    private RecyclerView restaurantsRecyclerView;
    private RecyclerView ngosRecyclerView;
    private RecyclerView individualsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    
    // Adapters
    private RestaurantAdapter restaurantAdapter;
    private NGOAdapter ngoAdapter;
    private IndividualAdapter individualAdapter;
    private SearchResultsAdapter searchResultsAdapter;
    
    // Data
    private FirebaseService firebaseService;
    private List<User> allUsers;
    private List<User> searchResults;
    private List<User> restaurants;
    private List<User> ngos;
    private List<User> individuals;
    
    // Search and filter state
    private boolean isSearching = false;
    private String currentSearchQuery = "";
    private Set<String> selectedUserTypes = new HashSet<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize Firebase service
        firebaseService = new FirebaseService();
        allUsers = new ArrayList<>();
        searchResults = new ArrayList<>();
        restaurants = new ArrayList<>();
        ngos = new ArrayList<>();
        individuals = new ArrayList<>();

        // Initialize views
        initializeViews(view);
        setupRecyclerViews();
        setupSearchFunctionality();
        loadUsers();
        
        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);

        return view;
    }

    private void initializeViews(View view) {
        // Search related views
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        filterButton = view.findViewById(R.id.filterButton);
        searchResultsCount = view.findViewById(R.id.searchResultsCount);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        defaultContentScrollView = view.findViewById(R.id.defaultContentScrollView);
        
        // Default content views
        restaurantsRecyclerView = view.findViewById(R.id.restaurantsRecyclerView);
        ngosRecyclerView = view.findViewById(R.id.ngosRecyclerView);
        individualsRecyclerView = view.findViewById(R.id.individualsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
    }

    private void setupRecyclerViews() {
        // Setup default content RecyclerViews
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        restaurantAdapter = new RestaurantAdapter(requireContext(), restaurants);
        restaurantsRecyclerView.setAdapter(restaurantAdapter);

        ngosRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        ngoAdapter = new NGOAdapter(requireContext(), ngos);
        ngosRecyclerView.setAdapter(ngoAdapter);

        individualsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        individualAdapter = new IndividualAdapter(requireContext(), individuals);
        individualsRecyclerView.setAdapter(individualAdapter);
        
        // Setup search results RecyclerView with enhanced scrolling
        LinearLayoutManager searchLayoutManager = new LinearLayoutManager(requireContext());
        searchResultsRecyclerView.setLayoutManager(searchLayoutManager);
        searchResultsRecyclerView.setHasFixedSize(false);
        searchResultsRecyclerView.setItemViewCacheSize(20);
        searchResultsRecyclerView.setNestedScrollingEnabled(false);
        searchResultsRecyclerView.setFocusable(true);
        searchResultsRecyclerView.setFocusableInTouchMode(true);
        searchResultsAdapter = new SearchResultsAdapter(requireContext(), searchResults);
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);
        
        // Set click listeners for all adapters
        restaurantAdapter.setOnUserClickListener(user -> {
            Intent intent = UserProfileViewActivity.newIntent(requireContext(), user.getId(), user.getName());
            startActivity(intent);
        });
        
        ngoAdapter.setOnUserClickListener(user -> {
            Intent intent = UserProfileViewActivity.newIntent(requireContext(), user.getId(), user.getName());
            startActivity(intent);
        });
        
        individualAdapter.setOnUserClickListener(user -> {
            Intent intent = UserProfileViewActivity.newIntent(requireContext(), user.getId(), user.getName());
            startActivity(intent);
        });
        
        searchResultsAdapter.setOnUserClickListener(user -> {
            Intent intent = UserProfileViewActivity.newIntent(requireContext(), user.getId(), user.getName());
            startActivity(intent);
        });
    }

    private void setupSearchFunctionality() {
        // Search button click
        searchButton.setOnClickListener(v -> performSearch());
        
        // Filter button click
        filterButton.setOnClickListener(v -> showFilterDialog());
        
        // Search text change listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    clearSearch();
                } else if (query.length() >= 2) {
                    // Auto-search after 2 characters
                    performSearch();
                }
            }
        });
        
        // Enter key listener
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            performSearch();
            return true;
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (query.isEmpty() && selectedUserTypes.isEmpty()) {
            clearSearch();
            return;
        }
        
        currentSearchQuery = query;
        isSearching = true;
        
        // Filter users based on search query and selected filters
        searchResults.clear();
        for (User user : allUsers) {
            if (matchesSearch(user, query) && matchesFilter(user)) {
                searchResults.add(user);
            }
        }
        
        // Update UI
        updateSearchUI();
    }

    private boolean matchesSearch(User user, String query) {
        if (query.isEmpty()) {
            return true;
        }
        
        String searchQuery = query.toLowerCase();
        
        // Search in name
        if (user.getName() != null && user.getName().toLowerCase().contains(searchQuery)) {
            return true;
        }
        
        // Search in description
        if (user.getDescription() != null && user.getDescription().toLowerCase().contains(searchQuery)) {
            return true;
        }
        
        // Search in user type
        if (user.getUserType() != null && user.getUserType().toLowerCase().contains(searchQuery)) {
            return true;
        }
        
        // Search in email
        if (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchQuery)) {
            return true;
        }
        
        return false;
    }

    private boolean matchesFilter(User user) {
        if (selectedUserTypes.isEmpty()) {
            return true; // No filter applied
        }
        
        String userType = user.getUserType();
        if (userType == null) {
            userType = "INDIVIDUAL"; // Default fallback
        }
        
        return selectedUserTypes.contains(userType);
    }

    private void clearSearch() {
        isSearching = false;
        currentSearchQuery = "";
        selectedUserTypes.clear();
        searchResults.clear();
        updateSearchUI();
    }

    private void updateSearchUI() {
        if (isSearching) {
            // Show search results
            searchResultsCount.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            
            // Update results count
            int count = searchResults.size();
            String countText = count + " results found";
            if (!currentSearchQuery.isEmpty()) {
                countText += " for \"" + currentSearchQuery + "\"";
            }
            if (!selectedUserTypes.isEmpty()) {
                countText += " (filtered)";
            }
            searchResultsCount.setText(countText);
            
            // Update adapter
            searchResultsAdapter.updateSearchResults(searchResults);
            
            // Ensure RecyclerView scrolls to top and shows all items
            searchResultsRecyclerView.post(() -> {
                searchResultsRecyclerView.scrollToPosition(0);
                searchResultsRecyclerView.invalidate();
            });
            
        } else {
            // Show default content
            searchResultsCount.setVisibility(View.GONE);
            searchResultsRecyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showFilterDialog() {
        String[] userTypes = UserRoleUtils.getAllUserTypes();
        String[] displayNames = UserRoleUtils.getAllUserTypeDisplayNames();
        
        // Convert selected types to boolean array for dialog
        boolean[] checkedItems = new boolean[userTypes.length];
        for (int i = 0; i < userTypes.length; i++) {
            checkedItems[i] = selectedUserTypes.contains(userTypes[i]);
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Filter by User Type")
                .setMultiChoiceItems(displayNames, checkedItems, (dialog, which, isChecked) -> {
                    // This will be handled in the positive button click
                })
                .setPositiveButton("Apply", (dialog, which) -> {
                    // Get selected items
                    selectedUserTypes.clear();
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    android.util.SparseBooleanArray checked = alertDialog.getListView().getCheckedItemPositions();
                    for (int i = 0; i < checked.size(); i++) {
                        if (checked.valueAt(i)) {
                            selectedUserTypes.add(userTypes[checked.keyAt(i)]);
                        }
                    }
                    
                    // Update search if currently searching
                    if (isSearching || !searchEditText.getText().toString().trim().isEmpty()) {
                        performSearch();
                    }
                })
                .setNegativeButton("Clear All", (dialog, which) -> {
                    selectedUserTypes.clear();
                    if (isSearching || !searchEditText.getText().toString().trim().isEmpty()) {
                        performSearch();
                    }
                })
                .setNeutralButton("Cancel", null);
        
        builder.show();
    }

    private void loadUsers() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        
        // Get current user ID to filter out from results
        String currentUserId = firebaseService.getCurrentUserId();
        
        firebaseService.getAllUsers(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                
                if (task.isSuccessful() && task.getResult() != null) {
                    allUsers.clear();
                    restaurants.clear();
                    ngos.clear();
                    individuals.clear();
                    
                    for (var document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            
                            // Skip the currently logged-in user
                            if (currentUserId != null && currentUserId.equals(user.getId())) {
                                continue;
                            }
                            
                            allUsers.add(user);
                            
                            String userType = user.getUserType();
                            if (userType == null) {
                                userType = "INDIVIDUAL"; // Default fallback
                            }
                            
                            switch (userType) {
                                case "RESTAURANT":
                                    restaurants.add(user);
                                    break;
                                case "NGO":
                                    ngos.add(user);
                                    break;
                                case "INDIVIDUAL":
                                default:
                                    individuals.add(user);
                                    break;
                            }
                        }
                    }
                    
                    updateUI();
                    
                    // If currently searching, update search results
                    if (isSearching) {
                        performSearch();
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load users: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateUI() {
        restaurantAdapter.updateUsers(restaurants);
        ngoAdapter.updateUsers(ngos);
        individualAdapter.updateUsers(individuals);
        
        // Show/hide sections based on data availability
        View view = getView();
        if (view != null) {
            View restaurantsSection = view.findViewById(R.id.restaurantsSection);
            View ngosSection = view.findViewById(R.id.ngosSection);
            View individualsSection = view.findViewById(R.id.individualsSection);
            
            if (restaurantsSection != null) {
                restaurantsSection.setVisibility(restaurants.isEmpty() ? View.GONE : View.VISIBLE);
            }
            if (ngosSection != null) {
                ngosSection.setVisibility(ngos.isEmpty() ? View.GONE : View.VISIBLE);
            }
            if (individualsSection != null) {
                individualsSection.setVisibility(individuals.isEmpty() ? View.GONE : View.VISIBLE);
            }
        }
        
        // Show empty state if no users at all
        if (restaurants.isEmpty() && ngos.isEmpty() && individuals.isEmpty()) {
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }
    
    // Public method to refresh data when navigating to this fragment
    public void refreshData() {
        if (isAdded() && !isDetached()) {
            loadUsers();
        }
    }
} 