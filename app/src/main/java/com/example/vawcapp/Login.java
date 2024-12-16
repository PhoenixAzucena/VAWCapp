package com.example.vawcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class Login extends Fragment {

    private EditText username, password;
    private Button loginButton;
    private TextView signupPrompt;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login_button);
        signupPrompt = view.findViewById(R.id.SignUp);

        loginButton.setOnClickListener(v -> {
            // Handle login logic here
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // For demonstration, we will just show a Toast
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Here you would typically check the credentials against your database
                Toast.makeText(getActivity (), "Login successful!", Toast.LENGTH_SHORT).show();
                // Navigate to the main activity or another fragment after successful login
                // You can save user data in SharedPreferences or a similar method to keep track of login status
            }
        });

        signupPrompt.setOnClickListener(v -> {
            // Navigate to signup activity
            Intent intent = new Intent(getActivity(), SignUpActivity.class);
            startActivity(intent);
        });

        return view;
    }
}