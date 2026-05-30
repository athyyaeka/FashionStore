package model;

import java.math.BigDecimal;


public class OrderItem {

    private String orderId;
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;


    private String productName;
    private String storeName;

    public OrderItem() {}

    public OrderItem(String orderId, String productId,
                     int quantity, BigDecimal unitPrice) {
        this.orderId   = orderId;
        this.productId = productId;
        this.quantity  = quantity;
        this.unitPrice = unitPrice;
    }

    public String getOrderId()               { return orderId; }
    public void setOrderId(String id)        { this.orderId = id; }

    public String getProductId()             { return productId; }
    public void setProductId(String id)      { this.productId = id; }

    public int getQuantity()                 { return quantity; }
    public void setQuantity(int q)           { this.quantity = q; }

    public BigDecimal getUnitPrice()         { return unitPrice; }
    public void setUnitPrice(BigDecimal p)   { this.unitPrice = p; }

    public String getProductName()           { return productName; }
    public void setProductName(String n)     { this.productName = n; }

    public String getStoreName()             { return storeName; }
    public void setStoreName(String n)       { this.storeName = n; }

    public BigDecimal getSubtotal() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}