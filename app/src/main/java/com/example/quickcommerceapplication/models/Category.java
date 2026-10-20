package com.example.quickcommerceapplication.models;

public class Category {
    private int id;
    private String name;
    private int iconRes;
    public Category(int id, String name, int iconRes) {
        this.id = id;
        this.name = name;
        this.iconRes = iconRes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getIconRes() { return iconRes; }
    public void setIconRes(int iconRes) { this.iconRes = iconRes; }
}

