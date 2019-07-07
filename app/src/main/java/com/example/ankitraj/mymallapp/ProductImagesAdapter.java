package com.example.ankitraj.mymallapp;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by AnkitRaj on 09-Jun-19.
 */

public class ProductImagesAdapter extends PagerAdapter {

    // make a list type variable
    // list is integet typr as well pass resources
    private List<String> productImages;
    // make  constructor


    public ProductImagesAdapter(List<String> productImages) {
        this.productImages = productImages;
    }

    // implement methods
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // in the viewPager we only need to show images
        // thus we don't create separate resource layout file for image
        // thus we create imageView in thie code here and set that
        ImageView productImage=new ImageView(container.getContext());
        // set resources on this productImage
        // set images from the list "productImages"
        Glide.with(container.getContext()).load(productImages.get(position)).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);
        //add this imageView to the container
        container.addView(productImage,0);
        // return this productImage view
        return productImage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // here remove view from the container
        container.removeView((ImageView)object);
    }

    @Override
    public int getCount() {
        // return the size of the container
        return productImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object ;
    }
}
