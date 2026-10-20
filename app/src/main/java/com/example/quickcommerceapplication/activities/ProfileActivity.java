package com.example.quickcommerceapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.adapters.OrderHistoryAdapter;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnLogout;
    private RecyclerView rvOrderHistory;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_profile);

        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String username = prefs.getString(Constants.KEY_USERNAME, "User");
        String fullName = prefs.getString("full_name", username);
        String email = prefs.getString("email", username + "@quickcommerce.com");

        tvProfileName.setText(fullName);
        tvProfileEmail.setText(email);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        List<Order> userOrders = dbHelper.getOrdersByUsername(username);

        OrderHistoryAdapter adapter = new OrderHistoryAdapter(this, userOrders);
        rvOrderHistory.setAdapter(adapter);

        findViewById(R.id.faqItem).setOnClickListener(v ->
            startActivity(new Intent(ProfileActivity.this, FaqActivity.class)));

        btnLogout.setOnClickListener(v -> {
            prefs.edit().putBoolean(Constants.KEY_LOGIN, false).apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}