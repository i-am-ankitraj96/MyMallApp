package com.example.ankitraj.mymallapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    private String EMAIL_PATTERN="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn = false ;

    private TextView dontHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private EditText emailId,pwd;
    private TextView forgotPwd;
    private Button signInBtn;
    private ProgressBar progressBar;

    private ImageButton closeButton;

    FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        dontHaveAnAccount=view.findViewById(R.id.tv_dont_have_sign_up_id);
        parentFrameLayout=getActivity().findViewById(R.id.register_frameLayout);
        emailId=view.findViewById(R.id.sign_in_id);
        pwd=view.findViewById(R.id.sign_in_password);
        progressBar=view.findViewById(R.id.progressBarLogin);
        signInBtn=view.findViewById(R.id.sign_in_button);
        closeButton=view.findViewById(R.id.goDirectly_FromLogin);
        forgotPwd=view.findViewById(R.id.sign_in_forgot_password);

        firebaseAuth=FirebaseAuth.getInstance();

        if(disableCloseBtn){
            closeButton.setVisibility(View.GONE);
        }else{
            closeButton.setVisibility(View.VISIBLE);
        }

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _id=emailId.getText().toString(),
                        _pwd=pwd.getText().toString();
                if(check(_id,_pwd)){

                    signInBtn.setVisibility(View.GONE);

                    progressBar.setVisibility(View.VISIBLE);

                    firebaseAuth.signInWithEmailAndPassword(_id,_pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                mainIntent();

                            }else{
                                signInBtn.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                String msg=task.getException().getMessage();
                                Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }else{
                    Toast.makeText(getActivity(),"Please follow instructions !",Toast.LENGTH_SHORT).show();
                }
            }
        });

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
            }
        });

        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ForgotPasswordFragment());
            }
        });

    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }

    private boolean check(String _id,String _pwd){
        boolean ans=false;
        if(! _id.isEmpty() && ! _pwd.isEmpty()){

            ans=true;

        }else{
            if(_id.isEmpty()) emailId.setError("Empty field");
            else pwd.setError("Empty field");
        }

        return ans;
    }

    private void mainIntent(){
        Intent intent=new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        disableCloseBtn = false;
        getActivity().finish();
    }
}
