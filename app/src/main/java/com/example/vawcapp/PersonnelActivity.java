package com.example.vawcapp;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vawcapp.databinding.ActivityPersonnelBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PersonnelActivity extends AppCompatActivity {
    ImageButton imageButton;
    FrameLayout frameLayout;
    ActivityPersonnelBinding binding;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonnelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance("https://vawcapp-d92da-default-rtdb.firebaseio.com/").getReference("vawc");

        // Show ReportFragment initially


        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.report:

                    break;
                case R.id.profile:

                break;

                case R.id.locations:

                    break;
            }
            return true;
        });
    }


}