package com.example.vawcapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LocationListPersonnel extends Fragment {

    private TextView tv_latitude, tv_longitude, tv_address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_list_personnel, container, false);
        tv_latitude = v.findViewById(R.id.tv_latitude);
        tv_longitude = v.findViewById(R.id.tv_longitude);
        tv_address = v.findViewById(R.id.tv_address);

        // Retrieve the data from the intent
        if (getArguments() != null) {
            double latitude = getArguments().getDouble("LATITUDE");
            double longitude = getArguments().getDouble("LONGITUDE");
            String address = getArguments().getString("ADDRESS");

            // Display the data
            tv_latitude.setText(String.valueOf(latitude));
            tv_longitude.setText(String.valueOf(longitude));
            tv_address.setText(address);
        }

        return v;
    }
}