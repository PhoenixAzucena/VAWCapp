package com.example.vawcapp;

import static com.example.vawcapp.ProfileFragment.newInstance;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vawcapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    FrameLayout frameLayout;
    ActivityMainBinding binding;
    private String currentUserId; // To store the current user's ID
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("users");

        // Show LoginFragment initially
        replaceFragment(new ReportFragment()); // Show Login fragment initially

        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.report:
                    replaceFragment(new ReportFragment());
                    break;
                case R.id.profile:
                    if (isUserLoggedIn()) {
                    onLoginSuccess(currentUserId);
                    loadProfileFragment();
                } else {
                    // Show LoginFragment if user is not logged in
                    replaceFragment(new Login());
                }
                break;
                case R.id.locations:
                    replaceFragment(new LocationsFragment());
                    break;
            }
            return true;
        });
    }

    private void loadProfileFragment() {
        // Load the profile fragment after fetching the user ID
        if (isUserLoggedIn()) {
            // Fetch user data from the database using currentUser Id
            databaseReference.child(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userName = task.getResult().child("name").getValue(String.class);

                    String userAge = task.getResult().child("age").getValue(String.class);
                    String userGender = task.getResult().child("gender").getValue(String.class);
                    String userAddress = task.getResult().child("address").getValue(String.class);
                    String userBirthDate = task.getResult().child("birth_date").getValue(String.class);
                    String userEmail = task.getResult().child("email").getValue(String.class);

                    ProfileFragment profileFragment = newInstance(userName, userAge, userGender, userAddress, userBirthDate, userEmail, currentUserId);
                    replaceFragment(profileFragment);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isUserLoggedIn() {
        // Check if the user is logged in by checking the currentUser Id
        return currentUserId != null;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
        fragmentTransaction.commit();
    }

    // Method to set the current user ID after successful login
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    // Method to clear the current user ID for logout
    public void clearCurrentUserId() {
        this.currentUserId = null;
    }
    public void onLoginSuccess(String userId) {
        setCurrentUserId(userId);

        // Navigate to the main activity or load the profile fragment
    }
}