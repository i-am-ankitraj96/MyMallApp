package com.example.ankitraj.mymallapp;

/**
 * Created by AnkitRaj on 16-Jun-19.
 */

public class AddressesModel {

    private String fullName;
    private String mobileNo;
    private String pinCode;
    private String fuladdress;
    private Boolean selected;

    public AddressesModel(String fullName, String fuladdress , String pinCode,Boolean selected,String mobileNo) {
        this.fullName = fullName;
        this.pinCode = pinCode;
        this.fuladdress = fuladdress;
        this.selected = selected;
        this.mobileNo = mobileNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getFuladdress() {
        return fuladdress;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setFuladdress(String fuladdress) {
        this.fuladdress = fuladdress;
    }
}
