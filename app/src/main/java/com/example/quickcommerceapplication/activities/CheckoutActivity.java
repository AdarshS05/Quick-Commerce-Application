package com.example.quickcommerceapplication.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.CartItem;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.services.OrderTrackingService;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private EditText address;
    private Button placeOrderButton;
    private List<CartItem> items;
    RadioButton cod, upi;

    private double latitude = Double.NaN;
    private double longitude = Double.NaN;

    private final ActivityResultLauncher<Intent> mapSelectionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            Intent data = result.getData();

                            latitude = data.getDoubleExtra(
                                    MapSelectionActivity.EXTRA_LATITUDE,
                                    Double.NaN
                            );

                            longitude = data.getDoubleExtra(
                                    MapSelectionActivity.EXTRA_LONGITUDE,
                                    Double.NaN
                            );

                            String selectedAddress = data.getStringExtra(
                                    MapSelectionActivity.EXTRA_ADDRESS
                            );

                            if (selectedAddress != null
                                    && !selectedAddress.trim().isEmpty()) {
                                address.setText(selectedAddress);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        database = new DatabaseHelper(this);
        cod = findViewById(R.id.cod);
        upi = findViewById(R.id.upi);

        address = findViewById(R.id.etAddress);
        placeOrderButton = findViewById(R.id.btnPlaceOrder);

        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        NavigationUtils.setupBottomNavigation(
                this,
                bottomNavigation,
                R.id.nav_cart
        );

        if (savedInstanceState != null) {
            address.setText(savedInstanceState.getString("address", ""));
            latitude = savedInstanceState.getDouble("latitude", Double.NaN);
            longitude = savedInstanceState.getDouble("longitude", Double.NaN);
        }

        findViewById(R.id.btnChooseLocation).setOnClickListener(v -> openMapSelection());

        placeOrderButton.setOnClickListener(v -> placeOrder());

        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        items = database.getCartItems();
    }

    private void openMapSelection() {
        Intent intent = new Intent(this, MapSelectionActivity.class);

        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
            intent.putExtra(MapSelectionActivity.EXTRA_LATITUDE, latitude);
            intent.putExtra(MapSelectionActivity.EXTRA_LONGITUDE, longitude);
        }

        mapSelectionLauncher.launch(intent);
    }

    private void placeOrder() {
        String deliveryAddress = address.getText().toString().trim();

        if (deliveryAddress.isEmpty()) {
            address.setError("Please enter or select a delivery address");
            Toast.makeText(
                    this,
                    "Please select your delivery location",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        items = database.getCartItems();

        if (items == null || items.isEmpty()) {
            Toast.makeText(
                    this,
                    "Your cart is empty. Please add an item first.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        double total = 20.0; // Delivery/base charge

        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }

        String username = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).getString(Constants.KEY_USERNAME, "");

        Order order = new Order(username, deliveryAddress, latitude, longitude, total, "Order Placed", System.currentTimeMillis());

        long orderId = database.createOrder(order, items);

        if (orderId <= 0) {
            Toast.makeText(this, "Unable to place your order. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        database.clearCart();

        Intent serviceIntent = new Intent(this, OrderTrackingService.class);
        serviceIntent.putExtra(Constants.EXTRA_ORDER_ID, orderId);

        ContextCompat.startForegroundService(this, serviceIntent);

        Intent detailsIntent = new Intent(this, OrderDetailsActivity.class);
        detailsIntent.putExtra(Constants.EXTRA_ORDER_ID, orderId);

        startActivity(detailsIntent);
        finish();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
        ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            android.Manifest.permission.POST_NOTIFICATIONS
                    },
                    Constants.NOTIFICATION_REQ_CODE
            );
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("address", address.getText().toString());
        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);

        super.onSaveInstanceState(outState);
    }
}