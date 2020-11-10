package com.poshwash.driver.Beans;

/**
 * Created by anandj on 5/31/2017.
 */

public class ManageMembershipModal {
    String id;
    String booking_id;
    String booking_type;
    String membership_type;
    String number_of_washes_monthly;
    String created;
    String week_days;
    String preferred_time;
    String preferred_dates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getBooking_type() {
        return booking_type;
    }

    public void setBooking_type(String booking_type) {
        this.booking_type = booking_type;
    }

    public String getMembership_type() {
        return membership_type;
    }

    public void setMembership_type(String membership_type) {
        this.membership_type = membership_type;
    }

    public String getNumber_of_washes_monthly() {
        return number_of_washes_monthly;
    }

    public void setNumber_of_washes_monthly(String number_of_washes_monthly) {
        this.number_of_washes_monthly = number_of_washes_monthly;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getWeek_days() {
        return week_days;
    }

    public void setWeek_days(String week_days) {
        this.week_days = week_days;
    }

    public String getPreferred_time() {
        return preferred_time;
    }

    public void setPreferred_time(String preferred_time) {
        this.preferred_time = preferred_time;
    }

    public String getPreferred_dates() {
        return preferred_dates;
    }

    public void setPreferred_dates(String preferred_dates) {
        this.preferred_dates = preferred_dates;
    }
}
