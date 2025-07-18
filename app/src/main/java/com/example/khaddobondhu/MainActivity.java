package com.example.khaddobondhu;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.khaddobondhu.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        
        // Check if user is signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // No user signed in, go to login
            startLoginActivity();
            return;
        }

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

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        
        // Listen for navigation changes to update toolbar
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            invalidateOptionsMenu(); // This will call onCreateOptionsMenu again
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
}