package com.example.printxpress.model;

public class Tshirt {
    private String id;
    private String title;
    private String productType;
    private String color;
    private String size;
    private String printLocation;
    private String printType;
    private String material;
    private int quantity;
    private double price;
    private String imageUrl;
    private boolean isPopular;

    public Tshirt() {
    }

    public Tshirt(String id, String title, String productType, String color,
                  String size, String printLocation, String printType, String material,
                  int quantity, double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.productType = productType;
        this.color = color;
        this.size = size;
        this.printLocation = printLocation;
        this.printType = printType;
        this.material = material;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPopular = false;
    }


    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getProductType() { return productType; }
    public String getColor() { return color; }
    public String getSize() { return size; }
    public String getPrintLocation() { return printLocation; }
    public String getPrintType() { return printType; }
    public String getMaterial() { return material; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }


    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setProductType(String productType) { this.productType = productType; }
    public void setColor(String color) { this.color = color; }
    public void setSize(String size) { this.size = size; }
    public void setPrintLocation(String printLocation) { this.printLocation = printLocation; }
    public void setPrintType(String printType) { this.printType = printType; }
    public void setMaterial(String material) { this.material = material; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPopular(boolean popular) { isPopular = popular; }
}
