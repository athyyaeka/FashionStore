package model;
 
import java.math.BigDecimal;
import java.sql.Date;
public class Promo {
 
    private String promoCode;
    private BigDecimal discountPct;
    private BigDecimal minPurchase;
    private Date validFrom;
    private Date validUntil;
    private boolean isActive;
 
    public Promo() {}
 
    public Promo(String promoCode, BigDecimal discountPct, BigDecimal minPurchase,
                 Date validFrom, Date validUntil, boolean isActive) {
        this.promoCode   = promoCode;
        this.discountPct = discountPct;
        this.minPurchase = minPurchase;
        this.validFrom   = validFrom;
        this.validUntil  = validUntil;
        this.isActive    = isActive;
    }
 
    public String getPromoCode()              { return promoCode; }
    public void setPromoCode(String c)        { this.promoCode = c; }
 
    public BigDecimal getDiscountPct()        { return discountPct; }
    public void setDiscountPct(BigDecimal d)  { this.discountPct = d; }
 
    public BigDecimal getMinPurchase()        { return minPurchase; }
    public void setMinPurchase(BigDecimal m)  { this.minPurchase = m; }
 
    public Date getValidFrom()                { return validFrom; }
    public void setValidFrom(Date d)          { this.validFrom = d; }
 
    public Date getValidUntil()               { return validUntil; }
    public void setValidUntil(Date d)         { this.validUntil = d; }
 
    public boolean isActive()                 { return isActive; }
    public void setActive(boolean a)          { this.isActive = a; }
 
    @Override
    public String toString() { return promoCode + " (" + discountPct + "%)"; }
}
