package com.example.quickcommerceapplication.utils;

public final class Constants {
    private Constants() {}

    public static final String PREF_NAME = "QuickCommercePrefs";
    public static final String KEY_LOGIN = "is_logged_in";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_ORDER_STATUS = "extra_order_status";
    public static final String EXTRA_IS_CONNECTED = "extra_is_connected";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String ACTION_ORDER_STATUS_UPDATE =
            "com.example.quickcommerceapplication.ORDER_STATUS_UPDATE";
    public static final String ACTION_NETWORK_STATUS =
            "com.example.quickcommerceapplication.NETWORK_STATUS";
    public static final int LOCATION_REQ_CODE = 100;
    public static final int NOTIFICATION_REQ_CODE = 101;
    public static final String API_PRODUCTS_URL = "https://fakestoreapi.com/products";
    public static final String API_CATEGORIES_URL = "https://fakestoreapi.com/products/categories";
}
