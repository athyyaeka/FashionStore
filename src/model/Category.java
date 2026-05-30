package model;


public class Category {

    private String categoryId;
    private String cName;
    private String description;

    public Category() {}

    public Category(String categoryId, String cName, String description) {
        this.categoryId  = categoryId;
        this.cName       = cName;
        this.description = description;
    }

    public String getCategoryId()              { return categoryId; }
    public void setCategoryId(String id)       { this.categoryId = id; }

    public String getCName()                   { return cName; }
    public void setCName(String name)          { this.cName = name; }

    public String getDescription()             { return description; }
    public void setDescription(String desc)    { this.description = desc; }

    @Override
    public String toString() { return cName; }
}