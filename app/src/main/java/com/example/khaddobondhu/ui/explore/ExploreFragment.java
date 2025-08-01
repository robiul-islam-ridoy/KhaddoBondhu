package com.example.khaddobondhu.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import java.util.List;

public class ExploreFragment extends Fragment {
    private RecyclerView restaurantsRecyclerView;
    private RecyclerView ngosRecyclerView;
    private RecyclerView individualsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView emptyStateTextView;
    private TextView statsTextView;
    
    private RestaurantAdapter restaurantAdapter;
    private NGOAdapter ngoAdapter;
    private IndividualAdapter individualAdapter;
    
    private FirebaseService firebaseService;
    private List<User> restaurants;
    private List<User> ngos;
    private List<User> individuals;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize Firebase service
        firebaseService = new FirebaseService();
        restaurants = new ArrayList<>();
        ngos = new ArrayList<>();
        individuals = new ArrayList<>();

        // Initialize views
        initializeViews(view);
        setupRecyclerViews();
        loadUsers();
        
        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);

        return view;
    }

    private void initializeViews(View view) {
        restaurantsRecyclerView = view.findViewById(R.id.restaurantsRecyclerView);
        ngosRecyclerView = view.findViewById(R.id.ngosRecyclerView);
        individualsRecyclerView = view.findViewById(R.id.individualsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        statsTextView = view.findViewById(R.id.statsTextView);
    }

    private void setupRecyclerViews() {
        // Setup Restaurants RecyclerView
        restaurantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        restaurantAdapter = new RestaurantAdapter(requireContext(), restaurants);
        restaurantsRecyclerView.setAdapter(restaurantAdapter);

        // Setup NGOs RecyclerView
        ngosRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        ngoAdapter = new NGOAdapter(requireContext(), ngos);
        ngosRecyclerView.setAdapter(ngoAdapter);

        // Setup Individuals RecyclerView
        individualsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        individualAdapter = new IndividualAdapter(requireContext(), individuals);
        individualsRecyclerView.setAdapter(individualAdapter);
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
                    restaurants.clear();
                    ngos.clear();
                    individuals.clear();
                    
                    for (var document : task.getResult()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            
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
                    updateStats();
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

    private void updateStats() {
        int totalUsers = restaurants.size() + ngos.size() + individuals.size();
        int totalPosts = restaurants.stream().mapToInt(User::getTotalPosts).sum() +
                        ngos.stream().mapToInt(User::getTotalPosts).sum() +
                        individuals.stream().mapToInt(User::getTotalPosts).sum();
        int totalDonations = restaurants.stream().mapToInt(User::getTotalDonations).sum() +
                           ngos.stream().mapToInt(User::getTotalDonations).sum() +
                           individuals.stream().mapToInt(User::getTotalDonations).sum();
        
        String statsText = String.format("📊 Community Stats\n%d Users • %d Posts • %d Donations", 
                                       totalUsers, totalPosts, totalDonations);
        if (statsTextView != null) {
            statsTextView.setText(statsText);
        }
    }
} 