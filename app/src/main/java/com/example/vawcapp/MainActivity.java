package com.example.vawcapp;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vawcapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    FrameLayout frameLayout;
    ActivityMainBinding binding;
    private String currentUserId; // To store the current user's ID
    private DatabaseReference notificationsReference;
    private DatabaseReference databaseReference; // Declare the databaseReference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database reference for notifications
        notificationsReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("notifications");

        // Initialize Firebase Database reference for user data
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("users");

        // Listen for new notifications
        listenForNotifications();

        // Show ReportFragment initially
        replaceFragment(new ReportFragment());

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

    private void listenForNotifications() {
        notificationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String notificationMessage = snapshot.getValue(String.class);
                    showAlertDialog(notificationMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to read notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("New Notification")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private boolean isUserLoggedIn() {
        // Check if the user is logged in by checking the currentUser  Id
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
        // Optionally, you can load the profile fragment or perform other actions
    }

    private void loadProfileFragment() {
        // Load the profile fragment after fetching the user ID
        if (isUserLoggedIn()) {
            // Fetch user data from the database using currentUser  Id
            databaseReference.child(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userName = task.getResult().child("name").getValue(String.class);
                    String userAge = task.getResult().child("age").getValue(String.class);
                    String userGender = task.getResult().child("gender").getValue(String.class);
                    String userAddress = task.getResult().child("address").getValue(String.class);
                    String userBirthDate = task.getResult().child("birth_date").getValue(String.class);
                    String userEmail = task.getResult().child("email").getValue(String.class);

                    ProfileFragment profileFragment = ProfileFragment.newInstance(userName, userAge, userGender, userAddress, userBirthDate, userEmail, currentUserId);
                    replaceFragment(profileFragment);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}