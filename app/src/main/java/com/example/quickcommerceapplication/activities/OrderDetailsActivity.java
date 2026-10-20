package com.example.quickcommerceapplication.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView status;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            status.setText(intent.getStringExtra(Constants.EXTRA_ORDER_STATUS));
        }
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_order_details);
        status = findViewById(R.id.tvOrderStatus);
        long orderId = getIntent().getLongExtra(Constants.EXTRA_ORDER_ID, -1);
        String savedStatus = new DatabaseHelper(this).getOrderStatus(orderId);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_orders);
        if (savedStatus != null) status.setText(savedStatus);
        findViewById(R.id.btnFeedback).setOnClickListener(v -> {
            Intent intent = new Intent(this, FeedbackActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
            startActivity(intent);
        });
    }

    @Override protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(Constants.ACTION_ORDER_STATUS_UPDATE));
    }

    @Override protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }
}

