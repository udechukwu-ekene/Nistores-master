package com.nistores.awesomeurch.nistores.folders.helpers;

public class Member {
    private String merchant_id;
    private String surname;
    private String location;
    private String picture;
    private String firstname;

    public String getMerchant_id() {
        return merchant_id;
    }

    public String getSurname() {
        return surname;
    }

    public String getLocation() {
        return location;
    }

    public String getPicture() {
        return picture;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
}
