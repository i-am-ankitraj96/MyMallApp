package com.example.ankitraj.mymallapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import static com.example.ankitraj.mymallapp.MainActivity.showCart;
import static com.example.ankitraj.mymallapp.RegisterActivity.setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;

    public static boolean running_rating_query = false;

    public static boolean running_cart_query = false;

    public static Activity productDetailsActivity ;

    // make variables for ViewPager  & others in the layout
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addToWishListBtn;

    private TextView productTitle;
    private TextView avgRatingMiniView;
    private TextView totalRatongMiniView;
    private TextView productPrice, cutPrice;
    private ImageView codImgView;
    private TextView codTextView;

    private String productOriginalPrice ;
    private String productDescription;
    private String productOtherDetails;
    //private int tabPosition = -1 ;

    // product description

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabContainer;

    private TextView productOnlyDescriptionBody;

    ////////// rating layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView averageRating;
    private TextView totalRatings;

    private LinearLayout ratingsNoContainer;
    private LinearLayout ratingsBarContainer;

    ////////// rating layout

    private Dialog signInDialog;

    private FirebaseUser currentUser;
    public static String productID;

    private DocumentSnapshot documentSnapshot;
    private boolean inStock = false ;


    private Button buyNowBtn;
    private LinearLayout addToCartBtn;
    public static  MenuItem cartItem;

    private FirebaseFirestore firebaseFirestore;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //set app bar(Tool bar)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // add back button on tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // assign the variables declared for the views
        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.view_pager_indicator);
        addToWishListBtn = findViewById(R.id.add_to_wishList_btn);
        productTitle = findViewById(R.id.product_title);
        avgRatingMiniView = findViewById(R.id.tv_product_rating_miniView);
        totalRatongMiniView = findViewById(R.id.total_ratings_miniview);
        productPrice = findViewById(R.id.wishlist_product_price);
        cutPrice = findViewById(R.id.wishlist_cut_price);
        codImgView = findViewById(R.id.cod_indicator_img_view);
        codTextView = findViewById(R.id.cod_indicator_tv);

        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productDetailsTabContainer = findViewById(R.id.product_details_tabs_container);
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);


        totalRatings = findViewById(R.id.wishlist_total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_numbers_containers);
        ratingsBarContainer = findViewById(R.id.rating_bar_container);
        averageRating = findViewById(R.id.average_rating);

        /*// connect both ,i.e., ViewPager and indicator
        viewPagerIndicator.setupWithViewPager(productImagesViewPager,true);
*/
        buyNowBtn = findViewById(R.id.buy_now_btn);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);


        /////////  loading dialog

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////// loading dialog

        initialRating = -1;

        firebaseFirestore = FirebaseFirestore.getInstance();
        productID = getIntent().getStringExtra("PRODUCT_ID");

        final List<String> productImages = new ArrayList<>();

        firebaseFirestore.collection("PRODUCTS").document(productID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();


                            firebaseFirestore.collection("PRODUCTS")
                                    .document(productID)
                                    .collection("QUANTITY")
                                    .orderBy("time", Query.Direction.ASCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){


                                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                                }
                                                // make an adapter and pass the list
                                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                                // set productImages with this adapter
                                                productImagesViewPager.setAdapter(productImagesAdapter);

                                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                                avgRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                                                totalRatongMiniView.setText(((long) documentSnapshot.get("total_ratings")) + ("( total ratings )"));
                                                productPrice.setText("Rs. " + (documentSnapshot.get("product_price").toString()) + (" \\-"));
                                                productOriginalPrice = documentSnapshot.get("product_price").toString();
                                                cutPrice.setText(("Rs. " + (documentSnapshot.get("product_cut_price").toString()) + (" \\-")));

                                                if (!(boolean) documentSnapshot.get("COD")) {
                                                    codImgView.setVisibility(View.GONE);
                                                    codTextView.setVisibility(View.GONE);
                                                } else {
                                                    codImgView.setVisibility(View.VISIBLE);
                                                    codTextView.setVisibility(View.VISIBLE);
                                                }

                                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                                    productDetailsTabContainer.setVisibility(View.VISIBLE);
                                                    productDetailsOnlyContainer.setVisibility(View.INVISIBLE);
                                                    productDescription = documentSnapshot.get("product_description").toString();

                                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                                    for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {

                                                        productSpecificationModelList
                                                                .add(new ProductSpecificationModel(0, documentSnapshot.get("spec_" + "title_" + x).toString()));
                                                        for (long y = 1; y <= (long) documentSnapshot.get("spec_title_" + x + "_total_fields"); y++) {
                                                            productSpecificationModelList
                                                                    .add(new ProductSpecificationModel(1
                                                                            , documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString()
                                                                            , documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                                        }


                                                    }

                                                } else {
                                                    productDetailsTabContainer.setVisibility(View.INVISIBLE);
                                                    productDetailsOnlyContainer.setVisibility(View.VISIBLE);

                                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                                }


                                                totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                                                for (int x = 1; x < 6; x++) {
                                                    TextView ratingsView = (TextView) ratingsNoContainer.getChildAt(x - 1);
                                                    ratingsView.setText(String.valueOf((long) documentSnapshot.get((6 - x) + "_star")));

                                                    ProgressBar progressBar = (ProgressBar) (ratingsBarContainer.getChildAt(x - 1));
                                                    progressBar.setMax((int) ((long) documentSnapshot.get("total_ratings")));
                                                    progressBar.setProgress((int) ((long) documentSnapshot.get((6 - x) + "_star")));
                                                }

                                                averageRating.setText(documentSnapshot.get("average_rating").toString());
                                                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                                                if (currentUser != null) {
                                                    if (DBQueries.myRatings.size() == 0) {
                                                        DBQueries.loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (DBQueries.cartList.size() == 0) {
                                                        DBQueries.loadCartList(ProductDetailsActivity.this, loadingDialog, false,new TextView(ProductDetailsActivity.this));

                                                    }
                                                    if (DBQueries.wishList.size() == 0) {
                                                        DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);

                                                    }
                                                    if(DBQueries.cartList.size() != 0 && DBQueries.wishList.size() != 0){
                                                        loadingDialog.dismiss();
                                                    }
                                                } else {
                                                    loadingDialog.dismiss();
                                                }

                                                if (DBQueries.myRatedIds.contains(productID)) {
                                                    int index = DBQueries.myRatedIds.indexOf(productID);
                                                    initialRating = Integer.parseInt(String.valueOf(DBQueries.myRatings.get(index))) - 1;
                                                    setRating(initialRating);
                                                }

                                                if (DBQueries.cartList.contains(productID)) {
                                                    ALREADY_ADDED_TO_CART = true;
                                                } else {
                                                    ALREADY_ADDED_TO_CART = false;
                                                }

                                                if (DBQueries.wishList.contains(productID)) {
                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                                } else {
                                                    addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#817e7e")));
                                                    ALREADY_ADDED_TO_WISHLIST = false;
                                                }



                                                if(task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")  ){

                                                    inStock = true ;
                                                    buyNowBtn.setVisibility(View.VISIBLE);
                                                    addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (currentUser == null) {
                                                                signInDialog.show();
                                                            } else {
                                                                if (!running_cart_query) {
                                                                    running_cart_query = true;
                                                                    //addToWishListBtn.setEnabled(false);
                                                                    // if the product already added to  WishList is TRUE
                                                                    if (ALREADY_ADDED_TO_CART) {
                                                                        running_cart_query = false;
                                                                        // set Color to Grey as already added to the WishList
                                                                        Toast.makeText(ProductDetailsActivity.this, "Already added to cart !", Toast.LENGTH_SHORT).show();
                                                                    } else {     // FALSE
                                                                        Map<String, Object> addProduct = new HashMap<>();
                                                                        addProduct.put("product_ID_" + String.valueOf(DBQueries.cartList.size()), productID);
                                                                        addProduct.put("list_size", (long) (DBQueries.cartList.size() + 1));

                                                                        firebaseFirestore.collection("USERS")
                                                                                .document(currentUser.getUid())
                                                                                .collection("USER_DATA")
                                                                                .document("MY_CART")
                                                                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    if (DBQueries.cartItemModelList.size() != 0) {
                                                                                        DBQueries.cartItemModelList.add(0,new CartItemModel(CartItemModel.CART_ITEM,
                                                                                                productID,
                                                                                                documentSnapshot.get("product_image_1").toString()
                                                                                                , documentSnapshot.get("product_title").toString()
                                                                                                , documentSnapshot.get("product_price").toString()
                                                                                                , (long) 1
                                                                                                ,(long)documentSnapshot.get("stock_quantity")));
                                                                                    }

                                                                                    ALREADY_ADDED_TO_CART = true;
                                                                                    DBQueries.cartList.add(productID);
                                                                                    Toast.makeText(ProductDetailsActivity.this, "Product Added to Cart Successfully", Toast.LENGTH_SHORT).show();
                                                                                    invalidateOptionsMenu();
                                                                                    running_cart_query = false;
                                                                                } else {
                                                                                    running_cart_query = false;
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });


                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    inStock = false;
                                                    // self made Toast
                                                    Toast.makeText(ProductDetailsActivity.this, "Sorry !  product out of stock ", Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // connect both ,i.e., ViewPager and indicator
        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);


        // add onClick Listener to the FAB
        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        //addToWishListBtn.setEnabled(false);
                        // if the product already added to  WishList is TRUE
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            // set Color to Grey as already added to the WishList
                            int index = DBQueries.wishList.indexOf(productID);
                            DBQueries.removeFromWishList(index, ProductDetailsActivity.this);
                            addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#817e7e")));
                        } else {     // FALSE

                            addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBQueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) (DBQueries.wishList.size() + 1));

                            firebaseFirestore.collection("USERS")
                                    .document(currentUser.getUid())
                                    .collection("USER_DATA")
                                    .document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (DBQueries.wishListModelList.size() != 0) {
                                            DBQueries.wishListModelList.add(new WishListModel(productID
                                                    , documentSnapshot.get("product_image_1").toString()
                                                    , (long) documentSnapshot.get("total_ratings")
                                                    , documentSnapshot.get("product_title").toString()
                                                    , documentSnapshot.get("average_rating").toString()
                                                    , documentSnapshot.get("product_price").toString()
                                                    , documentSnapshot.get("product_cut_price").toString()
                                                    , (boolean) documentSnapshot.get("COD")));
                                        }

                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                                        DBQueries.wishList.add(productID);
                                        Toast.makeText(ProductDetailsActivity.this, "Product Added to WishListSuccessfully", Toast.LENGTH_SHORT);
                                    } else {
                                        //addToWishListBtn.setEnabled(true);
                                        addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#817e7e")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;
                                }
                            });


                        }
                    }
                }
            }
        });

        /*productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(),productDetailsTabLayout.getTabCount()));
*/
        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tabPosition = tab.getPosition();
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ////////// rating layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (!running_rating_query) {

                            //running_rating_query = true;
                            setRating(starPosition);
                            Map<String, Object> updateRating = new HashMap<>();
                            if (DBQueries.myRatedIds.contains(productID)) {
                                TextView oldRatingsView = (TextView) ratingsNoContainer.getChildAt(4 - initialRating);
                                TextView finalRatingsView = (TextView) ratingsNoContainer.getChildAt(4 - starPosition);

                                updateRating.put((initialRating + 1) + "_star", Long.parseLong(oldRatingsView.getText().toString()) - 1);
                                updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRatingsView.getText().toString()) + 1);
                                updateRating.put("average_rating", calcAvgRating((long) starPosition - initialRating, true));

                            } else {
                                updateRating.put((starPosition + 1) + "_star"
                                        , (long) documentSnapshot.get((starPosition + 1) + "_star") + 1);
                                updateRating.put("average_rating", calcAvgRating((long) starPosition + 1, false));
                                updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                            }

                            firebaseFirestore.collection("PRODUCTS")
                                    .document(productID)
                                    .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Map<String, Object> myRating = new HashMap<>();

                                        if (DBQueries.myRatedIds.contains(productID)) {
                                            myRating.put("rating_" + (DBQueries.myRatedIds.indexOf(productID)), (long) starPosition + 1);
                                        } else {
                                            myRating.put("product_ID_" + DBQueries.myRatedIds.size(), productID);
                                            myRating.put("rating_" + DBQueries.myRatedIds.size(), (long) (starPosition + 1));
                                            myRating.put("list_size", (long) (DBQueries.myRatedIds.size()) + 1);

                                        }

                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                long totalRatingsTempStorage = 0;
                                                if (task.isSuccessful()) {

                                                    if (DBQueries.myRatedIds.contains(productID)) {
                                                        DBQueries.myRatings.set(DBQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                        TextView oldRatingsView = (TextView) ratingsNoContainer.getChildAt(4 - initialRating);
                                                        TextView finalRatingsView = (TextView) ratingsNoContainer.getChildAt(4 - starPosition);
                                                        finalRatingsView.setText(String.valueOf(Integer.parseInt(finalRatingsView.getText().toString()) + 1));
                                                        oldRatingsView.setText(String.valueOf(Integer.parseInt(oldRatingsView.getText().toString()) - 1));


                                                    } else {

                                                        DBQueries.myRatedIds.add(productID);
                                                        DBQueries.myRatings.add((long) starPosition + 1);


                                                        TextView ratingsView = (TextView) ratingsNoContainer.getChildAt(4 - starPosition);
                                                        ratingsView.setText(String.valueOf(Integer.parseInt(ratingsView.getText().toString()) + 1));

                                                        totalRatingsTempStorage = (long) documentSnapshot.get("total_ratings") + 1;

                                                        totalRatongMiniView.setText(((long) documentSnapshot.get("total_ratings") + 1) + ("( total ratings )"));
                                                        //totalRatongMiniView.setText(((long) documentSnapshot.get("total_ratings") + 1) + ("( total ratings )"));
                                                        totalRatings.setText(((long) documentSnapshot.get("total_ratings") + 1) + ("( total ratings )"));

                                                        Toast.makeText(ProductDetailsActivity.this, "Thank you! for rating. ", Toast.LENGTH_SHORT).show();
                                                    }

                                                    for (int x = 1; x < 6; x++) {
                                                        TextView ratingsFig = (TextView) ratingsNoContainer.getChildAt(x - 1);

                                                        ProgressBar progressBar = (ProgressBar) (ratingsBarContainer.getChildAt(x - 1));
                                                        progressBar.setMax((int) (totalRatingsTempStorage));
                                                        progressBar.setProgress(Integer.parseInt(ratingsFig.getText().toString()));
                                                    }
                                                    initialRating = starPosition;
                                                    avgRatingMiniView.setText(calcAvgRating(0, true));
                                                    averageRating.setText(calcAvgRating(0, true));

                                                } else {
                                                    //running_rating_query = false;
                                                    setRating(initialRating);
                                                    Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                                //running_rating_query = false;
                                            }
                                        });
                                    } else {
                                        //running_rating_query = false;
                                        setRating(initialRating);
                                        Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                }
            });
        }
        ////////// rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false ;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM,
                            productID,
                            documentSnapshot.get("product_image_1").toString()
                            , documentSnapshot.get("product_title").toString()
                            , documentSnapshot.get("product_price").toString()
                            , (long) 1
                            , (long) documentSnapshot.get("stock_quantity")));
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if(DBQueries.addressesModelList.size() == 0) {
                        DBQueries.loadAddresses(ProductDetailsActivity.this, loadingDialog);
                    }else{
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });



        ///////// signIn Dialog

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = false;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = false;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

        ///////// sign In Dialog
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (DBQueries.myRatings.size() == 0) {
                DBQueries.loadRatingList(ProductDetailsActivity.this);
            }
            if (DBQueries.wishList.size() == 0) {
                DBQueries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);

            }
            if(DBQueries.cartList.size() != 0 && DBQueries.wishList.size() != 0){
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }


        if (DBQueries.myRatedIds.contains(productID)) {
            int index = DBQueries.myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(DBQueries.myRatings.get(index))) - 1;
            setRating(initialRating);
        }

        if (DBQueries.cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (DBQueries.wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

        } else {
            ALREADY_ADDED_TO_WISHLIST = false;
            addToWishListBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#aca8a8")));
        }

        invalidateOptionsMenu();

    }

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#078f1a")));
            }
        }
    }


    private String calcAvgRating(long currentUserRating, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
            totalStars += x * (Long.parseLong(ratingNo.getText().toString()));
        }
        totalStars += (currentUserRating);
        long denominator = (long) Double.parseDouble((averageRating.getText().toString()));
        if (update) {
            if (denominator != 0)
                //return (totalStars / (Long.parseLong((averageRating.getText().toString()))));
                return String.valueOf(totalStars / denominator).substring(0, 3);
            else
                return String.valueOf(String.valueOf(totalStars / (denominator + 1))).substring(0, 3);
        }
        return "";
    }

    // import onCreateOptionsMenu() and onOptionsItemSelected() methods

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // pass layout of menu you want to be displayed on the Action Bar of this activity
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

       cartItem = menu.findItem(R.id.main_cart_icon);

        if(currentUser != null){
            if (DBQueries.cartList.size() == 0) {
                DBQueries.loadCartList(ProductDetailsActivity.this, loadingDialog , false,new TextView(ProductDetailsActivity.this));

            }
        }

       /*cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                        showCart = true;
                        startActivity(cartIntent);
                    }
                }
            });*/
        /*MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
        if(currentUser!=null){
            if(DBQueries.cartList.size() == 0){
                DBQueries.loadCartList(ProductDetailsActivity.this,loadingDialog,false,new TextView(ProductDetailsActivity.this));
                //DBQueries.loadCartList(ProductDetailsActivity.this,loadingDialog,false,);
            }
        }*/
        /*
        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null){
                    signInDialog.show();
                }else{
                    Intent cartIntent = new Intent(ProductDetailsActivity.this,MainActivity.class);
                    showCart = true ;
                    startActivity(cartIntent);
                }
            }
        });
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {   // for back-key icon
            productDetailsActivity = null ;
            finish();
            return true;
        } else if (id == R.id.main_cart_icon) {
            // todo: search
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, MainActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        } else if (id == R.id.main_search_icon) {
            // todo: cart
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null ;
        super.onBackPressed();

    }
}
