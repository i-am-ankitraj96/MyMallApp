package com.example.ankitraj.mymallapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AnkitRaj on 11-Jun-19.
 */

public class CartItemModel {

    public static final int CART_ITEM = 0;
    public static final int TOTAL_AMOUNT = 1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    ///////// cart item

    private String productID;
    private String productImage;
    private String productTitle;
    private String productPrice;
    private Long productQty;
    private Long stockQuantity;

    private List<String> qtyIDs;
    private boolean qtyError ;

    public CartItemModel(int type,String productID , String productImage, String productTitle, String productPrice, Long productQty, Long stockQuantity) {
        this.type = type;
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productQty = productQty;
        this.stockQuantity = stockQuantity ;
        qtyIDs = new ArrayList<>();
        qtyError = false;
    }

    public boolean isQtyError() {
        return qtyError;
    }

    public void setQtyError(boolean qtyError) {
        this.qtyError = qtyError;
    }

    public List<String> getQtyIDs() {
        return qtyIDs;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setQtyIDs(List<String> qtyIDs) {
        this.qtyIDs = qtyIDs;
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

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public Long getProductQty() {
        return productQty;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductQty(Long productQty) {
        this.productQty = productQty;
    }

    ///////// cart item




    //////// cart total

    private int totalItems , totalItemsPrice , totalAmount , savedAmount ;
    private String deliveryPrice ;

    public CartItemModel(int type) {
        this.type = type;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemsPrice() {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice(int totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    ///////// cart total

}
