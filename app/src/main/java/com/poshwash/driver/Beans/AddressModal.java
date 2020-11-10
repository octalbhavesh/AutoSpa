package com.poshwash.driver.Beans;

/**
 * Created by anandj on 2/8/2018.
 */

public class AddressModal {
    private int count;
    private String address;

    public AddressModal(int count, String address) {
        this.count = count;
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
