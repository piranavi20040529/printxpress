package com.example.printxpress.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private int imageResource;
    private String icon;
    private String category;
    private String priceString;
    private String oldPriceString;
    private double price;
    private boolean isPopular;

    public Product() {}

    public Product(String id, String name, String description, String priceString, String category, int imageResource) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceString = priceString;
        this.category = category;
        this.imageResource = imageResource;
        this.isPopular = false;
    }

    public Product(String id, String name, String description, String priceString, String oldPriceString, String category, int imageResource) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceString = priceString;
        this.oldPriceString = oldPriceString;
        this.category = category;
        this.imageResource = imageResource;
        this.isPopular = false;
    }

    public Product(String id, String name, String description, String priceString, String category, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceString = priceString;
        this.category = category;
        this.icon = icon;
        this.isPopular = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getImageResource() { return imageResource; }
    public void setImageResource(int imageResource) { this.imageResource = imageResource; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriceString() { return priceString; }
    public void setPriceString(String priceString) { this.priceString = priceString; }

    public String getOldPriceString() { return oldPriceString; }
    public void setOldPriceString(String oldPriceString) { this.oldPriceString = oldPriceString; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isPopular() { return isPopular; }
    public void setPopular(boolean popular) { isPopular = popular; }
}
