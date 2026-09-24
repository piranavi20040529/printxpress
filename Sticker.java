package com.example.printxpress.model;

public class Sticker {
    private String id;
    private String title;
    private String shape;
    private String finish;
    private String material;
    private double width;
    private double height;
    private int quantity;
    private double price;
    private String imageUrl;
    private boolean isPopular;

    public Sticker() {
    }

    public Sticker(String id, String title, String shape, String finish,
                   String material, double width, double height,
                   int quantity, double price, String imageUrl) {
        this.id = id;
        this.title = title;
        this.shape = shape;
        this.finish = finish;
        this.material = material;
        this.width = width;
        this.height = height;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPopular = false;
    }


    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getShape() { return shape; }
    public String getFinish() { return finish; }
    public String getMaterial() { return material; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }


    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setShape(String shape) { this.shape = shape; }
    public void setFinish(String finish) { this.finish = finish; }
    public void setMaterial(String material) { this.material = material; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPopular(boolean popular) { isPopular = popular; }

    public double getArea() {
        return width * height;
    }
}

