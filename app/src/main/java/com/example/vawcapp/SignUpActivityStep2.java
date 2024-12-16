package com.example.vawcapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivityStep2 extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button signupButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupstep2);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signup_button);

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve data from the previous activity
        String name = getIntent().getStringExtra("name");
        String age = getIntent().getStringExtra("age");
        String address = getIntent().getStringExtra("address");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input
                if (email.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        confirmPassword.getText().toString().isEmpty()) {
                    // Show error message
                    Toast.makeText(SignUpActivityStep2.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    // Show error message for password mismatch
                    Toast.makeText(SignUpActivityStep2.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert user data into the database
                    boolean isInserted = databaseHelper.insertUser (name, age, address, email.getText().toString(), password.getText().toString());
                    if (isInserted) {
                        // Show success message
                        Toast.makeText(SignUpActivityStep2.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        // Optionally, you can finish the activity or navigate to another screen
                        finish();
                    } else {
                        // Show error message if insertion failed
                        Toast.makeText(SignUpActivityStep2.this, "Signup Failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}