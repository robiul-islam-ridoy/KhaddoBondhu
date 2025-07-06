package com.example.khaddobondhu;

<<<<<<< HEAD
import android.os.Bundle;
=======
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
<<<<<<< HEAD
import com.example.khaddobondhu.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
=======
import com.example.khaddobondhu.auth.LoginActivity;
import com.example.khaddobondhu.databinding.ActivityMainBinding;
import com.example.khaddobondhu.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
<<<<<<< HEAD
=======
    private FirebaseAuth auth;
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

<<<<<<< HEAD
=======
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        
        // Check if user is signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // No user signed in, go to login
            startLoginActivity();
            return;
        }

>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
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
<<<<<<< HEAD
=======
        
        // Listen for navigation changes to update toolbar
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            invalidateOptionsMenu(); // This will call onCreateOptionsMenu again
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Show search and filter only on home page
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        
        if (navController != null) {
            int currentDestination = navController.getCurrentDestination().getId();
            if (currentDestination == R.id.navigation_home) {
                searchItem.setVisible(true);
                filterItem.setVisible(true);
            } else {
                searchItem.setVisible(false);
                filterItem.setVisible(false);
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
        } else if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutConfirmation();
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
    
    private void showFilterDialog() {
        // Get current fragment
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)
                .getChildFragmentManager()
                .getFragments().get(0);
        
        if (homeFragment != null && homeFragment instanceof HomeFragment) {
            homeFragment.showFilterDialog();
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
>>>>>>> 1ea8b2d (Backend Development Progress: Complete Firebase integration, Cloudinary image upload, user authentication, post management, and profile features)
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}