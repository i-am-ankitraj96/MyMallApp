package com.example.ankitraj.mymallapp;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.ankitraj.mymallapp.DBQueries.categoryModelList;
import static com.example.ankitraj.mymallapp.DBQueries.firebaseFirestore;
import static com.example.ankitraj.mymallapp.DBQueries.lists;
import static com.example.ankitraj.mymallapp.DBQueries.loadedCategoriesNames;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    private HomePageAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView=view.findViewById(R.id.category_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayout.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        categoryAdapter=new CategoryAdapter(categoryModelList);
        categoryRecyclerView.setAdapter(categoryAdapter);

        if(categoryModelList.size() == 0 ){
            DBQueries.loadCategories(categoryAdapter,getContext());
        }else{
            categoryAdapter.notifyDataSetChanged();
        }


        homePageRecyclerView = view.findViewById(R.id.home_page_recycler_view);
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);
        // List to pass various views
        //set Adapter

        if(lists.size() == 0 ){
            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            adapter = new HomePageAdapter(lists.get(0));
            DBQueries.loadFragmentData(adapter,getContext(),0,"Home");
        }else{
            adapter = new HomePageAdapter(lists.get(0));
            adapter.notifyDataSetChanged();
        }
        homePageRecyclerView.setAdapter(adapter);


        return view;
    }

}
