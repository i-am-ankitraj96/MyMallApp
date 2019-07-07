package com.example.ankitraj.mymallapp;

import java.util.List;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

// model class to implement multiple layout recyclerView

public class HomePageModel {

    // this variable must not be able to be changed from anywhere
    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_AD_BANNER = 1;
    public static final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static final int GRID_PRODUCT_VIEW = 3;

    // note HORIZONTAL_PRODUCT_VIEW & GRID_PRODUCT_VIEW both can utilize same constructors and methods and constructors as both have just differnt layout
    // just make sure to pass the type as REQUIRED,   i.e., 2 for prior one and 3 for later one


    private int type; // to be used for all
    private String backgroundColor;

    /////////   Banner Slider

    // this is where the entire Banners accessed via database is kept
    private List<SliderModel> sliderModelList;

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }


    public int getType() {
        return type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }



    /////////   Banner Slider




    /////////  Strip Ad

    // variables for image-resource and String text
    private String resource;

    // constructor
    public HomePageModel(int type, String resource, String backgroundColor) {
        this.type = type;
        this.resource = resource;
        this.backgroundColor = backgroundColor;
    }

    // getter methods
    public String getResource() {
        return resource;
    }
    public String getBackgroundColor() {
        return backgroundColor;
    }

    // setter methods
    public void setResource(String resource) {
        this.resource = resource;
    }
    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    /////////  Strip Ad




    ///////// horizontal product layout and Grid Layout

    private String title;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<WishListModel> viewAllProductList;

    // constructor


    public HomePageModel(int type, String backgroundColor , String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList,List<WishListModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }


    public HomePageModel(int type, String backgroundColor , String title, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    // getter and setter methods


    public List<WishListModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishListModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    public String getTitle() {
        return title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    ///////// horizontal product layout and Grid Layout






    /////////
}
