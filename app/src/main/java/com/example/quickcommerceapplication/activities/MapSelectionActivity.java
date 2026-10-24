package com.example.quickcommerceapplication.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.quickcommerceapplication.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapSelectionActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    public static final String EXTRA_LATITUDE = "selected_latitude";
    public static final String EXTRA_LONGITUDE = "selected_longitude";
    public static final String EXTRA_ADDRESS = "selected_address";

    private static final int LOCATION_PERMISSION_REQUEST = 5101;

    private GoogleMap map;
    private FusedLocationProviderClient locationClient;
    private Marker marker;
    private LatLng selectedPoint;

    private EditText addressPreview;
    private Button confirmButton;

    private final ExecutorService geocoderExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        addressPreview = findViewById(R.id.tvSelectedAddress);
        confirmButton = findViewById(R.id.btnConfirmLocation);

        confirmButton.setEnabled(false);

        locationClient =
                LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment fragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.selectionMap);

        if (fragment != null) {
            fragment.getMapAsync(this);
        }

        confirmButton.setOnClickListener(v -> confirmSelection());

        findViewById(R.id.btnMyLocation)
                .setOnClickListener(v -> moveToCurrentLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setZoomControlsEnabled(true);

        // No search bar: tap anywhere on the map to select a location.
        map.setOnMapClickListener(this::selectPoint);

        double lat = getIntent().getDoubleExtra(
                EXTRA_LATITUDE, Double.NaN
        );

        double lng = getIntent().getDoubleExtra(
                EXTRA_LONGITUDE, Double.NaN
        );

        if (!Double.isNaN(lat) && !Double.isNaN(lng)) {
            LatLng previousPoint = new LatLng(lat, lng);
            map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(previousPoint, 16f)
            );
            selectPoint(previousPoint);

        } else if (hasLocationPermission()) {
            moveToCurrentLocation();

        } else {
            map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(20.5937, 78.9629),
                            4.5f
                    )
            );
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void moveToCurrentLocation() {
        if (map == null) return;

        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng point = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude()
                            );

                            map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                            point, 16f
                                    )
                            );

                            selectPoint(point);
                        } else {
                            Toast.makeText(
                                    this,
                                    "Current location is unavailable. Tap the map to choose a location.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    "Location permission is required.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void selectPoint(LatLng point) {
        selectedPoint = point;

        if (marker != null) {
            marker.remove();
        }

        marker = map.addMarker(
                new MarkerOptions()
                        .position(point)
                        .title("Delivery location")
        );

        addressPreview.setText("Finding address...");
        confirmButton.setEnabled(false);

        geocoderExecutor.execute(() -> {
            String resolvedAddress = resolveAddress(point);

            runOnUiThread(() -> {
                if (isFinishing()
                        || selectedPoint == null
                        || !selectedPoint.equals(point)) {
                    return;
                }

                if (resolvedAddress != null) {
                    addressPreview.setText(resolvedAddress);
                } else {
                    // Still allow selection if reverse geocoding fails.
                    addressPreview.setText(
                            "Pinned location: "
                                    + point.latitude + ", "
                                    + point.longitude
                                    + ". Add your full delivery address here."
                    );
                }

                confirmButton.setEnabled(true);
            });
        });
    }

    private String resolveAddress(LatLng point) {
        if (!Geocoder.isPresent()) {
            return null;
        }

        try {
            Geocoder geocoder =
                    new Geocoder(this, Locale.getDefault());

            List<Address> results = geocoder.getFromLocation(
                    point.latitude,
                    point.longitude,
                    1
            );

            if (results == null || results.isEmpty()) {
                return null;
            }

            Address result = results.get(0);

            String line = result.getAddressLine(0);

            if (line != null && !line.trim().isEmpty()) {
                return line;
            }

            return null;

        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private void confirmSelection() {
        if (selectedPoint == null) {
            Toast.makeText(
                    this,
                    "Please tap the map to select a location.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String selectedAddress =
                addressPreview.getText().toString().trim();

        if (selectedAddress.isEmpty()
                || selectedAddress.equals("Finding address...")) {
            Toast.makeText(
                    this,
                    "Please wait for the address or enter it manually.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent result = new Intent();

        result.putExtra(EXTRA_LATITUDE, selectedPoint.latitude);
        result.putExtra(EXTRA_LONGITUDE, selectedPoint.longitude);
        result.putExtra(EXTRA_ADDRESS, selectedAddress);

        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (hasLocationPermission()) {
                moveToCurrentLocation();
            } else {
                Toast.makeText(
                        this,
                        "You can still tap the map to choose a location.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        geocoderExecutor.shutdown();
        super.onDestroy();
    }
}