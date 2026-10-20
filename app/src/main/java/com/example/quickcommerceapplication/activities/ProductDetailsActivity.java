package com.example.quickcommerceapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.Product;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailsActivity extends AppCompatActivity {
    private ImageView ivDetailImage;
    private TextView tvDetailTitle, tvDetailPrice, tvDetailDescription, tvQuantity;
    private Button btnMinus, btnPlus, btnAddToCart;
    ImageButton btnShare;

    private DatabaseHelper dbHelper;
    private Product currentProduct;
    private int quantity = 1;
    @SuppressLint({"WrongViewCast", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);

        dbHelper = new DatabaseHelper(this);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnShare = findViewById(R.id.btnShare);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_products);

        int productId= getIntent().getIntExtra(Constants.EXTRA_PRODUCT_ID,-1);
        if(productId!=-1){
            currentProduct = dbHelper.getProductById(productId);
            if(currentProduct != null) {
                populateUI(currentProduct);
            }
        }
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v->{
            if (quantity>1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnShare.setOnClickListener(v -> {
            if(currentProduct != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + currentProduct.getName() + " for â‚¹" + String.format("%.2f", currentProduct.getPrice()));
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        btnAddToCart.setOnClickListener(v -> {
            if(currentProduct != null){
                addToCart(currentProduct, quantity);
            }
        });
    }

    private void populateUI(Product product){
        tvDetailTitle.setText(product.getName());
        tvDetailPrice.setText("₹"+String.format("%.2f", product.getPrice()));
        tvDetailDescription.setText(product.getDescription());

        if(product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrl()).placeholder(R.drawable.ic_launcher_background).error(R.drawable.ic_launcher_background).into(ivDetailImage);
        }
    }

    private void addToCart(Product product, int qty) {
        try {
            dbHelper.addToCart(product.getId(), product.getName(), product.getPrice(),
                    qty, product.getImageUrl());
            Toast.makeText(this,"Added "+qty+ "item(s) to Cart", Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLiteException exception) {
            Toast.makeText(this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
        }
    }
}
