package com.example.printxpress.model;

public class BusinessCard {
    private String id;
    private String title;
    private String size;
    private String paperQuality;
    private String side;
    private String quantity;
    private String price;
    private String oldPrice;
    private String imageUrl;
    private boolean isPopular;

    public BusinessCard(String id, String title, String size, String paperQuality, String side, String quantity, String price, String oldPrice, String imageUrl, boolean isPopular) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.paperQuality = paperQuality;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.oldPrice = oldPrice;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSize() { return size; }
    public String getPaperQuality() { return paperQuality; }
    public String getSide() { return side; }
    public String getQuantity() { return quantity; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }

    public String getDescription() {
        return paperQuality + " | " + size;
    }
}
