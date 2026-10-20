package com.example.quickcommerceapplication.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.activities.CartActivity;
import com.example.quickcommerceapplication.activities.MainActivity;
import com.example.quickcommerceapplication.activities.OrderDetailsActivity;
import com.example.quickcommerceapplication.activities.OrderHistoryActivity;
import com.example.quickcommerceapplication.activities.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtils {
    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigation, int currentItemId) {
        bottomNavigation.setSelectedItemId(currentItemId);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == currentItemId) {
                return true;
            }
            Intent intent = null;
            if (itemId == R.id.nav_products) {
                intent = new Intent(activity, MainActivity.class);
            } else if (itemId == R.id.nav_cart) {
                intent = new Intent(activity, CartActivity.class);
            } else if (itemId == R.id.nav_orders) {
                intent = new Intent(activity, OrderHistoryActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}