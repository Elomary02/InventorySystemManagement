package com.example.gestioninventairemagasin;

import javafx.beans.property.*;

public class Products {

    private final IntegerProperty id;
    private final StringProperty productName;
    private final IntegerProperty quantity;
    private final DoubleProperty price;

    public Products(int id, String productName, int quantity, double price) {
        this.id = new SimpleIntegerProperty(this, "id", id);
        this.productName = new SimpleStringProperty(this, "productName", productName);
        this.quantity = new SimpleIntegerProperty(this, "quantity", quantity);
        this.price = new SimpleDoubleProperty(this, "price", price);
    }


    public IntegerProperty idProp() {
        return id;
    }
    public int getProdId() {
        return id.get();
    }
    public void setProdId(int newProdId) {
        id.set(newProdId);
    }

    public StringProperty productNameProp() {
        return productName;
    }
    public String getProductName() {
        return productName.get();
    }
    public void setProductName(String newProductName) {
        productName.set(newProductName);
    }

    public IntegerProperty quantityProp() {
        return quantity;
    }
    public int getQuantity() {
        return quantity.get();
    }
    public void setQuantity(int newQuantity) {
        quantity.set(newQuantity);
    }

    public DoubleProperty priceProp() {
        return price;
    }
    public double getPrice() {
        return price.get();
    }
    public void setPrice(double newPrice) {
        price.set(newPrice);
    }
}