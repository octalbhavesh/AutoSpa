package com.poshwash.driver.Beans;

/**
 * Created by anandj on 3/20/2017.
 */

public class NotificationModal {

    String id;
    String pic;
    String time;
    String text;
    String is_read;
    String booking_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    public String getBooking_driver_id() {
        return Booking_driver_id;
    }

    public void setBooking_driver_id(String booking_driver_id) {
        Booking_driver_id = booking_driver_id;
    }

    String Booking_driver_id;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    String userName;


    public String is_read() {
        return is_read;
    }

    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIs_read() {
        return is_read;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }
}
