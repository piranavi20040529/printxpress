package com.example.printxpress.model;

public class Banner {
    private String id;
    private String title;
    private String material;
    private String size;
    private int quantity;
    private double price;
    private String oldPrice;
    private String lamination;
    private String finishing;
    private String imageUrl;
    private boolean isPopular;

    public Banner(String id, String title, String material, String size,
                  int quantity, double price, String oldPrice, String lamination, 
                  String finishing, String imageUrl, boolean isPopular) {
        this.id = id;
        this.title = title;
        this.material = material;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.oldPrice = oldPrice;
        this.lamination = lamination;
        this.finishing = finishing;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMaterial() { return material; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getLamination() { return lamination; }
    public String getFinishing() { return finishing; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
}