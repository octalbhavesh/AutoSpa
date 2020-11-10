package com.poshwash.driver.Beans;

/**
 * Created by abhinava on 6/5/2017.
 */

public class BeanReviewData
{
    public String vehicle_name;
    public String vehicle_id;
    public String vehicle_amount;
    public String request_type_amount;
    public String request_type;

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_amount() {
        return vehicle_amount;
    }

    public void setVehicle_amount(String vehicle_amount) {
        this.vehicle_amount = vehicle_amount;
    }

    public String getRequest_type_amount() {
        return request_type_amount;
    }

    public void setRequest_type_amount(String request_type_amount) {
        this.request_type_amount = request_type_amount;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
