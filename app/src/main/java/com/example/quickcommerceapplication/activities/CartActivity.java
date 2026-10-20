package com.example.quickcommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.adapters.CartAdapter;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.CartItem;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private DatabaseHelper database;
    private List<CartItem> items;
    private CartAdapter adapter;
    private TextView subtotalView, totalView;
    private static final double DELIVERY_FEE = 20.0;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_cart);
        database = new DatabaseHelper(this);
        subtotalView = findViewById(R.id.tvSubtotal);
        totalView = findViewById(R.id.tvTotal);
        RecyclerView list = findViewById(R.id.cartRecyclerView);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_cart);
        list.setLayoutManager(new LinearLayoutManager(this));
        items = database.getCartItems();
        adapter = new CartAdapter(items, new CartAdapter.Listener() {
            @Override public void quantityChanged(CartItem item, int quantity) {
                database.updateCartQuantity(item.getId(), quantity);
                item.setQuantity(quantity);
                adapter.notifyDataSetChanged();
                refreshTotals();
            }
            @Override public void removed(CartItem item) {
                database.removeFromCart(item.getId());
                items.remove(item);
                adapter.notifyDataSetChanged();
                refreshTotals();
            }
        });
        list.setAdapter(adapter);
        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (!items.isEmpty()) startActivity(new Intent(this, CheckoutActivity.class));
        });
        refreshTotals();
    }

    private void refreshTotals() {
        double subtotal = 0;
        for (CartItem item : items) subtotal += item.getPrice() * item.getQuantity();
        subtotalView.setText(String.format("Subtotal: ₹%.2f", subtotal));
        totalView.setText(String.format("Total: ₹%.2f", subtotal + (subtotal > 0 ? DELIVERY_FEE : 0)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(database != null && adapter != null) {
            items.clear();
            items.addAll(database.getCartItems());
            adapter.notifyDataSetChanged();
            refreshTotals();
        }
    }
}

