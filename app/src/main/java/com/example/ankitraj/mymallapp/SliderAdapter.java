package com.example.ankitraj.mymallapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

public class SliderAdapter extends PagerAdapter {

    private List<SliderModel> sliderModelList;

    public SliderAdapter(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }


    @Override
    public int getCount() {

        return sliderModelList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view= LayoutInflater.from(container.getContext()).inflate(R.layout.slider_layout,container,false);
        // access the root Constraint Layout via view

        ConstraintLayout bannerContainer = view.findViewById(R.id.banner_container);
        // set Background colour of the container to that of the banner being displayed
        bannerContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(String.valueOf(sliderModelList.get(position).getBackgroundColor()))));
        ImageView banner=view.findViewById(R.id.banner_slide);
        Glide.with(container.getContext()).load(sliderModelList.get(position).getBanner()).
                apply(new RequestOptions().placeholder(R.mipmap.welcome_img)).into(banner);
        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
