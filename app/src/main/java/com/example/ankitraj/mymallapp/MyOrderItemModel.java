package com.example.ankitraj.mymallapp;

/**
 * Created by AnkitRaj on 11-Jun-19.
 */

public class MyOrderItemModel {

    private int productImage;
    private String productTitle;
    private String deliveryStatus;
    private int rating ;

    public MyOrderItemModel(int productImage, int rating ,String productTitle, String deliveryStatus) {
        this.productImage = productImage;
        this.rating = rating;
        this.productTitle = productTitle;
        this.deliveryStatus = deliveryStatus;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getProductImage() {
        return productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
