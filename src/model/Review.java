package model;
import java.sql.Date;
public class Review {
 
    private String reviewId;
    private int rating;           
    private String comment;
    private Date reviewDate;
    private String customerId;
    private String productId;
 

    private String customerName;
    private String productName;
 
    public Review() {}
 
    public Review(String reviewId, int rating, String comment,
                  Date reviewDate, String customerId, String productId) {
        this.reviewId   = reviewId;
        this.rating     = rating;
        this.comment    = comment;
        this.reviewDate = reviewDate;
        this.customerId = customerId;
        this.productId  = productId;
    }
 
    public String getReviewId()               { return reviewId; }
    public void setReviewId(String id)        { this.reviewId = id; }
 
    public int getRating()                    { return rating; }
    public void setRating(int r)              { this.rating = r; }
 
    public String getComment()                { return comment; }
    public void setComment(String c)          { this.comment = c; }
 
    public Date getReviewDate()               { return reviewDate; }
    public void setReviewDate(Date d)         { this.reviewDate = d; }
 
    public String getCustomerId()             { return customerId; }
    public void setCustomerId(String id)      { this.customerId = id; }
 
    public String getProductId()              { return productId; }
    public void setProductId(String id)       { this.productId = id; }
 
    public String getCustomerName()           { return customerName; }
    public void setCustomerName(String n)     { this.customerName = n; }
 
    public String getProductName()            { return productName; }
    public void setProductName(String n)      { this.productName = n; }
}
