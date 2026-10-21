package com.example.quickcommerceapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.activities.CartActivity;
import com.example.quickcommerceapplication.activities.MainActivity;
import com.example.quickcommerceapplication.activities.OrderHistoryActivity;
import com.example.quickcommerceapplication.activities.ProfileActivity;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NavigationUtils {

    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigation, int currentItemId) {
        // Setup Bottom Navigation
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(currentItemId);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == currentItemId) return true;

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

        // Setup Search on Top Header globally for all activities
        setupHeaderSearch(activity);
    }

    private static void setupHeaderSearch(Activity activity) {
        View topHeader = activity.findViewById(R.id.topHeader);
        if (topHeader == null) return;

        AutoCompleteTextView actvSearch = topHeader.findViewById(R.id.actvSearch);
        if (actvSearch == null) return;

        DatabaseHelper db = new DatabaseHelper(activity);
        List<String> suggestions = db.getAllSearchSuggestions();
        if (!suggestions.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_dropdown_item_1line,
                    suggestions
            );
            actvSearch.setAdapter(adapter);
        }

        // Redirect to MainActivity when user taps a suggestion
        actvSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedQuery = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("SEARCH_QUERY", selectedQuery);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        });
    }
}