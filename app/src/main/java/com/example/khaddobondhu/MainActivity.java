package com.example.khaddobondhu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.khaddobondhu.auth.LoginActivity;
import com.example.khaddobondhu.databinding.ActivityMainBinding;
import com.example.khaddobondhu.service.FirebaseService;
import com.example.khaddobondhu.ui.home.HomeFragment;
import com.example.khaddobondhu.ui.explore.ExploreFragment;
import com.example.khaddobondhu.ui.profile.ProfileFragment;
import com.example.khaddobondhu.MessageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private FirebaseAuth auth;
    private FirebaseService firebaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth and Service
        auth = FirebaseAuth.getInstance();
        firebaseService = new FirebaseService();
        
        // Check if user is signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // No user signed in, go to login
            startLoginActivity();
            return;
        }
        
        // Load current user profile data
        loadCurrentUserProfile();

        setSupportActionBar(binding.toolbar);

        // Set up the bottom navigation
        BottomNavigationView navView = binding.bottomNavigation;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Configure the app bar
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_explore,
                R.id.navigation_create_post,
                R.id.navigation_messages,
                R.id.navigation_profile
        ).build();

        // Don't use NavigationUI for title updates - keep static title
        NavigationUI.setupWithNavController(navView, navController);
        
        // Listen for navigation changes to update toolbar and trigger refresh
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            invalidateOptionsMenu(); // This will call onCreateOptionsMenu again
            
            // Trigger refresh when navigating to different fragments
            int destinationId = destination.getId();
            if (destinationId == R.id.navigation_home) {
                refreshHomeFragment();
            } else if (destinationId == R.id.navigation_explore) {
                refreshExploreFragment();
            } else if (destinationId == R.id.navigation_messages) {
                refreshMessagesFragment();
            } else if (destinationId == R.id.navigation_profile) {
                refreshProfileFragment();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Show search only on home page
        MenuItem searchItem = menu.findItem(R.id.action_search);
        
        if (navController != null) {
            int currentDestination = navController.getCurrentDestination().getId();
            if (currentDestination == R.id.navigation_home) {
                searchItem.setVisible(true);
            } else {
                searchItem.setVisible(false);
            }
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
            return true;
        } else if (id == R.id.action_test_cloudinary) {
            // Temporary test button
            Intent intent = new Intent(this, CloudinaryTestActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showSearchDialog() {
        // Get current fragment
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)
                .getChildFragmentManager()
                .getFragments().get(0);
        
        if (homeFragment != null && homeFragment instanceof HomeFragment) {
            homeFragment.showSearchDialog();
        }
    }
    
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                logout();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void logout() {
        auth.signOut();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startLoginActivity();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
    
    private void loadCurrentUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            firebaseService.getUserProfileData(currentUser.getUid(), new FirebaseService.Callback() {
                @Override
                public void onSuccess() {
                    // Profile loaded successfully
                }

                @Override
                public void onError(String error) {
                    Log.e("MainActivity", "Error loading profile: " + error);
                }
            });
        }
    }
    
    // Refresh methods for each fragment
    private void refreshHomeFragment() {
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag("f0"); // Default tag for first fragment
        if (homeFragment != null && homeFragment.isVisible()) {
            homeFragment.refreshData();
        }
    }
    
    private void refreshExploreFragment() {
        ExploreFragment exploreFragment = (ExploreFragment) getSupportFragmentManager()
                .findFragmentByTag("f1"); // Tag for second fragment
        if (exploreFragment != null && exploreFragment.isVisible()) {
            exploreFragment.refreshData();
        }
    }
    
    private void refreshMessagesFragment() {
        MessageFragment messageFragment = (MessageFragment) getSupportFragmentManager()
                .findFragmentByTag("f3"); // Tag for fourth fragment
        if (messageFragment != null && messageFragment.isVisible()) {
            messageFragment.refreshData();
        }
    }
    
    private void refreshProfileFragment() {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager()
                .findFragmentByTag("f4"); // Tag for fifth fragment
        if (profileFragment != null && profileFragment.isVisible()) {
            profileFragment.refreshData();
        }
    }

}