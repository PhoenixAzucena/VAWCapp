package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView messageList;
    private EditText messageInput;
    private Button sendButton;
    private DatabaseReference databaseReference;
    private String userId; // This will hold the user ID
    private String userName; // This will hold the user's name
    private MessageAdapter messageAdapter;
    private List<Message> messages; // List to hold messages
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);

        messageList = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        backButton = findViewById(R.id.BackButton);

        // Get the user ID and name from the intent
        userId = getIntent().getStringExtra("ARG_USER_ID");
        userName = getIntent().getStringExtra("ARG_USER_NAME");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("messages");

        // Set up RecyclerView
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(messageAdapter);

        // Fetch messages from Firebase
        fetchMessages();

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(userName, message); // Use userName as the sender
            } else {
                Toast.makeText(MessageActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            // Finish the current activity and return to the previous one in the back stack
            finish();
        });
    }

    private void sendMessage(String senderName, String message) {
        long timestamp = System.currentTimeMillis(); // Get the current timestamp
        Message messageData = new Message(senderName, message, timestamp);

        // Store the message in Firebase
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

    private void fetchMessages() {
        // Listen for new messages in the Firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message); // Add the message to the list
                    }
                }
                messageAdapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MessageActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}