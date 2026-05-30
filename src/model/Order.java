package model;

import java.math.BigDecimal;
import java.sql.Date;


public class Order {

    private String orderId;
    private Date orderDate;
    private String status;
    private BigDecimal totalPrice;
    private String customerId;
    private String promoCode;


    private String customerName;
    private String trackingNo;
    private String shipStatus;

    public Order() {}

    public Order(String orderId, Date orderDate, String status,
                 BigDecimal totalPrice, String customerId, String promoCode) {
        this.orderId    = orderId;
        this.orderDate  = orderDate;
        this.status     = status;
        this.totalPrice = totalPrice;
        this.customerId = customerId;
        this.promoCode  = promoCode;
    }

    public String getOrderId()                 { return orderId; }
    public void setOrderId(String id)          { this.orderId = id; }

    public Date getOrderDate()                 { return orderDate; }
    public void setOrderDate(Date d)           { this.orderDate = d; }

    public String getStatus()                  { return status; }
    public void setStatus(String s)            { this.status = s; }

    public BigDecimal getTotalPrice()          { return totalPrice; }
    public void setTotalPrice(BigDecimal t)    { this.totalPrice = t; }

    public String getCustomerId()              { return customerId; }
    public void setCustomerId(String id)       { this.customerId = id; }

    public String getPromoCode()               { return promoCode; }
    public void setPromoCode(String code)      { this.promoCode = code; }

    public String getCustomerName()            { return customerName; }
    public void setCustomerName(String n)      { this.customerName = n; }

    public String getTrackingNo()              { return trackingNo; }
    public void setTrackingNo(String t)        { this.trackingNo = t; }

    public String getShipStatus()              { return shipStatus; }
    public void setShipStatus(String s)        { this.shipStatus = s; }

    @Override
    public String toString() { return orderId + " - " + status; }
}