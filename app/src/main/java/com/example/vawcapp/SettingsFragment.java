package com.example.vawcapp;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


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

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment  {

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
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        btn_newWaypoint = v.findViewById(R.id.btn_newWayPoint);
        btn_showWayPointList = v.findViewById(R.id.btn_showWayPointList);
        btn_showMap = v.findViewById(R.id.btn_showmap);
        tv_wayPointCounts = v.findViewById(R.id.tv_countOfCrumbs);
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INT);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback(){

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);


                updateUIValues(locationResult.getLastLocation());
            }
        } ;
        btn_newWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the gps location

                //add the new location
               ILSApplication globalListClass = (ILSApplication) requireActivity().getApplication();
               savedLocations = globalListClass.getMyLocations();
               savedLocations.add(currentLocation);

            }
        });

        btn_showWayPointList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShowSavedLocationsList.class);
                startActivity(intent);
            }
        });
        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
    public void onRequestPermissionsResult(int requestCode,String permissions[]
            ,int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }

        } catch (SecurityException e){
                    Toast.makeText(getContext(), "This app requires permission to be granted in order to access your location.", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
        }

    }
    private void updateGPS(){
        //get perms from user to track GPS

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)==
        PackageManager.PERMISSION_GRANTED){
    fusedLocationProviderClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            updateUIValues(location);
            currentLocation = location;
        }
    });
        }
        else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
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
            Geocoder geocoder = new Geocoder(getContext());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder addressString = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex();.. i++) {
                        addressString.append(address.getAddressLine(i)).append("\n");
                    }
                    tv_address.setText(addressString.toString());
                } else {
                    tv_address.setText("Address not found");
                }
            } catch (Exception e) {
                tv_address.setText("Unable to get street address");
                Log.e("GeocoderError", e.getMessage(), e);
            }
        } else {
            tv_address.setText("Geocoder not available");
        }

        if (savedLocations == null) {
            savedLocations = new ArrayList<>(); // Initialize with an empty list
        }
        tv_wayPointCounts.setText(Integer.toString(savedLocations.size()));
    }


}