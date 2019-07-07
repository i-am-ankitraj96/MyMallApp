package com.example.ankitraj.mymallapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static java.lang.Math.min;

/**
 * Created by AnkitRaj on 08-Jun-19.
 */

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder>{


    //create a list and customize it


    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;


    //constructor
    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);

        return new ViewHolder(view);
    }


    // bind data here in this method
    @Override
    public void onBindViewHolder(HorizontalProductScrollAdapter.ViewHolder holder, int position) {

        // extract data from the list

        String resource = horizontalProductScrollModelList.get(position).getProductImage();
        String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();

        // set the extracted data via viewHolder

        holder.setProductImage(resource);
        holder.setProductTitle(title);
        holder.setProductDescription(description);
        holder.setProductPrice(price);
    }


    // return size of list
    @Override
    public int getItemCount() {
        // to show only 8 items at a time in a horizontal view
        return min(horizontalProductScrollModelList.size(),8);
    }

    //access all elementss here
    public class ViewHolder extends RecyclerView.ViewHolder {

        //create variables for all items under the layout

        private ImageView productImage;
        private TextView productTitle, productDescription,productPrice;

        public ViewHolder(final View itemView) {
            super(itemView);
            // assign all above created variables their ids

            productImage = itemView.findViewById(R.id.h_s_product_image);
            productTitle = itemView.findViewById(R.id.h_s_product_title);
            productDescription = itemView.findViewById(R.id.h_s_product_description);
            productPrice = itemView.findViewById(R.id.h_s_product_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetails=new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    itemView.getContext().startActivity(productDetails);
                }
            });

        }

        //  methods to set values of above components

        //set resource by its integer id
        private void setProductImage(String resource){
            Glide.with(itemView.getContext()).load(resource)
                    .apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);
        }

        //
        private void setProductTitle(String title){
            productTitle.setText(title);
        }

        //

        private void setProductDescription(String description){
            productDescription.setText(description);
        }

        //
        private void setProductPrice(String price){
            productPrice.setText("Rs "+price+" /-");
        }

    }
}
