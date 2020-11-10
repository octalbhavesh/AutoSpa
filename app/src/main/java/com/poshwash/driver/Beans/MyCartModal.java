package com.poshwash.driver.Beans;

import java.io.Serializable;

/**
 * Created by anandj on 3/21/2017.
 */

public class MyCartModal implements Serializable {
    String id;
    String productname;
    String size;
    String color;
    int qty;
    String qty_confirrrmation;
    String selling_price;
    String mrp_price;
    String image;

    public MyCartModal(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }

    public String getMrp_price() {
        return mrp_price;
    }

    public void setMrp_price(String mrp_price) {
        this.mrp_price = mrp_price;
    }

    public String getQty_confirrrmation() {
        return qty_confirrrmation;
    }

    public void setQty_confirrrmation(String qty_confirrrmation) {
        this.qty_confirrrmation = qty_confirrrmation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
