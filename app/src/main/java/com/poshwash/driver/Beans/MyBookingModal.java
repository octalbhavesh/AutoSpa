package com.poshwash.driver.Beans;

import java.util.List;

/**
 * Created by anandj on 3/20/2017.
 */

public class MyBookingModal {
    String id;
    String heading_name;
    List<MyBookingChildModal> myBookingChildModals;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeading_name() {
        return heading_name;
    }

    public void setHeading_name(String heading_name) {
        this.heading_name = heading_name;
    }

    public List<MyBookingChildModal> getMyBookingChildModals() {
        return myBookingChildModals;
    }

    public void setMyBookingChildModals(List<MyBookingChildModal> myBookingChildModals) {
        this.myBookingChildModals = myBookingChildModals;
    }
}
