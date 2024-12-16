package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, age, gender, address, birthDate, email, password; // Added password field
    private Button signupButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI elements
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.address);
        birthDate = findViewById(R.id.birth_date);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password); // Initialize password field
        signupButton = findViewById(R.id.signup_button);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input
                if (name.getText().toString().isEmpty() ||
                        age.getText().toString().isEmpty() ||
                        gender.getText().toString().isEmpty() ||
                        address.getText().toString().isEmpty() ||
                        birthDate.getText().toString().isEmpty() ||
                        email.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty()) { // Check if password is empty
                    // Show error message
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Use the profile name as the user ID
                    String userId = name.getText().toString().trim(); // Use the name as the user ID

                    // Create a map to hold user data
                    Map<String, String> userData = new HashMap<>();
                    userData.put("name", userId); // Store the name
                    userData.put("age", age.getText().toString());
                    userData.put("gender", gender.getText().toString());
                    userData.put("address", address.getText().toString());
                    userData.put("birth_date", birthDate.getText().toString());
                    userData.put("email", email.getText().toString());
                    userData.put("password", password.getText().toString()); // Store the password

                    // Store user data in Firebase under the unique userId
                    databaseReference.child(userId).setValue(userData)
                            .addOnSuccessListener(aVoid -> {
                                // Show success message
                                Toast.makeText(SignUpActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to MainActivity and pass user data
                                // Inside SignUpActivity.java after successful signup
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.putExtra("USER_NAME", name.getText().toString());
                                intent.putExtra("USER_AGE", age.getText().toString());
                                intent.putExtra("USER_GENDER", gender.getText().toString());
                                intent.putExtra("USER_ADDRESS", address.getText().toString());
                                intent.putExtra("USER_BIRTH_DATE", birthDate.getText().toString());
                                intent.putExtra("USER_EMAIL", email.getText().toString());
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Show error message if insertion failed
                                Toast.makeText(SignUpActivity.this, "Signup Failed. Please try again.", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }
}