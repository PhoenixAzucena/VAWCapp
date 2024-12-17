package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends Fragment {

    private EditText username, password;
    private Button loginButton;
    private TextView signupPrompt;
    private DatabaseReference databaseReference;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login_button);
        signupPrompt = view.findViewById(R.id.SignUp);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("users");

        loginButton.setOnClickListener(v -> {
            // Handle login logic here
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Check credentials against Firebase
                authenticateUser (user, pass);
            }
        });

        signupPrompt.setOnClickListener(v -> {
            // Navigate to signup activity
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void authenticateUser (String username, String password) {
        databaseReference.orderByChild("name").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Login successful, fetch user data
                            String userName = userSnapshot.child("name").getValue(String.class);
                            String userAge = userSnapshot.child("age").getValue(String.class);
                            String userGender = userSnapshot.child("gender").getValue(String.class);
                            String userAddress = userSnapshot.child("address").getValue(String.class);
                            String userBirthDate = userSnapshot.child("birth_date").getValue(String.class);
                            String userEmail = userSnapshot.child("email").getValue(String.class);
                            String currentUserId = userSnapshot.getKey(); // Get the user ID

                            // Navigate to ProfileFragment
                            MainActivity mainActivity = (MainActivity) getActivity();
                            if (mainActivity != null) {
                                mainActivity.setCurrentUserId(currentUserId); // Set current user ID
                                ProfileFragment profileFragment = ProfileFragment.newInstance(userName, userAge, userGender, userAddress, userBirthDate, userEmail, currentUserId);
                                mainActivity.replaceFragment(profileFragment);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "User  not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}