package com.example.ankitraj.mymallapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by AnkitRaj on 11-Jun-19.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.Viewholder> {

    private List<MyOrderItemModel> myOrderItemModelList;

    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }



    @Override
    public MyOrderAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(MyOrderAdapter.Viewholder holder, int position) {

        int resource = myOrderItemModelList.get(position).getProductImage();
        int rating = myOrderItemModelList.get(position).getRating();
        String title = myOrderItemModelList.get(position).getProductTitle();
        String deliveredDate = myOrderItemModelList.get(position).getDeliveryStatus();
        holder.setData(resource,title,deliveredDate,rating);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{

        private ImageView productImage ;
        private ImageView orderIndicator;
        private TextView productTitle;
        private TextView deliveryStatus;
        private LinearLayout rateNowContainer;


        public Viewholder(final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            orderIndicator = itemView.findViewById(R.id.order_indicator);
            deliveryStatus = itemView.findViewById(R.id.order_delivered_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(),OrderDetailsActivity.class);
                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });
        }

        private void setData(int resource,String title,String deliveredDate,int rating){
            productImage.setImageResource( resource);
            productTitle.setText(title);
            if(deliveredDate.equals("Cancelled")){
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
            }else{
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorSuccessGreen)));
            }
            deliveryStatus.setText(deliveredDate);

            ////////// rating layout
            setRating(rating);
            for(int x=0;x<rateNowContainer.getChildCount();x++){
                final int starPosition=x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setRating(starPosition);
                    }
                });
            }
            ////////// rating layout
        }

        private void setRating(int starPosition){
            for(int x=0;x < rateNowContainer.getChildCount();x++){
                ImageView starBtn = (ImageView)rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if(x<=starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#078f1a")));
                }
            }
        }
    }
}
