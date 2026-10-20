package com.example.quickcommerceapplication.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FaqActivity extends AppCompatActivity {
    private static final String[] QUESTIONS = {
            "How do I place an order?",
            "How can I track my order?",
            "What payment methods are supported?",
            "How do I change my delivery address?",
            "How can I contact support?"
    };
    private static final String[] ANSWERS = {
            "Add products to Cart, open Checkout, enter your address, and select Place Order.",
            "Open Order Details after checkout. The status changes from Placed to Delivered.",
            "The demo supports Cash on Delivery and UPI.",
            "Change the address during Checkout before placing the order.",
            "Use the support contact supplied by the project team."
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_faq);
        ListView list = findViewById(R.id.faqList);
        TextView answer = findViewById(R.id.tvFaqAnswer);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_profile);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, QUESTIONS));
        list.setOnItemClickListener((parent, view, position, id) ->
                answer.setText(ANSWERS[position]));
    }
}

