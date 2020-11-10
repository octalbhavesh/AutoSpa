package com.poshwash.driver.Beans;

/**
 * Created by anandj on 3/24/2017.
 */

public class VechicleListModal {
    String id;
    String Vehicle_type;
    String vin;
    String make;
    String model;
    String plate_number;
    String color;
    String vehicle_location;
    String lat;
    String lng;
    String parking_floor;
    String photo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicle_type() {
        return Vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        Vehicle_type = vehicle_type;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicle_location() {
        return vehicle_location;
    }

    public void setVehicle_location(String vehicle_location) {
        this.vehicle_location = vehicle_location;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getParking_floor() {
        return parking_floor;
    }

    public void setParking_floor(String parking_floor) {
        this.parking_floor = parking_floor;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
