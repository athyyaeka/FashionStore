package model;

import java.math.BigDecimal;


public class Seller {

    private String sellerId;
    private String username;
    private String password;
    private String email;
    private String storeName;
    private BigDecimal balance;

    public Seller() {}

    public Seller(String sellerId, String username, String password,
                  String email, String storeName, BigDecimal balance) {
        this.sellerId  = sellerId;
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.storeName = storeName;
        this.balance   = balance;
    }

    public String getSellerId()              { return sellerId; }
    public void setSellerId(String id)       { this.sellerId = id; }

    public String getUsername()              { return username; }
    public void setUsername(String u)        { this.username = u; }

    public String getPassword()              { return password; }
    public void setPassword(String p)        { this.password = p; }

    public String getEmail()                 { return email; }
    public void setEmail(String e)           { this.email = e; }

    public String getStoreName()             { return storeName; }
    public void setStoreName(String name)    { this.storeName = name; }

    public BigDecimal getBalance()           { return balance; }
    public void setBalance(BigDecimal bal)   { this.balance = bal; }

    @Override
    public String toString() {
        return storeName + " (" + sellerId + ")";
    }
}