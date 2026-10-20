package com.example.quickcommerceapplication.models;

public class Feedback {
    private final long orderId;
    private final String username;
    private final float rating;
    private final String comment;
    private final long createdAt;

    public Feedback(long orderId, String username, float rating, String comment, long createdAt) {
        this.orderId = orderId;
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public long getOrderId() { return orderId; }
    public String getUsername() { return username; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public long getCreatedAt() { return createdAt; }
}

