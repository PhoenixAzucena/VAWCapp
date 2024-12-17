package com.example.vawcapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_AGE = "age";
    private static final String ARG_GENDER = "gender";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_BIRTH_DATE = "birth_date";
    private static final String ARG_EMAIL = "email";

    private String userName;
    private String userAge;
    private String userGender;
    private String userAddress;
    private String userBirthDate;
    private String userEmail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String userName, String userAge, String userGender,
                                              String userAddress, String userBirthDate,
                                              String userEmail, String currentUserId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, userName);
        args.putString(ARG_AGE, userAge);
        args.putString(ARG_GENDER, userGender);
        args.putString(ARG_ADDRESS, userAddress);
        args.putString(ARG_BIRTH_DATE, userBirthDate);
        args.putString(ARG_EMAIL, userEmail);
        args.putString(ARG_NAME, currentUserId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_NAME);
            userAge = getArguments().getString(ARG_AGE);
            userGender = getArguments().getString(ARG_GENDER);
            userAddress = getArguments().getString(ARG_ADDRESS);
            userBirthDate = getArguments().getString(ARG_BIRTH_DATE);
            userEmail = getArguments().getString(ARG_EMAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find TextViews to display user information
        TextView usernameTextView = view.findViewById(R.id.name);
        TextView ageTextView = view.findViewById(R.id.age);
        TextView genderTextView = view.findViewById(R.id.gender);
        TextView addressTextView = view.findViewById(R.id.address);
        TextView birthDateTextView = view.findViewById(R.id.birth_date);
        TextView emailTextView = view.findViewById(R.id.email);
        Button logoutButton = view.findViewById(R.id.logout_button); // Find the logout button

        // Set the user information to the TextViews with labels
        usernameTextView.setText("Name: " + userName);
        ageTextView.setText("Age: " + userAge);
        genderTextView.setText("Gender: " + userGender);
        addressTextView.setText("Address: " + userAddress);
        birthDateTextView.setText("Birth Date: " + userBirthDate);
        emailTextView.setText("Email: " + userEmail);

        // Set up the logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Handle logout logic
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.setCurrentUserId(null); // Clear the current user ID
                mainActivity.replaceFragment(new Login()); // Replace with Login fragment
                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}