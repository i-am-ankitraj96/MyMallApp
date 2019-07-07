package com.example.ankitraj.mymallapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private Button saveBtn ;
    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText mobileNo;
    private EditText alternateMobNo;
    private Spinner stateSpinner;
    private String selectedState;

    private Dialog loadingDialog;

    private String []stateList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        stateList = getResources().getStringArray(R.array.india_states);

        stateSpinner = findViewById(R.id.state_spinner);
        saveBtn = findViewById(R.id.save_address_btn);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        pincode = findViewById(R.id.pincode);
        landmark = findViewById(R.id.landmark);
        name = findViewById(R.id.name);
        mobileNo = findViewById(R.id.mobile_no);
        alternateMobNo = findViewById(R.id.alternate_mobile_no);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(! TextUtils.isEmpty(city.getText())){
                     if(! TextUtils.isEmpty(locality.getText())){
                         if(! TextUtils.isEmpty(flatNo.getText())){
                             if(! TextUtils.isEmpty(pincode.getText()) && ((pincode.getText()).toString().trim()).length()==6){
                                 if(! TextUtils.isEmpty(name.getText())){
                                     if(! TextUtils.isEmpty(mobileNo.getText()) && ((mobileNo.getText()).toString().trim()).length()==10 ){

                                         loadingDialog.show();

                                         final String state = selectedState ;
                                         final String fullAddress = flatNo.getText().toString()+" , "
                                                               +locality.getText().toString()+" ,  "
                                                               +("  ,   land mark : "+landmark.getText().toString())+ "  , "
                                                               +city.getText().toString()
                                                               +"  , "+selectedState;


                                         Map<String , Object> addAddress = new HashMap();
                                         addAddress.put("list_size",(long)DBQueries.addressesModelList.size()+1);

                                         if(TextUtils.isEmpty(alternateMobNo.getText())) {
                                             addAddress.put("mobile_no_" + (DBQueries.addressesModelList.size() + 1), mobileNo.getText().toString());
                                         }else{
                                             addAddress.put("mobile_no_" + (DBQueries.addressesModelList.size() + 1), mobileNo.getText().toString()+" or "+alternateMobNo.getText().toString());
                                         }

                                         addAddress.put("fullname_"+(DBQueries.addressesModelList.size()+1),name.getText().toString());
                                         addAddress.put("pincode_"+(DBQueries.addressesModelList.size()+1),pincode.getText().toString());
                                         addAddress.put("address_"+(DBQueries.addressesModelList.size()+1),fullAddress);
                                         addAddress.put("selected_"+(DBQueries.addressesModelList.size()+1),true);

                                         //if(DBQueries.addressesModelList.size() > 0) {
                                             addAddress.put("selected_" + (DBQueries.selectedAddress + 1), false);
                                         //}
                                         FirebaseFirestore.getInstance().collection("USERS")
                                                 .document(FirebaseAuth.getInstance().getUid())
                                                 .collection("USER_DATA")
                                                 .document("MY_ADDRESSES")
                                                 .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {

                                                 if(task.isSuccessful()){

                                                     if(DBQueries.addressesModelList.size() > 0) {
                                                         DBQueries.addressesModelList.get(DBQueries.selectedAddress).setSelected(false);
                                                     }


                                                     if(TextUtils.isEmpty(alternateMobNo.getText())) {

                                                         DBQueries.addressesModelList.add(new AddressesModel(name.getText().toString()
                                                                 , fullAddress, pincode.getText().toString()
                                                                 , true,  mobileNo.getText().toString() ));
                                                     }else{
                                                         DBQueries.addressesModelList.add(new AddressesModel(name.getText().toString()
                                                                 , fullAddress, pincode.getText().toString()
                                                                 , true,  mobileNo.getText().toString() + " or "+alternateMobNo.getText().toString()));
                                                     }

                                                     if(getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                         Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                         startActivity(deliveryIntent);
                                                     }else{
                                                         MyAddressesActivity.refreshItem(DBQueries.selectedAddress,DBQueries.addressesModelList.size()-1);
                                                     }
                                                     DBQueries.selectedAddress = DBQueries.addressesModelList.size() - 1;
                                                     finish();

                                                 }else{
                                                     Toast.makeText(AddAddressActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                 }

                                                 loadingDialog.dismiss();

                                             }
                                         });


                                     }else{
                                         mobileNo.setError("\"Mobile No.\" must be set to 10 digits long");
                                     }
                                 }else{
                                     name.setError("\"Name\" can't be empty");
                                 }
                             }else{
                                 pincode.setError("\"Pincode\" must be set to 6 digits long");
                             }
                         }else{
                             flatNo.setError("This field can't be empty");
                             flatNo.setFocusable(true);
                         }
                     }else{
                         locality.setError("\"locality\" can't be empty");
                     }
                 }else{
                     city.setError("\"City\" can't be empty");
                     //city.setFocusable(true);
                 }

             }
         });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
