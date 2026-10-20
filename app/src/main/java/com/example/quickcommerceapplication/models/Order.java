package com.example.quickcommerceapplication.models;

public class Order {
    private long id;
    private String username;
    private String address;
    private double latitude;
    private double longitude;
    private double totalAmount;
    private String status;
    private long orderDate;

    public Order(){

    }

    public Order(String username, String address, double latitude, double longitude,
                 double totalAmount, String status, long orderDate) {
        this.username = username;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDate = orderDate;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public long getOrderDate() { return orderDate; }
    public void setId(long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setTotalAmount(double totalAmount) { this.totalAmount=totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
}

