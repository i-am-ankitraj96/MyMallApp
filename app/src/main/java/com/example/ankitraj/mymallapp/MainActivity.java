package com.example.ankitraj.mymallapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.ankitraj.mymallapp.RegisterActivity.setSignUpFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int REWARDS_FRAGMENT = 4;
    private static final int ACCOUNT_ACCOUNT = 5;

    public static Activity mainActivity ;

    public static Boolean showCart = false;
    public static boolean resetMainActivity = false;

    private  Dialog signInDialog;

    private FrameLayout frameLayout;
    private ImageView noInternetConnection ;

    private ImageView actionBarLogo;
    private int currentFragment = -1;
    private NavigationView  navigationView;

    private FirebaseUser currentUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBarLogo = findViewById(R.id.action_bar_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout=findViewById(R.id.main_frame_layout);
        noInternetConnection = findViewById(R.id.no_internet_connection);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true) {
            noInternetConnection.setVisibility(View.GONE);
            if (showCart) {
                mainActivity = this ;
                // lock navigation bar
                drawer.setDrawerLockMode(1);
                // remove hamburger icon
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                goToFragment("My Cart", new MyCartFragment(), -2);
            } else {
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                //setFragment(new OrderDetailsFragment(),HOME_FRAGMENT);
            }

            signInDialog = new Dialog(MainActivity.this);
            signInDialog.setContentView(R.layout.sign_in_dialog);
            signInDialog.setCancelable(true);

            signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            Button dialogSignInBtn = signInDialog.findViewById(R.id.dialog_sign_in_btn);
            Button dialogSignUpBtn = signInDialog.findViewById(R.id.dialog_sign_up_btn);
            final Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);

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

        } else {

            Glide.with(this).load(R.drawable.no_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null ){
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
        }else{
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
        }
        if(resetMainActivity){
            resetMainActivity = false;
            actionBarLogo.setVisibility(View.VISIBLE);
            setFragment(new HomeFragment(),HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            }else{
                if(showCart){
                    mainActivity = null ;
                    showCart = false;
                    finish();
                }else{
                    actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(),HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
                if(currentUser != null){
                    if (DBQueries.cartList.size() == 0) {
                        DBQueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false,new TextView(MainActivity.this));

                    }
                }

            /*(MenuItemCompat.getActionView(cartItem)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentUser == null) {
                        signInDialog.show();
                    }else {
                        goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                    }
                }
            });
*/
                /*cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(currentUser == null) {
                            signInDialog.show();
                        }else {
                            goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                        }
                    }
                });*/
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_notification_icon) {
            return true;
        } else if (id == R.id.main_cart_icon) {

            if(currentUser == null) {
                signInDialog.show();
            }else {
                goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
            }
            return true;
        }else if(id==R.id.main_search_icon){
            return true;
        }else if(id == android.R.id.home){
            if(showCart){
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToFragment(String title,Fragment fragment,int fragmentNo) {
        actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment,fragmentNo);
        if(fragmentNo == CART_FRAGMENT) {
            navigationView.getMenu().getItem(3).setChecked(true);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(currentUser != null) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_my_orders) {
                goToFragment("My Orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
            } else if (id == R.id.nav_my_cart) {
                goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);

            } else if (id == R.id.nav_my_wishlist) {
                goToFragment("My WishList", new MyWishListFragment(), WISHLIST_FRAGMENT);

            } else if (id == R.id.nav_my_account) {
                goToFragment("My Account", new MyAccountFragment(), 5);

            } else if (id == R.id.nav_my_rewards) {

            } else if (id == R.id.nav_sign_out) {

                FirebaseAuth.getInstance().signOut();
                DBQueries.clearData();
                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();

            } else if (id == R.id.nav_my_mail) {  // my_mall
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                actionBarLogo.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), 0);

            }
            return true;
        }else{
            signInDialog.show();
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }


    }

    private void setFragment(Fragment fragment,int fragmentNo){
        currentFragment = fragmentNo;
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
