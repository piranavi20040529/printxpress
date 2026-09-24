package com.example.printxpress.model;

public class Mug {
    private String id;
    private String title;
    private String mugType;
    private String size;
    private String color;
    private String printArea;
    private int quantity;
    private double price;
    private String imageUrl;
    private boolean isPopular;

    public Mug() {
    }

    public Mug(String id, String title, String mugType, String size,
               String color, String printArea, int quantity,
               double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.mugType = mugType;
        this.size = size;
        this.color = color;
        this.printArea = printArea;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPopular = false;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMugType() { return mugType; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getPrintArea() { return printArea; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setMugType(String mugType) { this.mugType = mugType; }
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; }
    public void setPrintArea(String printArea) { this.printArea = printArea; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPopular(boolean popular) { isPopular = popular; }
}


