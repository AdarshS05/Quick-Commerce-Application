package com.example.quickcommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.quickcommerceapplication.R;
import com.example.quickcommerceapplication.adapters.ProductAdapter;
import com.example.quickcommerceapplication.database.DatabaseHelper;
import com.example.quickcommerceapplication.models.Product;
import com.example.quickcommerceapplication.utils.Constants;
import com.example.quickcommerceapplication.utils.NavigationUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView actvSearch;
    private RecyclerView rvProducts;
    private BottomNavigationView bottomNavigation;

    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private final Map<String, Integer> categoryMap = new HashMap<>();
    private final Set<String> suggestionsSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        View topHeader = findViewById(R.id.topHeader);
        actvSearch = topHeader.findViewById(R.id.actvSearch);
        rvProducts = findViewById(R.id.rvProducts);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, new ArrayList<>());
        rvProducts.setAdapter(productAdapter);

        NavigationUtils.setupBottomNavigation(this, bottomNavigation, R.id.nav_products);
        setupDynamicSearch();
        fetchCategoriesAndProducts();
    }

    private void setupDynamicSearch() {
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        actvSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSuggestion = (String) parent.getItemAtPosition(position);

            Product matchedProduct = null;
            for (Product p : allProducts) {
                if (p.getName() != null && p.getName().equalsIgnoreCase(selectedSuggestion)) {
                    matchedProduct = p;
                    break;
                }
            }

            if (matchedProduct != null) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, matchedProduct.getId());
                startActivity(intent);
            } else {
                filterProducts(selectedSuggestion);
            }
        });
    }
    private void filterProducts(String query) {
        if (query.isEmpty()) {
            productAdapter.updateData(allProducts);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Product product : allProducts) {
            boolean matchesName = product.getName() != null && product.getName().toLowerCase().contains(lowerQuery);
            boolean matchesDescription = product.getDescription() != null && product.getDescription().toLowerCase().contains(lowerQuery);
            boolean matchesCategory = false;

            for(Map.Entry<String,Integer> entry : categoryMap.entrySet()) {
                if (entry.getValue() == product.getCategoryId() && entry.getKey().contains(lowerQuery)) {
                    matchesCategory = true;
                    break;
                }

            }

            if (matchesName || matchesDescription || matchesCategory) {
                filteredList.add(product);
            }
        }

        productAdapter.updateData(filteredList);
    }

    private void fetchCategoriesAndProducts() {
        String categoriesUrl = Constants.API_CATEGORIES_URL;

        JsonArrayRequest catRequest = new JsonArrayRequest(Request.Method.GET, categoriesUrl, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            String catName = response.getString(i);
                            categoryMap.put(catName.toLowerCase(), i + 1);
                            suggestionsSet.add(catName.substring(0, 1).toUpperCase() + catName.substring(1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fetchProducts();
                },
                error -> fetchProducts() // Fallback to fetching products directly
        );

        Volley.newRequestQueue(this).add(catRequest);
    }

    private void fetchProducts() {
        String url = Constants.API_PRODUCTS_URL;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Product> apiProducts = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            double price = obj.getDouble("price");
                            String description = obj.getString("description");
                            String imgUrl = obj.getString("image");
                            String categoryStr = obj.getString("category").toLowerCase();

                            int categoryId = categoryMap.getOrDefault(categoryStr, 1);

                            Product product = new Product(id, categoryId, title, description, price, imgUrl, 50);
                            apiProducts.add(product);
                            suggestionsSet.add(title);
                        }

                        if (!apiProducts.isEmpty()) {
                            dbHelper.syncProducts(apiProducts);
                            allProducts = dbHelper.getAllProducts();
                            productAdapter.updateData(allProducts);

                            // Populate Autocomplete search suggestions
                            ArrayAdapter<String> autoAdapter = new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    new ArrayList<>(suggestionsSet)
                            );
                            actvSearch.setAdapter(autoAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "API offline, loading cached products", Toast.LENGTH_SHORT).show();
                    allProducts = dbHelper.getAllProducts();
                    productAdapter.updateData(allProducts);
                });

        Volley.newRequestQueue(this).add(request);
    }
}