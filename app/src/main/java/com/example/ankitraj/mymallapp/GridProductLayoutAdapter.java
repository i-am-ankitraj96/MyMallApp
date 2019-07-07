package com.example.ankitraj.mymallapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

public class GridProductLayoutAdapter extends BaseAdapter {

    // create a List
    // each item has same layout as used in the horizontal_produc_scroll_layout

    // thus we don't create a model for Grid View and use the same model of horizontal scroll

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    List<HorizontalProductScrollModel> horizontalProductScrollModelList;


    // cuz always we want to show only 4 items

    @Override
    public int getCount() {
        return horizontalProductScrollModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;

        if(convertView==null){  // if convertView is null,  set the layout
            // define view

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            // as we are re-using layout created for horizontal slider , remove shadow effect and background
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent productDetailsIntent= new Intent(parent.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductScrollModelList.get(position).getProductId());
                    parent.getContext().startActivity(productDetailsIntent);
                }
            });

            // do the data binding here
            // as here no recyclerView

            // to set the view access all the elements under the layout first
            ImageView productImage = view.findViewById(R.id.h_s_product_image);
            TextView productTitle = view.findViewById(R.id.h_s_product_title);
            TextView productDescription = view.findViewById(R.id.h_s_product_description);
            TextView productPrice = view.findViewById(R.id.h_s_product_price);

            // set the data now using methods

            Glide.with(view.getContext()).load(horizontalProductScrollModelList.get(position).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);

            productTitle.setText(horizontalProductScrollModelList.get(position).getProductTitle());
            productDescription.setText(horizontalProductScrollModelList.get(position).getProductDescription());
            productPrice.setText("Rs. "+horizontalProductScrollModelList.get(position).getProductPrice()+" /-");

        }else{

            view = convertView;

        }

        return view;
    }
}
