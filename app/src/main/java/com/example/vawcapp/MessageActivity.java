package com.example.vawcapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView messageList;
    private EditText messageInput;
    private Button sendButton;
    private DatabaseReference databaseReference;
    private String userId; // This will hold the profile name as user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);

        messageList = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);

        // Get the profile name from the intent
        userId = getIntent().getStringExtra("SENDER_NAME");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        // Set up RecyclerView
        messageList.setLayoutManager(new LinearLayoutManager(this));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(userId, message);
                } else {
                    Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String userId, String message) {
        // Create a map to hold the message data
        Map<String, String> messageData = new HashMap<>();
        messageData.put("userId", userId);
        messageData.put("message", message);

        // Store the message in Firebase under the unique userId
        databaseReference.push().setValue(messageData)
                .addOnSuccessListener(aVoid -> {
                    // Clear the input field after sending
                    messageInput.setText("");
                    Toast.makeText(MessageActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Show error message if sending failed
                    Toast.makeText(MessageActivity.this, "Failed to send message. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}