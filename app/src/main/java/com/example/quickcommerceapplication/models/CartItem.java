package com.example.quickcommerceapplication.models;

public class CartItem {
    private int id;
    private int productId;
    private int quantity;
    private String name;
    private String imageUrl;
    private double price;

    public CartItem(int id, int productId, String name, double price, int quantity, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public double getPrice() { return price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

