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

/**
 * Created by AnkitRaj on 12-Jun-19.
 */

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private List<WishListModel> wishListModelList ;
    private Boolean wishList;

    public WishListAdapter(List<WishListModel> wishListModelList,Boolean wishlist) {
        this.wishListModelList = wishListModelList;
        this.wishList = wishlist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String productID = wishListModelList.get(position).getProductID();
        String resource = wishListModelList.get(position).getProductImage();
        String title = wishListModelList.get(position).getProductTitle();
        String rating = wishListModelList.get(position).getRating();
        long totalRatings = wishListModelList.get(position).getTotalRatings();
        String productPrice = wishListModelList.get(position).getProductPrice();
        String cutPrice = wishListModelList.get(position).getCutPrice();
        boolean paymentMethod = wishListModelList.get(position).isCOD();

        holder.setData(productID,resource,title,rating,totalRatings,productPrice
                ,cutPrice,paymentMethod,position);

    }

    @Override
    public int getItemCount() {
        return wishListModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle,rating,totalRating,
                productPrice,cutPrice,paymentMethod;

        private View priceCutView;

        public ViewHolder(View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.wishlist_product_img);
            rating = itemView.findViewById(R.id.wishlist_rating);
            productPrice = itemView.findViewById(R.id.wishlist_product_price);
            productTitle = itemView.findViewById(R.id.wishlist_product_title);
            totalRating = itemView.findViewById(R.id.wishlist_total_ratings);
            cutPrice = itemView.findViewById(R.id.wishlist_cut_price);
            paymentMethod = itemView.findViewById(R.id.wishlist_payment_method);

            priceCutView = itemView.findViewById(R.id.wishlist_view_cutter);
        }

        private void setData(final String productID, String resource , String title , String averageRate  , long totalRatingNo , String price
                              , String cutPriceVal , boolean payMethod , int index){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);
            productTitle.setText(title);
            rating.setText(averageRate);
            totalRating.setText(totalRatingNo+"(ratings)");
            productPrice.setText("Rs. "+price+" /-");

            cutPrice.setText("Rs. "+cutPriceVal+" /-");
            if(payMethod) {
                paymentMethod.setText("COD available");
            }else{
                paymentMethod.setVisibility(View.GONE);
            }

            /* forDeletionOption

            //deleteBtn.setEnabled(false);
            if(!ProductDetailsActivity.running_wishlist_query) {
                ProductDetailsActivity.running_wishlist_query = true;
            }
            DBQueries.removeFromWishList(index,itemView.getContext());


               */


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",productID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
