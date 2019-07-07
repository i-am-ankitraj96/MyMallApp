package com.example.ankitraj.mymallapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ankitraj.mymallapp.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView myAddressesRecyclerView ;
    private Button deliverHereBtn;
    private static AddressesAdapter addressesAdapter;

    private LinearLayout addNewAddressBtn;
    private TextView addressesSaved;

    private Dialog loadingDialog;

    private int previousAddress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        previousAddress = DBQueries.selectedAddress;

        myAddressesRecyclerView = findViewById(R.id.addresses_recycler_view);
        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddressBtn = findViewById(R.id.add_new_address_btn);
        addressesSaved = findViewById(R.id.address_save);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(linearLayoutManager);

        int mode = getIntent().getIntExtra("MODE",-1);

        if(mode == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE );
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBQueries.selectedAddress != previousAddress){

                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBQueries.selectedAddress+1),true);

                    previousAddress = DBQueries.selectedAddress;

                    FirebaseFirestore.getInstance()
                            .collection("USERS")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    finish();
                                }else{
                                    previousAddress = previousAddressIndex;
                                    Toast.makeText(MyAddressesActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                        }
                    });
                }else{
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBQueries.addressesModelList,mode);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressintent = new Intent(MyAddressesActivity.this,AddAddressActivity.class);
                addAddressintent.putExtra("INTENT","null");
                startActivity(addAddressintent);
            }
        });

        addressesSaved.setText(String.valueOf(DBQueries.addressesModelList.size())+ " addresses saved ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBQueries.addressesModelList.size())+ " addresses saved ");
    }

    public static void refreshItem(int deSelectPos, int selectPos){
        addressesAdapter.notifyItemChanged(deSelectPos);
        addressesAdapter.notifyItemChanged(selectPos);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(DBQueries.selectedAddress != previousAddress){
                DBQueries.addressesModelList.get(DBQueries.selectedAddress).setSelected(false);
                DBQueries.addressesModelList.get(previousAddress).setSelected(true);
                DBQueries.selectedAddress = previousAddress ;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(DBQueries.selectedAddress != previousAddress){
            DBQueries.addressesModelList.get(DBQueries.selectedAddress).setSelected(false);
            DBQueries.addressesModelList.get(previousAddress).setSelected(true);
            DBQueries.selectedAddress = previousAddress ;
        }
        super.onBackPressed();
    }
}
