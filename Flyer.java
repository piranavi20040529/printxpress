package com.example.printxpress.model;

public class Flyer {
    private String id;
    private String title;
    private String size;
    private String paperQuality;
    private int quantity;
    private double price;
    private String oldPrice;
    private String orientation;
    private String printingType;
    private String imageUrl;
    private boolean isPopular;

    public Flyer(String id, String title, String size, String paperQuality,
                 int quantity, double price, String oldPrice, String orientation,
                 String printingType, String imageUrl, boolean isPopular) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.paperQuality = paperQuality;
        this.quantity = quantity;
        this.price = price;
        this.oldPrice = oldPrice;
        this.orientation = orientation;
        this.printingType = printingType;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSize() { return size; }
    public String getPaperQuality() { return paperQuality; }
    public String getPaperType() { return paperQuality; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getOrientation() { return orientation; }
    public String getPrintingType() { return printingType; }
    public String getPrintingSide() { return printingType; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
}
