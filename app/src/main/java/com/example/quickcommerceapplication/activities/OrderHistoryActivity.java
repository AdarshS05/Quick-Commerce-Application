package com.example.quickcommerceapplication.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.adapters.OrderHistoryAdapter;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    RecyclerView rv;
    OrderHistoryAdapter orderAdapter;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String username = prefs.getString(Constants.KEY_USERNAME, "User");

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_orders);

        db = new DatabaseHelper(this);
        List<Order> userOrders = db.getOrdersByUsername(username);
        rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new GridLayoutManager(this,1));
        orderAdapter = new OrderHistoryAdapter(this,userOrders);
        rv.setAdapter(orderAdapter);
    }
}