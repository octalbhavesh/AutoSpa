package com.poshwash.driver.Beans;

/**
 * Created by anandj on 3/22/2017.
 */

public class ProductsModal {
    String id;
    String name;
    String real_price;
    String sall_price;
    String image;
    String detail;
    String item_type;
    boolean wishlist_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReal_price() {
        return real_price;
    }

    public void setReal_price(String real_price) {
        this.real_price = real_price;
    }

    public String getSall_price() {
        return sall_price;
    }

    public void setSall_price(String sall_price) {
        this.sall_price = sall_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isWishlist_status() {
        return wishlist_status;
    }

    public void setWishlist_status(boolean wishlist_status) {
        this.wishlist_status = wishlist_status;
    }

    public String getItem_type() {
        return item_type;
    }

    public void setItem_type(String item_type) {
        this.item_type = item_type;
    }
}
