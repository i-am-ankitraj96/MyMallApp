package com.example.ankitraj.mymallapp;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

public class HorizontalProductScrollModel {

    // create variables for
    /*
    setting four variables.......
    one image view
    and three text views
     */


    private String productId;
    private String productImage;
    private String productTitle, productDescription, productPrice;


    public HorizontalProductScrollModel(String productId,String productImage, String productTitle, String productDescription, String productPrice) {
        this.productId = productId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }


    public String getProductImage() {
        return productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }
}
