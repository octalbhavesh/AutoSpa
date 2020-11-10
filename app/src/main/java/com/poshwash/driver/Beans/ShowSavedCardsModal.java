package com.poshwash.driver.Beans;

/**
 * Created by anandj on 4/5/2017.
 */

public class ShowSavedCardsModal {
    String id;
    String name;
    String card_number;
    String card_type;
    String expire_month;
    String expire_year;
    String cvv_no;
    String card_id;
    String external_customer_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCvv_no() {
        return cvv_no;
    }

    public void setCvv_no(String cvv_no) {
        this.cvv_no = cvv_no;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getExternal_customer_id() {
        return external_customer_id;
    }

    public void setExternal_customer_id(String external_customer_id) {
        this.external_customer_id = external_customer_id;
    }
}
