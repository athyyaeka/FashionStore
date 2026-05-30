package model;

import java.math.BigDecimal;


public class Product {

    private String productId;
    private String pName;
    private BigDecimal price;
    private int stock;
    private int weightGram;
    private String description;
    private String categoryId;
    private String sellerId;


    private String categoryName;
    private String storeName;


    private String jenisProduk;

    public Product() {}

    public Product(String productId, String pName, BigDecimal price,
                   int stock, int weightGram, String description,
                   String categoryId, String sellerId) {
        this.productId   = productId;
        this.pName       = pName;
        this.price       = price;
        this.stock       = stock;
        this.weightGram  = weightGram;
        this.description = description;
        this.categoryId  = categoryId;
        this.sellerId    = sellerId;
    }

    public String getProductId()               { return productId; }
    public void setProductId(String id)        { this.productId = id; }

    public String getPName()                   { return pName; }
    public void setPName(String name)          { this.pName = name; }

    public BigDecimal getPrice()               { return price; }
    public void setPrice(BigDecimal p)         { this.price = p; }

    public int getStock()                      { return stock; }
    public void setStock(int s)                { this.stock = s; }

    public int getWeightGram()                 { return weightGram; }
    public void setWeightGram(int w)           { this.weightGram = w; }

    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }

    public String getCategoryId()              { return categoryId; }
    public void setCategoryId(String id)       { this.categoryId = id; }

    public String getSellerId()                { return sellerId; }
    public void setSellerId(String id)         { this.sellerId = id; }

    public String getCategoryName()            { return categoryName; }
    public void setCategoryName(String n)      { this.categoryName = n; }

    public String getStoreName()               { return storeName; }
    public void setStoreName(String n)         { this.storeName = n; }

    public String getJenisProduk()             { return jenisProduk; }
    public void setJenisProduk(String j)       { this.jenisProduk = j; }

    @Override
    public String toString() { return pName + " (" + productId + ")"; }
}