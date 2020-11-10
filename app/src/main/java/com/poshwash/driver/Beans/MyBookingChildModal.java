package com.poshwash.driver.Beans;

import org.json.JSONArray;

/**
 * Created by anandj on 3/20/2017.
 */

public class MyBookingChildModal {

    String id = "";
    String image = "";
    String car_name = "";
    String more_cars = "";
    String owner_name = "";
    String rating_count = "";
    String price = "";
    String booking_lat = "";
    String start_wash_time = "";
    String booking_long;
    String user_lat;
    String user_lon;
    String user_phone;
    String profile_image_url;
    String booking_transaction;
    String booking_date;
    String booking_time;
    String service_type;
    String transaction_number;
    String duration;
    String status;
    String booking_sp_id;
    String sp_id;
    String booking_type;
    String vehicle_type_id = "";
    boolean is_rating;

    public boolean getIs_rating() {
        return is_rating;
    }

    public void setIs_rating(boolean is_rating) {
        this.is_rating = is_rating;
    }

    public String getVehicle_type_id() {
        return vehicle_type_id;
    }

    public void setVehicle_type_id(String vehicle_type_id) {
        this.vehicle_type_id = vehicle_type_id;
    }

    public String getStart_wash_time() {
        return start_wash_time;
    }

    public void setStart_wash_time(String start_wash_time) {
        this.start_wash_time = start_wash_time;
    }

    public String getBooking_sp_id() {
        return booking_sp_id;
    }

    public void setBooking_sp_id(String booking_sp_id) {
        this.booking_sp_id = booking_sp_id;
    }

    public String getBooking_lat() {
        return booking_lat;
    }

    public void setBooking_lat(String booking_lat) {
        this.booking_lat = booking_lat;
    }

    public String getBooking_long() {
        return booking_long;
    }

    public void setBooking_long(String booking_long) {
        this.booking_long = booking_long;
    }

    public String getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(String user_lat) {
        this.user_lat = user_lat;
    }

    public String getUser_lon() {
        return user_lon;
    }

    public void setUser_lon(String user_lon) {
        this.user_lon = user_lon;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getBooking_transaction() {
        return booking_transaction;
    }

    public void setBooking_transaction(String booking_transaction) {
        this.booking_transaction = booking_transaction;
    }


    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }


    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getTransaction_number() {
        return transaction_number;
    }

    public void setTransaction_number(String transaction_number) {
        this.transaction_number = transaction_number;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBooking_Sp_id() {
        return booking_sp_id;
    }

    public void setBooking_Sp_id(String booking_sp_id) {
        this.booking_sp_id = booking_sp_id;
    }

    public String getSp_id() {
        return sp_id;
    }

    public void setSp_id(String sp_id) {
        this.sp_id = sp_id;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public String getRating_count() {
        return rating_count;
    }

    public void setRating_count(String rating_count) {
        this.rating_count = rating_count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getMore_cars() {
        return more_cars;
    }

    public void setMore_cars(String more_cars) {
        this.more_cars = more_cars;
    }

    public JSONArray getCar_array() {
        return car_array;
    }

    public void setCar_array(JSONArray car_array) {
        this.car_array = car_array;
    }

    public JSONArray car_array = new JSONArray();
}
