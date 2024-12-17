package com.example.vawcapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RespondFragment extends Fragment {

    private Button respondButton;
    private DatabaseReference databaseReference;

    public RespondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_respond, container, false);
        respondButton = v.findViewById(R.id.respond);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        respondButton.setOnClickListener(view -> {
            // Send notification to the database
            String notificationMessage = "Alert from VAWC: Help is on the way!";
            databaseReference.push().setValue(notificationMessage)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Notification sent!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send notification", Toast.LENGTH_SHORT).show());
        });

        return v;
    }
}