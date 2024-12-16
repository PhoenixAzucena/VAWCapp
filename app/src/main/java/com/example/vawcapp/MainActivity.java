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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        // Check if user is logged in and fetch user data
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail != null) {
            fetchUserId(userEmail);
        } else {
            // Show LoginFragment if no user email is provided
            replaceFragment(new Login());
        }

        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.report:
                    replaceFragment(new ReportFragment());
                    break;
                case R.id.profile:
                    if (isUserLoggedIn()) {
                    // Pass user data to ProfileFragment
                    String userName = getIntent().getStringExtra("USER_NAME");
                    String userAge = getIntent().getStringExtra("USER_AGE");
                    String userGender = getIntent().getStringExtra("USER_GENDER");
                    String userAddress = getIntent().getStringExtra("USER_ADDRESS");
                    String userBirthDate = getIntent().getStringExtra("USER_BIRTH_DATE");

                    ProfileFragment profileFragment = newInstance(userName, userAge, userGender, userAddress, userBirthDate, userEmail, currentUserId);
                    replaceFragment(profileFragment);
                } else {
                    // Show LoginFragment if user is not logged in
                    replaceFragment(new Login());
                }
                break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });
    }

    private void fetchUserId(String email) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        currentUserId = userSnapshot.getKey(); // Get the user ID (key)
                        // Optionally, you can also retrieve other user data here
                        // e.g., String name = userSnapshot.child("name").getValue(String.class);
                    }
                    // User ID fetched successfully, you can now proceed to load the profile
                    loadProfileFragment(); // Load the profile or any other fragment
                } else {
                    Toast.makeText(MainActivity.this, "User  not found", Toast.LENGTH_SHORT).show();
                    replaceFragment(new Login()); // Show login if user not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileFragment() {
        // Load the profile fragment after fetching the user ID
        if (isUserLoggedIn()) {
            String userName = getIntent().getStringExtra("USER_NAME");
            String userAge = getIntent().getStringExtra("USER_AGE");
            String userGender = getIntent().getStringExtra("USER_GENDER");
            String userAddress = getIntent().getStringExtra("USER_ADDRESS");
            String userBirthDate = getIntent().getStringExtra("USER_BIRTH_DATE");

            ProfileFragment profileFragment = newInstance(userName, userAge, userGender, userAddress, userBirthDate, getIntent().getStringExtra("USER_EMAIL"), currentUserId);
            replaceFragment(profileFragment);
        }
    }

    private boolean isUserLoggedIn() {
        // Check if the user is logged in by checking the currentUser Id
        return currentUserId != null;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Method to set the current user ID after successful login
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }
}