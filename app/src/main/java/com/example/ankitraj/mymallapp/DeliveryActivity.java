package com.example.ankitraj.mymallapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {


    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddNewAddressBtn;
    public static CartAdapter cartAdapter;
    private TextView totalAmtTv;

    private TextView fullName;
    private String name,mobileNo;
    private TextView fullAddress;
    private TextView pincode;

    private Button continueBtn;

    public static List<CartItemModel> cartItemModelList ;

    public static final int SELECT_ADDRESS = 0;
    private String paymentMethod = "PAYTM";

    public static Dialog loadingDialog ;
    private Dialog paymentMethodDialog ;

    private ImageView paytm,cod;

    private ConstraintLayout orderCnfLayout ;
    private ImageButton continueShoppingBtn;
    private TextView orderId;

    private boolean successResponse = false;
    public static boolean fromCart ;
    private String order_id;
    public static boolean codOrderConfirmed = false;

    private FirebaseFirestore firebaseFirestore;

    public static boolean getQtyIDs = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");


        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);

        fullName = findViewById(R.id.full_name);
        fullAddress = findViewById(R.id.address_pincode);
        pincode = findViewById(R.id.pincode);

        totalAmtTv = findViewById(R.id.total_cart_amount);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);

        continueBtn = findViewById(R.id.cart_continue_btn);

        orderCnfLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(false);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paymentMethodDialog.setCancelable(true);
        paytm = paymentMethodDialog.findViewById(R.id.paytm_btn);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        order_id = UUID.randomUUID().toString().substring(0,28);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemModelList,totalAmtTv,false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);

        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQtyIDs = false;

                Intent myAddressesIntent = new Intent(DeliveryActivity.this,MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });


        if(DBQueries.selectedAddress != -1){
            fullName.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFullName());
            fullAddress.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFuladdress());
            pincode.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getPinCode());
        }else{
            Intent addAddressActivity = new Intent(DeliveryActivity.this,AddAddressActivity.class);
            addAddressActivity.putExtra("INTENT","null_1");
            startActivity(addAddressActivity);
        }


       /* fullName.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFullName());
        fullAddress.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFuladdress());
        pincode.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getPinCode());*/

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductsAvailable = true ;
                for(CartItemModel cartItemModel : cartItemModelList){
                    if(cartItemModel.isQtyError()){
                        allProductsAvailable = false ;
                    }
                }

                if(allProductsAvailable){
                    paymentMethodDialog.show();
                }else{
                    Toast.makeText(DeliveryActivity.this, "Please see ,all chosen products not available at the moment.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "COD" ;
                placeOrderDetails();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "PAYTM" ;
                placeOrderDetails();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // accessing quantity
        if(getQtyIDs) {
            loadingDialog.show();
            // check here limit of x
            //for (int x = 0; x < cartItemModelList.size() - 2; x++) {

            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for(int y = 0; y < cartItemModelList.get(x).getProductQty(); y ++){
                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0,20);
                    Map<String,Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());
                    final int finalX = x;
                    final int finalY = y;
                    firebaseFirestore.collection("PRODUCTS")
                            .document(cartItemModelList.get(x).getProductID())
                            .collection("QUANTITY")
                            .document(quantityDocumentName)
                            .set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                   if(task.isSuccessful()){

                                       cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                       if(finalY +1 == cartItemModelList.get(finalX).getProductQty()){

                                           firebaseFirestore.collection("PRODUCTS")
                                                   .document(cartItemModelList.get(finalX).getProductID())
                                                   .collection("QUANTITY")
                                                   .orderBy("time",Query.Direction.ASCENDING)
                                                   .limit(cartItemModelList.get(finalX).getStockQuantity())
                                                   .get()
                                                   .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                           if(task.isSuccessful()){
                                                               List<String> serverQuantity = new ArrayList<>();

                                                               for(DocumentSnapshot documentSnapshot : task.getResult()){
                                                                   serverQuantity.add(documentSnapshot.getId());
                                                               }
                                                               long availableQty = 0 ;
                                                               boolean noLongerAvailable = true ;
                                                               for(String qtyID : cartItemModelList.get(finalX).getQtyIDs()){
                                                                   cartItemModelList.get(finalX).setQtyError(false);
                                                                   if(!(serverQuantity.contains(qtyID))){

                                                                       if(noLongerAvailable){
                                                                           // not a single unit
                                                                           // cartItemModelList.get(finalX).setInStock(false);
                                                                       }else {
                                                                           cartItemModelList.get(finalX).setQtyError(true);
                                                                           //cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                           Toast.makeText(DeliveryActivity.this, "Sorry ! all products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   }else{
                                                                       availableQty ++;
                                                                       noLongerAvailable = false ;
                                                                   }
                                                                   // remove this
                                                                   if(serverQuantity.size() >= cartItemModelList.get(finalX).getStockQuantity()){
                                                                       firebaseFirestore.collection("PRODUCTS")
                                                                               .document(cartItemModelList.get(finalX).getProductID())
                                                                               .update("in_stock",false);
                                                                   }
                                                               }

                                                               cartAdapter.notifyDataSetChanged();

                                                           }else{
                                                               Toast.makeText(DeliveryActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                           }
                                                           loadingDialog.dismiss();
                                                       }
                                                   });

                                       }

                                   }else{
                                       loadingDialog.dismiss();
                                       Toast.makeText(DeliveryActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                   }
                                }
                            });
                }

            }
        }else{
            getQtyIDs = true;
        }

        // accessing quantity

        name = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFullName();
        mobileNo = DBQueries.addressesModelList.get(DBQueries.selectedAddress).getMobileNo();
        fullName.setText(name + " - " + mobileNo);
        fullAddress.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getFuladdress());
        pincode.setText(DBQueries.addressesModelList.get(DBQueries.selectedAddress).getPinCode());

        if(codOrderConfirmed){
            showConfirmationLayout();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(getQtyIDs) {
            // check here limit of x
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 2; x++) {


                if(!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS")
                                .document(cartItemModelList.get(x).getProductID())
                                .collection("QUANTITY")
                                .document(qtyID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if(qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size()-1))){
                                            cartItemModelList.get(finalX).getQtyIDs().clear();
                                            // remove this
                                            firebaseFirestore.collection("PRODUCTS")
                                                    .document(cartItemModelList.get(finalX).getProductID())
                                                    .collection("QUANTITY")
                                                    .orderBy("time",Query.Direction.ASCENDING)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                // remove this

                                                                if(task.getResult().getDocuments().size() < cartItemModelList.get(finalX).getStockQuantity()){
                                                                    firebaseFirestore.collection("PRODUCTS")
                                                                            .document(cartItemModelList.get(finalX).getProductID())
                                                                            .update("in_stock",true);
                                                                }

                                                            }else{
                                                                Toast.makeText(DeliveryActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                }else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }


            }
        }
    }

    @Override
    public void onBackPressed() {
        if(successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout(){
        //Toast.makeText(DeliveryActivity.this, "here mobile no. is :"+mobileNo.substring(0,10)+"Bas", Toast.LENGTH_LONG).show();
        successResponse = true ;
        codOrderConfirmed = false;
        getQtyIDs = false;
        // check here limit of x
        for(int x=0; x<cartItemModelList.size()-2;x++){

            for(String qtyID : cartItemModelList.get(x).getQtyIDs()){

                firebaseFirestore.collection("PRODUCTS")
                        .document(cartItemModelList.get(x).getProductID())
                        .collection("QUANTITY")
                        .document(qtyID)
                        .update("user_ID",FirebaseAuth.getInstance().getUid());
            }



        }
        //Toast.makeText(DeliveryActivity.this, "here mobile no. is A : "+mobileNo.substring(0,10), Toast.LENGTH_LONG).show();

        if(MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null ;
            MainActivity.showCart = false;
        }else{
            MainActivity.resetMainActivity = true;
        }

        if(ProductDetailsActivity.productDetailsActivity != null){
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null ;
        }

        //Toast.makeText(DeliveryActivity.this, "here mobile no. is : B "+mobileNo.substring(0,10), Toast.LENGTH_LONG).show();

        ////// send confirmation sms

        String SMS_API = "https://www.fast2sms.com/dev/bulk" ;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(DeliveryActivity.this,"failed to send the confirmation message to the registered number ",Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("authorization","BTCNFdgzLmMqPUoGIsvW6xfDc2KkyQhReH37ptXnSiJ48r9V1Z9xMSbNzIWAVEFKjUY1snudT7yHOwi5");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> body = new HashMap<>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",mobileNo.substring(0,10));
                body.put("message","11572");
                body.put("variables","{#FF#}");
                body.put("variables_values", String.valueOf(order_id));
                return body;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);

        ////// send confirmation sms

        if(fromCart){
            loadingDialog.show();
            Map<String,Object> updateCartList = new HashMap<>();
            long cartListSize = 0 ;
            final List<Integer> indexList = new ArrayList<>();
            for(int x = 0 ; x < DBQueries.cartList.size() ; x++){
                indexList.add(x);
            }
            updateCartList.put("list_size",cartListSize);

            FirebaseFirestore.getInstance().collection("USERS")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA")
                    .document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        for(int x=0; x<indexList.size();x++){
                            DBQueries.cartList.remove(indexList.get(x).intValue());
                            DBQueries.cartItemModelList.remove(indexList.get(x));
                            DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size()-1);
                        }
                    }else{
                        Toast.makeText(DeliveryActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }

                    loadingDialog.dismiss();
                }
            });
        }

        continueBtn.setEnabled(false);
        changeOrAddNewAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        orderId.setText("Order ID : #"+order_id);
        orderCnfLayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeOrderDetails(){

        String userID = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for(CartItemModel cartItemModel  : cartItemModelList) {
            if(cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID",order_id);
                orderDetails.put("PRODUCT ID",cartItemModel.getProductID());
                orderDetails.put("User ID",userID);
                orderDetails.put("Product Quantity",cartItemModel.getProductQty());
                orderDetails.put("Cut Price",(long)0 );
                orderDetails.put("Product Price",cartItemModel.getProductPrice());
                orderDetails.put("Date",FieldValue.serverTimestamp());
                orderDetails.put("Payment Method",paymentMethod);
                orderDetails.put("Address",fullAddress.getText().toString());
                orderDetails.put("FullName",fullName.getText().toString());
                orderDetails.put("Pincode",pincode.getText().toString());

                firebaseFirestore.collection("ORDERS")
                        .document(order_id)
                        .collection("OrderItems")
                        .document(cartItemModel.getProductID())
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }else{
                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price",cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery Price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount",cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status","not paid");
                orderDetails.put("Order Status","cancelled");

                firebaseFirestore.collection("ORDERS")
                        .document(order_id)
                        .set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    if(paymentMethod.equals("PAYTM")){
                                        paytm();
                                    }else{
                                        cod();
                                    }

                                }else{
                                    Toast.makeText(DeliveryActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        }

    }

    private void paytm(){

        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();

        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        final String M_id = "pGljHp54795220312309";
        final String customer_id = FirebaseAuth.getInstance().getUid();
        String url = "https://mymallapp.000webhostapp.com/paytm/generateChecksum.php";
        final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";


        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.has("CHECKSUMHASH")){
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService = PaytmPGService.getStagingService();

                        HashMap<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , M_id);
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , totalAmtTv.getText().toString().substring(4,totalAmtTv.getText().length()-3) );
                        paramMap.put( "WEBSITE" , "APP_STAGING");
                        paramMap.put( "CALLBACK_URL", callBackUrl);
                        paramMap.put("CHECKSUMHASH",CHECKSUMHASH);

                        PaytmOrder order = new PaytmOrder(paramMap);

                        paytmPGService.initialize(order,null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                // Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                if(inResponse.getString("STATUS").equals("TXN_SUCCESS")){
                                        Map<String,Object> updateStatus = new HashMap<>();
                                        updateStatus.put("Payment Status","Paid");
                                        updateStatus.put("Order Status","Ordered");
                                        firebaseFirestore.collection("ORDERS")
                                                .document(order_id)
                                                .update(updateStatus)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            showConfirmationLayout();
                                                        }else{
                                                            Toast.makeText(DeliveryActivity.this, "ORDER CANCELLED due to "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this,"Oops ! something went wrong !",Toast.LENGTH_LONG);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<String,String>();



                paramMap.put( "MID" , M_id);
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , totalAmtTv.getText().toString().substring(4,totalAmtTv.getText().length()-3) );
                paramMap.put( "WEBSITE" , "APP_STAGING");
                paramMap.put( "CALLBACK_URL", callBackUrl);
                return paramMap;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void cod(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this,OTPVerificationActivity.class);
        otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
        otpIntent.putExtra("OrderID",order_id);
        startActivity(otpIntent);
    }
}
