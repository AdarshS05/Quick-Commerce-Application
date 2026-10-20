package com.example.quickcommerceapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quickcommerceapplication.models.CartItem;
import com.example.quickcommerceapplication.models.Order;
import com.example.quickcommerceapplication.models.Category;
import com.example.quickcommerceapplication.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "quickcommerce.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CART = "cart";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String TABLE_FEEDBACK = "feedback";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, icon_res INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, category_id INTEGER, name TEXT NOT NULL, " +
                "description TEXT, price REAL NOT NULL, image_url TEXT, stock INTEGER DEFAULT 0, " +
                "FOREIGN KEY(category_id) REFERENCES categories(id))");
        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER, product_name TEXT, " +
                "price REAL, quantity INTEGER, image_url TEXT, " +
                "FOREIGN KEY(product_id) REFERENCES products(id))");
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, address TEXT, latitude REAL, " +
                "longitude REAL, total_amount REAL, status TEXT, order_date INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER, product_name TEXT, " +
                "price REAL, quantity INTEGER, FOREIGN KEY(order_id) REFERENCES orders(id))");
        db.execSQL(createFeedbackTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) db.execSQL(createFeedbackTable());
    }

    private String createFeedbackTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_FEEDBACK + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL UNIQUE, " +
                "username TEXT NOT NULL, rating REAL NOT NULL CHECK(rating >= 1 AND rating <= 5), " +
                "comment TEXT, created_at INTEGER NOT NULL)";
    }

    public long addToCart(int productId, String productName, double price,
                          int quantity, String imageUrl) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be positive");
        ContentValues values = new ContentValues();
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("image_url", imageUrl);
        return getWritableDatabase().insertOrThrow(TABLE_CART, null, values);
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_CART, null, null, null,
                null, null, "id ASC")) {
            while (cursor.moveToNext()) {
                items.add(new CartItem(cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("product_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("product_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image_url"))));
            }
        }
        return items;
    }

    public void updateCartQuantity(int cartId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be positive");
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        getWritableDatabase().update(TABLE_CART, values, "id = ?",
                new String[]{String.valueOf(cartId)});
    }

    public void removeFromCart(int cartId) {
        getWritableDatabase().delete(TABLE_CART, "id = ?",
                new String[]{String.valueOf(cartId)});
    }

    public void clearCart() {
        getWritableDatabase().delete(TABLE_CART, null, null);
    }

    public long createOrder(Order order, List<CartItem> items) {
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Cart is empty");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put("username", order.getUsername());
            orderValues.put("address", order.getAddress());
            orderValues.put("latitude", order.getLatitude());
            orderValues.put("longitude", order.getLongitude());
            orderValues.put("total_amount", order.getTotalAmount());
            orderValues.put("status", order.getStatus());
            orderValues.put("order_date", order.getOrderDate());
            long orderId = db.insertOrThrow(TABLE_ORDERS, null, orderValues);
            for (CartItem item : items) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_name", item.getName());
                itemValues.put("price", item.getPrice());
                itemValues.put("quantity", item.getQuantity());
                db.insertOrThrow(TABLE_ORDER_ITEMS, null, itemValues);
            }
            db.setTransactionSuccessful();
            return orderId;
        } finally {
            db.endTransaction();
        }
    }

    public void updateOrderStatus(long orderId, String status) {
        ContentValues values = new ContentValues();
        values.put("status", status);
        getWritableDatabase().update(TABLE_ORDERS, values, "id = ?",
                new String[]{String.valueOf(orderId)});
    }

    public String getOrderStatus(long orderId) {
        try (Cursor cursor = getReadableDatabase().query(TABLE_ORDERS,
                new String[]{"status"}, "id = ?",
                new String[]{String.valueOf(orderId)}, null, null, null)) {
            return cursor.moveToFirst() ? cursor.getString(0) : null;
        }
    }

    public void saveFeedback(long orderId, String username, float rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1 to 5");
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("username", username);
        values.put("rating", rating);
        values.put("comment", comment);
        values.put("created_at", System.currentTimeMillis());
        getWritableDatabase().insertWithOnConflict(TABLE_FEEDBACK, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void syncCategories(List<Category> categories) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CATEGORIES, null, null);
        db.beginTransaction();
        try {
            for (Category category : categories) {
                ContentValues values = new ContentValues();
                values.put("id", category.getId());
                values.put("name", category.getName());
                values.put("icon_res", category.getIconRes());
                db.insertWithOnConflict(TABLE_CATEGORIES, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void syncProducts(List<Product> products) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PRODUCTS, null, null);
        db.beginTransaction();
        try {
            for (Product product : products) {
                ContentValues values = new ContentValues();
                values.put("id", product.getId());
                values.put("category_id", product.getCategoryId());
                values.put("name", product.getName());
                values.put("description", product.getDescription());
                values.put("price", product.getPrice());
                values.put("image_url", product.getImageUrl());
                values.put("stock", product.getStock());
                db.insertWithOnConflict(TABLE_PRODUCTS, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        String sql = "SELECT DISTINCT p.* FROM " + TABLE_PRODUCTS + " p " +
                "LEFT JOIN " + TABLE_CATEGORIES + " c ON p.category_id = c.id " +
                "WHERE p.name LIKE ? OR p.description LIKE ? OR c.name LIKE ?";
        String wildcard = "%" + query.trim() + "%";
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{wildcard, wildcard, wildcard})) {
            while (cursor.moveToNext()) {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
                products.add(product);
            }
        }
        return products;
    }

    public List<String> getAllSearchSuggestions() {
        List<String> suggestions = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(true, TABLE_CATEGORIES, new String[]{"name"},
                null, null, null, null, "name ASC", null)) {
            while (cursor.moveToNext()) {
                suggestions.add(cursor.getString(0));
            }
        }
        try (Cursor cursor = getReadableDatabase().query(true, TABLE_PRODUCTS, new String[]{"name"},
                null, null, null, null, "name ASC", null)) {
            while (cursor.moveToNext()) {
                suggestions.add(cursor.getString(0));
            }
        }
        return suggestions;
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return queryProducts("category_id = ?", new String[]{String.valueOf(categoryId)});
    }

    public Product getProductById(int productId) {
        List<Product> products = queryProducts("id = ?",
                new String[]{String.valueOf(productId)});
        return products.isEmpty() ? null : products.get(0);
    }

    public List<Product> getAllProducts() {
        return queryProducts(null, null);
    }

    private List<Product> queryProducts(String selection, String[] selectionArgs) {
        List<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE_PRODUCTS, null, selection,
                selectionArgs, null, null, null)) {
            while (cursor.moveToNext()) {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow("image_url")));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow("stock")));
                products.add(product);
            }
        }
        return products;
    }

    public List<Order> getOrdersByUsername(String username) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "username = ?";
        String[] selectionArgs = new String[]{username};

        Cursor cursor = db.query(TABLE_ORDERS, null, selection, selectionArgs, null, null, "id DESC");
        if (cursor != null && cursor.moveToFirst()){
            do {
                Order order = new Order();
                order.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                order.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
                order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                order.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")));
                order.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                order.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount")));
                order.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                order.setOrderDate(cursor.getLong(cursor.getColumnIndexOrThrow("order_date")));
                list.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }
}
