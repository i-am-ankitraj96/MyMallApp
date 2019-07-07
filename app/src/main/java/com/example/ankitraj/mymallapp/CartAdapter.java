package com.example.ankitraj.mymallapp;

import android.app.Dialog;
import android.app.VoiceInteractor;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AnkitRaj on 11-Jun-19.
 */

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private TextView cartTotalAmt;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmt,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmt = cartTotalAmt;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch(cartItemModelList.get(position).getType()){
            case 0 :
                return CartItemModel.CART_ITEM;

            case 1:
                return CartItemModel.TOTAL_AMOUNT;

            default:
                return -1;


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch(viewType){
            case CartItemModel.CART_ITEM:
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new CartItemViewHolder(itemView);
            case CartItemModel.TOTAL_AMOUNT:
                View totalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                return new CartTotalAmountViewHolder(totalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch(cartItemModelList.get(position).getType()){
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String resource = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                Long productQuantity = cartItemModelList.get(position).getProductQty();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIDs = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,productPrice,position, String.valueOf(productQuantity),qtyError,qtyIDs,stockQty);
                break;

            case CartItemModel.TOTAL_AMOUNT:

                int totalItems = 0;
                int totalItemsPrice = 0;
                String deliveryPrice ;
                int totalAmount ;
                int savedAmount = 0;

                for(int x = 0 ; x < cartItemModelList.size() ; x++){
                    if(cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM){
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQty()));
                        totalItems += quantity ;
                        totalItemsPrice += Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;

                        //if(!TextUtils.isEmpty(cartItemModelList.get(x).getCu))
                    }
                }

                if(totalItemsPrice > 500){
                    deliveryPrice = "FREE";
                    totalAmount = totalItemsPrice;
                }else{
                    deliveryPrice = "100";
                    totalAmount = totalItemsPrice + 100;
                }

                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemsPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);

                ((CartTotalAmountViewHolder)holder).setTotalAmt(totalItems,totalItemsPrice,deliveryPrice,totalAmount,savedAmount);

                break;

            default:

                return;

        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView productQty;

        private LinearLayout deleteBtn;

        public CartItemViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productQty = itemView.findViewById(R.id.product_quantity);
            productPrice = itemView.findViewById(R.id.wishlist_product_price);
            deleteBtn = itemView.findViewById(R.id.remove_item_linear_layout);
        }

        private void setItemDetails(final String productID, String resource, String title, String price , final int position, final String quantity, boolean qtyError , final List<String> qtyIds, final long stockQty){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home_icon)).into(productImage);
            productTitle.setText(title);
            productPrice.setText(price);

            productQty.setText("Qty : "+quantity);
            if(!showDeleteBtn) {
                if (qtyError) {
                    productQty.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                    productQty.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
                    // self made Toast
                    Toast.makeText(productQty.getContext(), "Max quantity is less than selected value", Toast.LENGTH_SHORT).show();
                } else {
                    productQty.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                    productQty.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));

                }
            }

            productQty.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog qtyDialog = new Dialog(itemView.getContext());
                    qtyDialog.setContentView(R.layout.quantity_dialog);
                    qtyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    qtyDialog.setCancelable(false);

                    final EditText qtyNo = qtyDialog.findViewById(R.id.quantity_no);
                    Button cancelBtn = qtyDialog.findViewById(R.id.cancel_btn);
                    Button okBtn = qtyDialog.findViewById(R.id.ok_btn);
                    qtyNo.setHint("Min. 1");

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            qtyDialog.dismiss();
                        }
                    });

                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!TextUtils.isEmpty(qtyNo.getText()) && Long.valueOf(qtyNo.getText().toString())>0 ) {

                                if(itemView.getContext() instanceof  MainActivity){
                                    cartItemModelList.get(position).setProductQty(Long.valueOf(qtyNo.getText().toString()));
                                }else {
                                    if (DeliveryActivity.fromCart) {
                                        cartItemModelList.get(position).setProductQty(Long.valueOf(qtyNo.getText().toString()));
                                    } else {
                                        DeliveryActivity.cartItemModelList.get(position).setProductQty(Long.valueOf(qtyNo.getText().toString()));
                                    }
                                }
                                productQty.setText("Qty : " + qtyNo.getText());

                                notifyItemChanged(cartItemModelList.size()-1);
                                // extra line
                                if(cartItemModelList.size()>=2){
                                    notifyItemChanged(cartItemModelList.size()-2);
                                }

                                if(!showDeleteBtn){

                                    DeliveryActivity.loadingDialog.show();
                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(false);

                                    final int initialQty = Integer.parseInt(quantity);
                                    final int finalQty = Integer.parseInt(qtyNo.getText().toString());

                                    final FirebaseFirestore firebaseFirestore =FirebaseFirestore.getInstance();

                                    if(finalQty > initialQty) {
                                        for (int y = 0; y < finalQty - initialQty; y++) {
                                            final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);
                                            Map<String, Object> timestamp = new HashMap<>();
                                            timestamp.put("time", FieldValue.serverTimestamp());
                                            final int finalY = y;
                                            firebaseFirestore.collection("PRODUCTS")
                                                    .document(productID)
                                                    .collection("QUANTITY")
                                                    .document(quantityDocumentName)
                                                    .set(timestamp)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            qtyIds.add(quantityDocumentName);

                                                            if (finalY + 1 == finalQty - initialQty) {

                                                                firebaseFirestore.collection("PRODUCTS")
                                                                        .document(productID)
                                                                        .collection("QUANTITY")
                                                                        .orderBy("time", Query.Direction.ASCENDING)
                                                                        .limit(stockQty)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    List<String> serverQuantity = new ArrayList<>();

                                                                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                                                                        serverQuantity.add(documentSnapshot.getId());
                                                                                    }
                                                                                    long availableQty = 0;
                                                                                    boolean noLongerAvailable = true;
                                                                                    for (String qtyID : qtyIds) {
                                                                                        if (!serverQuantity.contains(qtyID)) {
                                                                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                            //DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                            Toast.makeText(itemView.getContext(), "Sorry ! all products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                                        }else{
                                                                                            availableQty ++ ;
                                                                                        }
                                                                                    }

                                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                                } else {
                                                                                    Toast.makeText(itemView.getContext(), task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                DeliveryActivity.loadingDialog.dismiss();
                                                                            }
                                                                        });

                                                            }

                                                        }
                                                    });
                                        }
                                    }else if(initialQty > finalQty){

                                        for (int x = 0 ; x< initialQty - finalQty ; x++ ) {
                                            final String qtyID = qtyIds.get(qtyIds.size() - 1 - x);
                                            final int finalX = x;
                                            firebaseFirestore.collection("PRODUCTS")
                                                    .document(productID)
                                                    .collection("QUANTITY")
                                                    .document(qtyID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            qtyIds.remove(qtyID);
                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                            if(finalX+1 == initialQty - finalQty ){
                                                                DeliveryActivity.loadingDialog.dismiss();
                                                            }
                                                        }
                                                    });

                                        }

                                    }

                                }

                            }else{
                                Toast.makeText(itemView.getContext(),"Quantity must be a positive number",Toast.LENGTH_SHORT).show();
                            }

                            qtyDialog.dismiss();
                        }
                    });
                    qtyDialog.show();
                }
            }));


            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }else{
                deleteBtn.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;
                        DBQueries.removeFromCart(position,itemView.getContext(),cartTotalAmt);
                    }
                }
            });
        }
    }


    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder{

        private TextView totalItems;
        private TextView totalItemPrice;
        private TextView deliveryPrice;
        private TextView totalAmt;
        private TextView savedAmt;


        public CartTotalAmountViewHolder(View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmt = itemView.findViewById(R.id.total_price);
            savedAmt = itemView.findViewById(R.id.saved_amount);
        }

        private void setTotalAmt(int totalItemText,int totalItemPriceText, String deliveryPriceText,int totalAmtText,int savedAmtText){
            totalItems.setText("Price("+totalItemText+ " items )");
            totalItemPrice.setText("Rs. "+totalItemPriceText+" /-");
            if(deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            }else{
                deliveryPrice.setText("Rs. "+deliveryPriceText+" /-");
            }
            totalAmt.setText("Rs. "+totalAmtText+" /-");
            cartTotalAmt.setText("Rs. "+totalAmtText+" /-");
            savedAmt.setText("You saved Rs. "+savedAmtText+" /- on this order");

            LinearLayout parent = (LinearLayout) cartTotalAmt.getParent().getParent();

            if(totalItemPriceText == 0){
                if(DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if(showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            }else{
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
