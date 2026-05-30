package model;
import java.sql.Date;
public class Shipment {
 
    private String trackingNo;
    private String courier;
    private Date shipDate;
    private String shipStatus;  
    private String delivAddress;
    private String orderId;
 

    private String customerName;
 
    public Shipment() {}
 
    public Shipment(String trackingNo, String courier, Date shipDate,
                    String shipStatus, String delivAddress, String orderId) {
        this.trackingNo   = trackingNo;
        this.courier      = courier;
        this.shipDate     = shipDate;
        this.shipStatus   = shipStatus;
        this.delivAddress = delivAddress;
        this.orderId      = orderId;
    }
 
    public String getTrackingNo()              { return trackingNo; }
    public void setTrackingNo(String t)        { this.trackingNo = t; }
 
    public String getCourier()                 { return courier; }
    public void setCourier(String c)           { this.courier = c; }
 
    public Date getShipDate()                  { return shipDate; }
    public void setShipDate(Date d)            { this.shipDate = d; }
 
    public String getShipStatus()              { return shipStatus; }
    public void setShipStatus(String s)        { this.shipStatus = s; }
 
    public String getDelivAddress()            { return delivAddress; }
    public void setDelivAddress(String a)      { this.delivAddress = a; }
 
    public String getOrderId()                 { return orderId; }
    public void setOrderId(String id)          { this.orderId = id; }
 
    public String getCustomerName()            { return customerName; }
    public void setCustomerName(String n)      { this.customerName = n; }
}
