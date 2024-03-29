package com.example.ankitraj.mymallapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {


    public MyOrdersFragment() {
        // Required empty public constructor
    }

    private RecyclerView myOrderRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_orders, container, false);

        myOrderRecyclerView = view.findViewById(R.id.my_orders_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        myOrderRecyclerView.setLayoutManager(layoutManager);

        List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.bed_img,2,"King Bed SIze2.2"
                ,"Delivered on Mon,12th Jan 2013"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.bed_img,2,"King Bed SIze2.2"
                ,"Cancelled"));
        myOrderItemModelList.add(new MyOrderItemModel(R.drawable.bed_img,2,"King Bed SIze2.2"
                ,"Delivered on Mon,12th Jan 2013"));

        MyOrderAdapter myOrderAdapter = new MyOrderAdapter((myOrderItemModelList));
        myOrderRecyclerView.setAdapter(myOrderAdapter);
        myOrderAdapter.notifyDataSetChanged();


        return view;
    }

}
