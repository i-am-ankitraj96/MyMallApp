package com.example.ankitraj.mymallapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.example.ankitraj.mymallapp.DeliveryActivity.SELECT_ADDRESS;
import static com.example.ankitraj.mymallapp.MyAccountFragment.MANAGE_ADDRESS;

/**
 * Created by AnkitRaj on 16-Jun-19.
 */

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.Viewholder> {


    private List<AddressesModel> adressesModelList;
    private int MODE;
    private int preSelectedPosition ;

    public AddressesAdapter(List<AddressesModel> adressesModelList,int MODE) {
        this.adressesModelList = adressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBQueries.selectedAddress;
    }

    @Override
    public AddressesAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(AddressesAdapter.Viewholder holder, int position) {

        String name = adressesModelList.get(position).getFullName();
        String mobileNo = adressesModelList.get(position).getMobileNo();
        String address = adressesModelList.get(position).getFuladdress();
        String pincode = adressesModelList.get(position).getPinCode();
        Boolean selected = adressesModelList.get(position).getSelected();
        holder.setData(name,address,pincode,selected,position,mobileNo);
    }

    @Override
    public int getItemCount() {
        return adressesModelList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView address;
        private TextView pincode;
        private ImageView icon;
        private LinearLayout optionContainer;

        public Viewholder(View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            pincode = itemView.findViewById(R.id.pincode);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String userName, String userAddress , String userPincode, Boolean selected, final int position,String mobileNo){
            fullName.setText(userName+" - "+mobileNo);
            address.setText(userAddress);
            pincode.setText(userPincode);

            if(MODE == SELECT_ADDRESS){

                icon.setImageResource(R.mipmap.check_icon);
                if(selected){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                }else{
                    icon.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(preSelectedPosition !=position){
                            adressesModelList.get(position).setSelected(true);
                            adressesModelList.get(preSelectedPosition).setSelected(false);
                            MyAddressesActivity.refreshItem(preSelectedPosition,position);
                            preSelectedPosition = position;
                            DBQueries.selectedAddress = position;
                        }
                    }
                });

            }else if(MODE == MANAGE_ADDRESS){
                optionContainer.setVisibility(View.GONE);
                icon.setImageResource(R.mipmap.nav_dummy_icon);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //icon.setVisibility();
                        optionContainer.setVisibility(View.VISIBLE);
                        MyAddressesActivity.refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = position ;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyAddressesActivity.refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }

        }
    }
}
