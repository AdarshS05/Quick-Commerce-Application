package com.example.quickcommerceapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean(Constants.KEY_LOGIN, false);
            Intent intent = new Intent(SplashActivity.this, isLoggedIn ? MainActivity.class : LoginActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
