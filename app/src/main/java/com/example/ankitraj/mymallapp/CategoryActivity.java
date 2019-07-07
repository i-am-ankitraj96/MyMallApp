package com.example.ankitraj.mymallapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static com.example.ankitraj.mymallapp.DBQueries.lists;
import static com.example.ankitraj.mymallapp.DBQueries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    // declare variable for the RecyclerView of this Activity
    private RecyclerView categoryRecyclerView;
    HomePageAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // set toolBar as ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set Tutle of the ToolBAr
        // first get The title from the Intent
        String title=getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        // set back Arrow to go to parent Activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //assign recyclerView
        categoryRecyclerView=findViewById(R.id.category_recyclerview);

        // recycler view code for only particular activity

        //testing multiple layout recycler view
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManager);
        //set Adapter

        int listPosition = 0;
        for(int x = 0; x < loadedCategoriesNames.size() ; x++ ){
            if(loadedCategoriesNames.get(x) . equals(title.toUpperCase())){
                listPosition = x;
            }
        }

        if(listPosition == 0){
            loadedCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            adapter = new HomePageAdapter(lists.get(loadedCategoriesNames.size()-1));
            DBQueries.loadFragmentData(adapter,this,loadedCategoriesNames.size()-1,title);
        }else{
            adapter = new HomePageAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    // create menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==R.id.main_search_icon){
            //todo : search
            return true;
        }else if(id == android.R.id.home){  //enabling back button on Action Bar // note: "home" is the predefined id
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
