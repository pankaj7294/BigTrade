package com.example.pankajbharati.assignment;



public class Product {

    private String image;
    private String id;
    private String mrp;
    private String selling_price;
    private String name;
    private String quantity;
    private String description;

    public  Product(){}

    public Product(String image, String id, String mrp, String selling_price, String name, String quantity, String description) {
        this.image = image;
        this.id = id;
        this.mrp = mrp;
        this.selling_price = selling_price;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String  mrp) {
        this.mrp = mrp;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
