package com.example.vawcapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationsFragment extends Fragment  {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INT = 5;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView tv_lat, tv_lon, tv_altitude,
    tv_accuracy, tv_speed, tv_sensor, tv_updates,tv_address, tv_wayPointCounts;
    Switch sw_locationupdates,sw_gps;
    boolean updateOn = false;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    Button btn_newWaypoint, btn_showWayPointList, btn_showMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    List<Location> savedLocations;
    public LocationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationsFragment newInstance(String param1, String param2) {
        LocationsFragment fragment = new LocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("locations");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        tv_lat = (TextView) v.findViewById(R.id.tv_lat);
        tv_lon = (TextView) v.findViewById(R.id.tv_lon);
        tv_altitude = (TextView) v.findViewById(R.id.tv_altitude);
        tv_accuracy = (TextView) v.findViewById(R.id.tv_accuracy);
        tv_speed = v.findViewById(R.id.tv_speed);
        tv_sensor = v.findViewById(R.id.tv_sensor);
        tv_updates = v.findViewById(R.id.tv_updates);
        tv_address = v.findViewById(R.id.tv_address);
        sw_locationupdates = v.findViewById(R.id.sw_locationsupdates);
        sw_gps = v.findViewById(R.id.sw_gps);
        locationRequest = new LocationRequest();
       
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INT);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        btn_saveLocation = v.findViewById(R.id.btn_saveLocation); // Make sure to add this button in your XML layout

        btn_saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    saveLocationToDatabase(currentLocation);
                } else {
                    Toast.makeText(getContext(), "Current location is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationCallBack = new LocationCallback(){

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);


                updateUIValues(locationResult.getLastLocation());
            }
        } ;




        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()){
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });
        sw_locationupdates.setOnClickListener(new View.OnClickListener(){
        @Override
            public void onClick(View v){
            if (sw_locationupdates.isChecked()){
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        }
        });
        updateGPS();
        // Inflate the layout for this fragment
        return v;
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is NOT being Tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");

        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");


        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);

    }

    private void startLocationUpdates() {

        try {
            tv_updates.setText("Location is being tracked");
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
            updateGPS();
        } catch (SecurityException e){
            Toast.makeText(getContext(), "This app requires permission to be granted in order to access your location.", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, update GPS
                    updateGPS();
                } else {
                    // Permission denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // User selected "Don't ask again"
                        Toast.makeText(getContext(), "Please enable location permission in app settings.", Toast.LENGTH_LONG).show();
                        // Optionally, you can open the app settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "Permission denied. Unable to access location.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Check if the permission is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, get the last location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (isAdded()) { // Check if the fragment is currently added to its activity
                        updateUIValues(location);
                        currentLocation = location;
                    } else if (location == null) {
                        Toast.makeText(requireContext(), "Unable to get last location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Check if we should show an explanation
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user asynchronously
                Toast.makeText(getContext(), "Location permission is needed to show your location.", Toast.LENGTH_LONG).show();
                // After showing the explanation, request the permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            } else {
                // No explanation needed; request the permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (sw_locationupdates.isChecked()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()){
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not Available.");
        }
        if (location.hasSpeed()){
            tv_altitude.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_altitude.setText("Not Available.");
        }

        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder addressString = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressString.append(address.getAddressLine(i)).append("\n");
                    }
                    tv_address.setText(addressString.toString());
                } else {
                    tv_address.setText("Address not found");
                }
            } catch (Exception e) {
                tv_address.setText("Unable to get street address");
                Log.e("GeocoderError", "Error retrieving address: " + e.getMessage(), e);
            }
        } else {
            tv_address.setText("Geocoder not available");
        }


        if (savedLocations == null) {
            savedLocations = new ArrayList<>(); // Initialize with an empty list
        }

    }

    private DatabaseReference databaseReference; // Firebase Database reference
    private Button btn_saveLocation; // Button to save location


        // Initialize Firebase Database reference






    private void saveLocationToDatabase(Location location) {
        String key = databaseReference.push().getKey(); // Create a unique key for the location
        if (key != null) {
            String address = getAddressFromLocation(location);
            LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude(), address);
            databaseReference.child(key).setValue(locationData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Location saved successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save location", Toast.LENGTH_SHORT).show());
        }
    }

    private String getAddressFromLocation(Location location) {
        String address = "Address not found";
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(requireContext());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addr = addresses.get(0);
                    StringBuilder addressString = new StringBuilder();
                    for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++) {
                        addressString.append(addr.getAddressLine(i)).append("\n");
                    }
                    address = addressString.toString();
                }
            } catch (Exception e) {
                Log.e("GeocoderError", "Error retrieving address: " + e.getMessage(), e);
            }
        }
        return address;
    }

    // Other methods...
}
