package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportFragment extends Fragment {

    private Button btn_sendLocation;
    private DatabaseReference databaseReference; // Firebase Database reference

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        btn_sendLocation = v.findViewById(R.id.btn_sendLocation); // Make sure to add this button in your XML layout

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("locations");

        btn_sendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLatestLocationData();
            }
        });

        return v;
    }

    private void fetchLatestLocationData() {
        databaseReference.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        LocationData locationData = snapshot.getValue(LocationData.class);
                        if (locationData != null) {
                            sendLocationData(locationData.getLatitude(), locationData.getLongitude(), locationData.getAddress());
                        }
                    }
                } else {
                    // Handle case where no location data exists
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void sendLocationData(double latitude, double longitude, String address) {
        Intent intent = new Intent(getActivity(), LocationListPersonnel.class);
        // Pass the latitude, longitude, and address as extras
        intent.putExtra("LATITUDE", latitude);
        intent.putExtra("LONGITUDE", longitude);
        intent.putExtra("ADDRESS", address);
        startActivity(intent);
    }

    public void setUserInfo(String userName, String userName1) {
    }
}