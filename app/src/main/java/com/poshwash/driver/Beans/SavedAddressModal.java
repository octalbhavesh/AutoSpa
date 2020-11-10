package com.poshwash.driver.Beans;

/**
 * Created by anandj on 4/4/2017.
 */

public class SavedAddressModal {
    String address_id;
    String name;
    String address;
    String zip_code;
    String number;

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        if(zip_code.equalsIgnoreCase("null"))
                this.zip_code = "";
        else
            this.zip_code = zip_code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
