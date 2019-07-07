package com.example.ankitraj.mymallapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.google.android.gms.common.SignInButton;

public class RegisterActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    public static boolean setSignUpFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameLayout=findViewById(R.id.register_frameLayout);

        if(setSignUpFragment){
            setSignUpFragment = false;
            setFragment(new SignUpFragment());
        }else{
            setFragment(new LoginFragment());
        }
        //setFragment(new LoginFragment());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            LoginFragment.disableCloseBtn =false;
            SignUpFragment.disableCloseBtn =false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
}
