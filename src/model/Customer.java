package model;

import java.math.BigDecimal;
import java.sql.Date;


public class Customer {

    private String customerId;
    private String fullName;
    private String email;
    private String password;
    private BigDecimal balance;
    private Date regDate;



    public Customer() {}

    public Customer(String customerId, String fullName, String email,
                    String password, BigDecimal balance, Date regDate) {
        this.customerId = customerId;
        this.fullName   = fullName;
        this.email      = email;
        this.password   = password;
        this.balance    = balance;
        this.regDate    = regDate;
    }



    public String getCustomerId()             { return customerId; }
    public void setCustomerId(String id)      { this.customerId = id; }

    public String getFullName()               { return fullName; }
    public void setFullName(String name)      { this.fullName = name; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getPassword()               { return password; }
    public void setPassword(String password)  { this.password = password; }

    public BigDecimal getBalance()            { return balance; }
    public void setBalance(BigDecimal bal)    { this.balance = bal; }

    public Date getRegDate()                  { return regDate; }
    public void setRegDate(Date date)         { this.regDate = date; }

    @Override
    public String toString() {
        return fullName + " (" + customerId + ")";
    }
}