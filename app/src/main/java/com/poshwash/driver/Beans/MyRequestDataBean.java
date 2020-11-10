package com.poshwash.driver.Beans;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by abhinava on 4/11/2017.
 */

public class MyRequestDataBean
{
    public ArrayList<BeanCars> getSelected_cars() {
        return selected_cars;
    }

    public void setSelected_cars(ArrayList<BeanCars> selected_cars) {
        this.selected_cars = selected_cars;
    }

    public ArrayList<BeanCars> selected_cars=new ArrayList<>();
    public ArrayList<String> week_days=new ArrayList<>();
    public ArrayList<String> select_car_ids=new ArrayList<>();
    public String service_type="";
    public String request_type="1";
    public String number_of_monthly_washes="1";
    public String request_time="";

    public JSONArray getFare_review() {
        return fare_review;
    }

    public void setFare_review(JSONArray fare_review) {
        this.fare_review = fare_review;
    }

    public JSONArray fare_review=new JSONArray();

    public JSONArray getDates_array() {
        return dates_array;
    }

    public void setDates_array(JSONArray dates_array) {
        this.dates_array = dates_array;
    }

    public JSONArray dates_array=new JSONArray();

    public String getRequest_end_time() {
        return request_end_time;
    }

    public void setRequest_end_time(String request_end_time) {
        this.request_end_time = request_end_time;
    }

    public String request_end_time="";
    public String membership_type="1";
    public String payment_card_id="";
    public String payment_card_type="";

    public String getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(String request_amount) {
        this.request_amount = request_amount;
    }

    public String request_amount="0.00";

    public String getRequest_address() {
        return request_address;
    }

    public void setRequest_address(String request_address) {
        this.request_address = request_address;
    }

    public String getRequest_lat() {
        return request_lat;
    }

    public void setRequest_lat(String request_lat) {
        this.request_lat = request_lat;
    }

    public String getRequest_long() {
        return request_long;
    }

    public void setRequest_long(String request_long) {
        this.request_long = request_long;
    }

    public String request_address="";
    public String request_lat="";
    public String request_long="";

    public String getPayment_card_number() {
        return payment_card_number;
    }

    public void setPayment_card_number(String payment_card_number) {
        this.payment_card_number = payment_card_number;
    }

    public String payment_card_number="";

    public String getPayment_card_type() {
        return payment_card_type;
    }

    public void setPayment_card_type(String payment_card_type) {
        this.payment_card_type = payment_card_type;
    }



    public ArrayList<String> getSelect_car_ids() {
        return select_car_ids;
    }

    public void setSelect_car_ids(ArrayList<String> select_car_ids) {
        this.select_car_ids = select_car_ids;
    }

    public ArrayList<String> getWeek_days()
    {
        return week_days;
    }

    public void setWeek_days(ArrayList<String> week_days) {
        this.week_days = week_days;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getNumber_of_monthly_washes() {
        return number_of_monthly_washes;
    }

    public void setNumber_of_monthly_washes(String number_of_monthly_washes) {
        this.number_of_monthly_washes = number_of_monthly_washes;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public void setMembership_type(String membership_type) {
        this.membership_type = membership_type;
    }

    public String getPayment_card_id() {
        return payment_card_id;
    }

    public void setPayment_card_id(String payment_card_id) {
        this.payment_card_id = payment_card_id;
    }
}
