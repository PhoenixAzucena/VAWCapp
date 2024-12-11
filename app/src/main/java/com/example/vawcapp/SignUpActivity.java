package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, age, address;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        address = findViewById(R.id.address);
        nextButton = findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate input
                if (name.getText().toString().isEmpty() ||
                        age.getText().toString().isEmpty() ||
                        address.getText().toString().isEmpty()) {
                    // Show error message (you can use Toast or Snackbar)
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Create an Intent to start SignupStep2Activity
                    Intent intent = new Intent(SignUpActivity.this, SignUpActivityStep2.class);
                    // Pass the data to the next activity
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("age", age.getText().toString());
                    intent.putExtra("address", address.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}