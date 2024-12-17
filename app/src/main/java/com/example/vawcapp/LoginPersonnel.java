package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPersonnel extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpersonnel);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("vawc");

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Set up the login button click listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Validate input
                if (name.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginPersonnel.this, "Please enter both name and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Check credentials in Firebase
                    checkCredentials(name, password);
                }
            }
        });
    }

    private void checkCredentials(String name, String password) {
        databaseReference.orderByChild("personnel").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, check password
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("personnelpassword").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Successful login
                            Toast.makeText(LoginPersonnel.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginPersonnel.this, PersonnelActivity.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        } else {
                            // Incorrect password
                            Toast.makeText(LoginPersonnel.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // User does not exist
                    Toast.makeText(LoginPersonnel.this, "User  not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginPersonnel.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}