package com.example.printxpress.model;

public class Order {
    private String orderId;
    private String userId;
    private String productName;
    private String date;
    private String status;
    private String price;
    private String quantity;
    private String material;
    private String printing;
    private String deliveryMethod;
    private String time;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private long timestamp;

    public Order() {
    }

    public Order(String orderId, String userId, String productName, String date, String status, String price,
                 String quantity, String material, String printing, String deliveryMethod, String time,
                 String customerName, String customerPhone, String customerEmail, String customerAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.productName = productName;
        this.date = date;
        this.status = status;
        this.price = price;
        this.quantity = quantity;
        this.material = material;
        this.printing = printing;
        this.deliveryMethod = deliveryMethod;
        this.time = time;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.customerAddress = customerAddress;
        this.timestamp = System.currentTimeMillis();
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getPrinting() { return printing; }
    public void setPrinting(String printing) { this.printing = printing; }

    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}







