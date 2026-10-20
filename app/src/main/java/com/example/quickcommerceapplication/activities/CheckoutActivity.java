package com.example.quickcommerceapplication.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.CartItem;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.services.OrderTrackingService;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseHelper database;
    private EditText address;
    private List<CartItem> items;
    private double latitude = 28.6139; // Default fallback (New Delhi)
    private double longitude = 77.2090;
    private FusedLocationProviderClient locationClient;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_checkout);
        database = new DatabaseHelper(this);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        address = findViewById(R.id.etAddress);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_cart);

        items = database.getCartItems();
        if (state != null) {
            address.setText(state.getString("address", ""));
            latitude = state.getDouble("latitude", latitude);
            longitude = state.getDouble("longitude", longitude);
        }

        findViewById(R.id.btnCurrentLocation).setOnClickListener(v -> requestLocation());
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> placeOrder());
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    Constants.NOTIFICATION_REQ_CODE);
        }
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_REQ_CODE);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                showLocationOnMap();
            } else {
                // Active single location request fallback if last known location is null
                LocationRequest locationRequest = new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY, 5000)
                        .setMaxUpdates(1)
                        .build();

                locationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        Location freshLocation = locationResult.getLastLocation();
                        if (freshLocation != null) {
                            latitude = freshLocation.getLatitude();
                            longitude = freshLocation.getLongitude();
                            showLocationOnMap();
                        }
                    }
                }, Looper.getMainLooper());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                map.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            requestLocation();
        } else {
            showLocationOnMap(); // Render default marker until permission is granted
        }
    }

    private void showLocationOnMap() {
        if (map == null) return;
        LatLng location = new LatLng(latitude, longitude);
        map.clear();
        map.addMarker(new MarkerOptions().position(location).title("Delivery location"));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_REQ_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    private void placeOrder() {
        String text = address.getText().toString().trim();
        if (text.isEmpty() || items.isEmpty()) {
            Toast.makeText(this, "Enter an address and add an item first", Toast.LENGTH_SHORT).show();
            return;
        }
        double total = 20;
        for (CartItem item : items) total += item.getPrice() * item.getQuantity();
        String username = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                .getString(Constants.KEY_USERNAME, "");
        Order order = new Order(username, text, latitude, longitude, total,
                "Order Placed", System.currentTimeMillis());
        long orderId = database.createOrder(order, items);
        database.clearCart();

        Intent service = new Intent(this, OrderTrackingService.class);
        service.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        ContextCompat.startForegroundService(this, service);

        Intent details = new Intent(this, OrderDetailsActivity.class);
        details.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        startActivity(details);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        state.putString("address", address.getText().toString());
        state.putDouble("latitude", latitude);
        state.putDouble("longitude", longitude);
        super.onSaveInstanceState(state);
    }
}