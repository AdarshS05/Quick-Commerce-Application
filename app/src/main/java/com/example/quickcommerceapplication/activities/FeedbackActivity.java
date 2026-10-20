package com.example.quickcommerceapplication.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedbackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_feedback);
        RatingBar rating = findViewById(R.id.ratingBar);
        EditText comment = findViewById(R.id.etFeedback);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_cart);

        findViewById(R.id.btnSubmitFeedback).setOnClickListener(v -> {
            long orderId = getIntent().getLongExtra(Constants.EXTRA_ORDER_ID, -1);
            if (orderId < 0 || rating.getRating() < 1) {
                Toast.makeText(this, "Select a rating first", Toast.LENGTH_SHORT).show();
                return;
            }
            String username = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                    .getString(Constants.KEY_USERNAME, "");
            new DatabaseHelper(this).saveFeedback(orderId, username, rating.getRating(),
                    comment.getText().toString().trim());
            Toast.makeText(this, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

