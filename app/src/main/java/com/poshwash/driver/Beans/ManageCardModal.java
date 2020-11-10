package com.poshwash.driver.Beans;

/**
 * Created by anandj on 5/30/2017.
 */

public class ManageCardModal {
    private String user_id;
    private String card_holder_name;
    private String id ;
    private String card_id;
    private String card_number;
    private String card_type;
    private String expire_month;
    private String expire_year;
    private String external_customer_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getExpire_month() {
        return expire_month;
    }

    public void setExpire_month(String expire_month) {
        this.expire_month = expire_month;
    }

    public String getExpire_year() {
        return expire_year;
    }

    public void setExpire_year(String expire_year) {
        this.expire_year = expire_year;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_holder_name() {
        return card_holder_name;
    }

    public void setCard_holder_name(String card_holder_name) {
        this.card_holder_name = card_holder_name;
    }

    public String getExternal_customer_id() {
        return external_customer_id;
    }

    public void setExternal_customer_id(String external_customer_id) {
        this.external_customer_id = external_customer_id;
    }
}
