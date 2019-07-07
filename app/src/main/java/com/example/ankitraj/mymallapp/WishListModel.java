package com.example.ankitraj.mymallapp;

/**
 * Created by AnkitRaj on 12-Jun-19.
 */

public class WishListModel {

    private String productID;
    private String productImage;
    private long totalRatings;
    private String productTitle,rating;
    private String productPrice , cutPrice ;
    private boolean COD;

    public WishListModel(String productID,String productImage, long totalRatings, String productTitle, String rating, String productPrice, String cutPrice, boolean COD) {
        this.productID = productID;
        this.productImage = productImage;
        this.totalRatings = totalRatings;
        this.productTitle = productTitle;
        this.rating = rating;
        this.productPrice = productPrice;
        this.cutPrice = cutPrice;
        this.COD = COD;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getRating() {
        return rating;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public boolean isCOD() {
        return COD;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }
}
