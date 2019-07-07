package com.example.ankitraj.mymallapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AnkitRaj on 13-Jun-19.
 */

public class DBQueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static List<String>  cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishListModel> wishListModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRatings = new ArrayList<>();

    public static void loadCategories(final CategoryAdapter categoryAdapter , final Context context){
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),documentSnapshot.get("categoryName").toString()));
                            }
                            categoryAdapter.notifyDataSetChanged();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final HomePageAdapter adapter , final Context context,final int index,String categoryName){

        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){

                                if((long) documentSnapshot.get("view_type") == 0){

                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long noOfBanners = (long) documentSnapshot.get("no_of_banners");
                                    for(long x=1 ; x<noOfBanners+1; x++){
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_"+x).toString()
                                                ,documentSnapshot.get("banner_"+x+"_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0,sliderModelList));

                                }else if((long) documentSnapshot.get("view_type") == 1){

                                    lists.get(index).add(new HomePageModel(1,documentSnapshot.get("strip_ad_banner").toString()
                                            ,documentSnapshot.get("background").toString()));

                                }else if((long) documentSnapshot.get("view_type") == 2){

                                    List<WishListModel> viewAllProductList = new ArrayList<>();

                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long noOfProducts = (long) documentSnapshot.get("no_of_products");
                                    for(long x=1 ; x<noOfProducts+1; x++){
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString(),
                                                documentSnapshot.get("product_image_"+x).toString()
                                                ,documentSnapshot.get("product_title_"+x).toString()
                                                ,documentSnapshot.get("product_subtitle_"+x).toString()
                                                ,documentSnapshot.get("product_price_"+x).toString()));


                                        viewAllProductList.add(new WishListModel(documentSnapshot.get("product_ID_"+x).toString()
                                                ,documentSnapshot.get("product_image_"+x).toString()
                                        ,(long)documentSnapshot.get("total_ratings_"+x)
                                        ,documentSnapshot.get("product_full_title_"+x).toString()
                                        ,documentSnapshot.get("average_rating_"+x).toString()
                                        ,documentSnapshot.get("product_price_"+x).toString()
                                        ,documentSnapshot.get("product_cut_price_"+x).toString()
                                        ,(boolean)documentSnapshot.get("COD_"+x)));

                                    }
                                    lists.get(index).add(new HomePageModel(2
                                            ,documentSnapshot.get("layout_background").toString()
                                            ,documentSnapshot.get("layout_title").toString()
                                            ,horizontalProductScrollModelList,viewAllProductList));

                                }else if((long) documentSnapshot.get("view_type") == 3){

                                    List<HorizontalProductScrollModel> gridLayout = new ArrayList<>();
                                    long noOfProducts = (long) documentSnapshot.get("no_of_products");
                                    for(long x=1 ; x<noOfProducts+1; x++){
                                        gridLayout.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_"+x).toString(),
                                                documentSnapshot.get("product_image_"+x).toString()
                                                ,documentSnapshot.get("product_title_"+x).toString()
                                                ,documentSnapshot.get("product_subtitle_"+x).toString()
                                                ,documentSnapshot.get("product_price_"+x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(3
                                            ,documentSnapshot.get("layout_background").toString()
                                            ,documentSnapshot.get("layout_title").toString()
                                            ,gridLayout));

                                }

                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadWishList(final Context context, final Dialog dialog , final boolean loadProductData){
        wishList.clear();
        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_WISHLIST").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            for(long x = 0 ; x < (long)task.getResult().get("list_size") ; x++){
                                wishList.add(task.getResult().get("product_ID_"+x).toString());

                                if(DBQueries.wishList.contains(ProductDetailsActivity.productID)){
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                                    if(ProductDetailsActivity.addToWishListBtn != null) {
                                        ProductDetailsActivity.addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                                    }
                                }else{
                                    if(ProductDetailsActivity.addToWishListBtn != null) {
                                        ProductDetailsActivity.addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#817e7e")));
                                    }
                                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                                }

                                if(loadProductData) {
                                    wishListModelList.clear();
                                    final String productId = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("PRODUCTS")
                                            .document(productId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                final DocumentSnapshot documentSnapshot = task.getResult();

                                                FirebaseFirestore.getInstance().collection("PRODUCTS")
                                                        .document(productId)
                                                        .collection("QUANTITY")
                                                        .orderBy("time", Query.Direction.ASCENDING)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    if(task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){

                                                                        wishListModelList.add(new WishListModel(productId,
                                                                                documentSnapshot.get("product_image_1").toString()
                                                                                , (long) documentSnapshot.get("total_ratings")
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("average_rating").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , documentSnapshot.get("product_cut_price").toString()
                                                                                , (boolean) documentSnapshot.get("COD")));

                                                                    }else{

                                                                        wishListModelList.add(new WishListModel(productId,
                                                                                documentSnapshot.get("product_image_1").toString()
                                                                                , (long) documentSnapshot.get("total_ratings")
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("average_rating").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , documentSnapshot.get("product_cut_price").toString()
                                                                                , (boolean) documentSnapshot.get("COD")));

                                                                    }
                                                                    MyWishListFragment.wishListAdapter.notifyDataSetChanged();

                                                                }else{
                                                                    Toast.makeText(context, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                });
    }

    public static void loadRatingList(final Context context){

        if(!ProductDetailsActivity.running_rating_query) {
            //ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRatings.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA")
                    .document("MY_RATINGS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {

                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRatings.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {

                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                }
                            }

                        }

                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                /*ProductDetailsActivity.running_rating_query = true;*/
            });
            //ProductDetailsActivity.running_rating_query = true;
        }

    }

    public static void loadCartList(final Context context,final Dialog dialog ,final boolean loadProductData , final  TextView totalCartAmt){
        cartList.clear();
        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_CART").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            for(long x = 0 ; x < (long)task.getResult().get("list_size") ; x++){
                                cartList.add(task.getResult().get("product_ID_"+x).toString());

                                if(DBQueries.cartList.contains(ProductDetailsActivity.productID)){
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                                }else{
                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                                }

                                if(loadProductData) {
                                    cartItemModelList.clear();
                                    final String productId = task.getResult().get("product_ID_" + x).toString();
                                    firebaseFirestore.collection("PRODUCTS")
                                            .document(productId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {

                                                final DocumentSnapshot documentSnapshot = task.getResult();

                                                FirebaseFirestore.getInstance().collection("PRODUCTS")
                                                        .document(productId)
                                                        .collection("QUANTITY")
                                                        .orderBy("time", Query.Direction.ASCENDING)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()){
                                                                    int index = 0;
                                                                    if(cartList.size() >= 2){
                                                                        index = cartList.size()-2;
                                                                    }


                                                                    if(task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){

                                                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                                                productId,
                                                                                documentSnapshot.get("product_image_1").toString()
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , (long)1
                                                                                ,(long) documentSnapshot.get("stock_quantity")));

                                                                    }else{

                                                                        cartItemModelList.add(index, new CartItemModel(CartItemModel.CART_ITEM,
                                                                                productId,
                                                                                documentSnapshot.get("product_image_1").toString()
                                                                                , documentSnapshot.get("product_title").toString()
                                                                                , documentSnapshot.get("product_price").toString()
                                                                                , (long)1
                                                                                ,(long) documentSnapshot.get("stock_quantity")));
                                                                    }

                                                                    if(cartList.size() == 1){
                                                                        //Toast.makeText(totalCartAmt.getContext(), "Ankit here", Toast.LENGTH_LONG).show();
                                                                        cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                                        LinearLayout parent = (LinearLayout) totalCartAmt.getParent().getParent();
                                                                        parent.setVisibility(View.GONE);

                                                                    }

                                                                    if(cartList.size() == 0){
                                                                        cartItemModelList.clear();
                                                                    }

                                                                    MyCartFragment.cartAdapter.notifyDataSetChanged();

                                                                }else{
                                                                    Toast.makeText(context, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                            //dialog.dismiss();
                                        }
                                    });
                                }
                            }

                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }

                });
    }

    public static void removeFromWishList(final int index, final Context context){
        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String,Object> updateWishList = new HashMap<>();
        for(int x = 0 ; x < wishList.size() ; x++){
            updateWishList.put("product_ID_"+x,wishList.get(x));

        }
        updateWishList.put("list_size",(long)wishList.size());

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_WISHLIST")
                .set(updateWishList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            if(wishListModelList.size()!=0){
                                wishListModelList.remove(index);
                                MyWishListFragment.wishListAdapter.notifyDataSetChanged();
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                            Toast.makeText(context,"Removed successfully",Toast.LENGTH_SHORT).show();

                        }else{
                            if(ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                            }
                            wishList.add(removedProductId);
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        if(ProductDetailsActivity.addToWishListBtn != null) {
                            //ProductDetailsActivity.addToWishListBtn.setEnabled(true);
                            ProductDetailsActivity.running_wishlist_query = false;
                        }
                    }
                });
    }

    public static void removeFromCart(final int index , final Context context ,final TextView cartTotalAmt){
        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String,Object> updateCartList = new HashMap<>();
        for(int x = 0 ; x < cartList.size() ; x++){
            updateCartList.put("product_ID_"+x,cartList.get(x));

        }
        updateCartList.put("list_size",(long)cartList.size());

        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_CART")
                .set(updateCartList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            if(cartItemModelList.size()!=0){
                                cartItemModelList.remove(index);
                                MyCartFragment.cartAdapter.notifyDataSetChanged();
                            }
                            if(cartList.size() == 0){
                                LinearLayout parent = (LinearLayout) cartTotalAmt.getParent().getParent();
                                cartItemModelList.clear();
                                parent.setVisibility(View.GONE);
                            }
                            Toast.makeText(context,"Removed successfully",Toast.LENGTH_SHORT).show();

                        }else{
                            cartList.add(removedProductId);
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        ProductDetailsActivity.running_cart_query = false;
                    }
                });
    }

    public static void loadAddresses(final Context context , final Dialog loadingDialog){

        addressesModelList.clear();
        firebaseFirestore.collection("USERS")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA")
                .document("MY_ADDRESSES").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Intent deliveryIntent;
                            if((long)task.getResult().get("list_size") == 0){
                                deliveryIntent = new Intent(context,AddAddressActivity.class);
                                deliveryIntent.putExtra("INTENT","deliveryIntent");
                            }else{

                                for(long x = 1 ; x <= (long)task.getResult().get("list_size") ; x++){
                                    addressesModelList.add(new AddressesModel(task.getResult().get("fullname_"+x).toString()
                                            ,task.getResult().get("address_"+x).toString()
                                            ,task.getResult().get("pincode_"+x).toString()
                                            ,(Boolean) task.getResult().get("selected_"+x)
                                            , task.getResult().getString("mobile_no_"+x).toString()));
                                    if((boolean)task.getResult().get("selected_"+x)){
                                        selectedAddress = (int)x-1;
                                    }
                                }

                                deliveryIntent = new Intent(context,DeliveryActivity.class);
                            }
                            context.startActivity(deliveryIntent);
                        }else{
                            Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });

    }

    public static void clearData(){
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishListModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRatings.clear();
        addressesModelList.clear();
    }

}
