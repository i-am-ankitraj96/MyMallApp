package com.example.ankitraj.mymallapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    public static boolean disableCloseBtn = false;

    private EditText name,emailAdd,pwd,cnfPwd;
    private Button signUpBtn;
    private ProgressBar progressBarSignUp;

    private ImageButton crossButton;

    private String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public SignUpFragment() {
        // Required empty public constructor
    }

    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAccount=view.findViewById(R.id.tv_already_have_sign_in_id);
        parentFrameLayout=getActivity().findViewById(R.id.register_frameLayout);
        name=view.findViewById(R.id.name);
        emailAdd=view.findViewById(R.id.sign_up_id);
        pwd=view.findViewById(R.id.sign_up_password);
        cnfPwd=view.findViewById(R.id.sign_up_cnf_password);
        signUpBtn=view.findViewById(R.id.sign_up_button);
        progressBarSignUp=view.findViewById(R.id.progressBarSignUp);
        crossButton=view.findViewById(R.id.goDirectly_FromSignUp);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        if(disableCloseBtn){
            crossButton.setVisibility(View.GONE);
        }else{
            crossButton.setVisibility(View.VISIBLE);
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new LoginFragment());
            }
        });


        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String _id=emailAdd.getText().toString(),
                        _pwd=pwd.getText().toString(),
                        _cnfPwd=cnfPwd.getText().toString();
                if(check(_id,_pwd,_cnfPwd)){
                    signUpBtn.setVisibility(View.GONE);
                    SystemClock.sleep(1000);
                    progressBarSignUp.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(_id,_pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                final Map<Object,String> userdata=new HashMap<>();
                                userdata.put("fullname",name.getText().toString());
                                firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                        .set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            CollectionReference userDataReference  = firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");


                                            ////// Maps
                                            Map<String,Object> wishlistMap=new HashMap<>();
                                            wishlistMap.put("list_size",(long)0);

                                            Map<String,Object> ratingsMap=new HashMap<>();
                                            ratingsMap.put("list_size",(long)0);

                                            Map<String,Object> cartMap=new HashMap<>();
                                            cartMap.put("list_size",(long)0);

                                            Map<String,Object> myAddressessMap=new HashMap<>();
                                            myAddressessMap.put("list_size",(long)0);

                                            final List<String> documentNames = new ArrayList<>();
                                            documentNames.add("MY_WISHLIST");
                                            documentNames.add("MY_RATINGS");
                                            documentNames.add("MY_CART");
                                            documentNames.add("MY_ADDRESSES");

                                            List<Map<String,Object>> documentFields = new ArrayList<>();
                                            documentFields.add(wishlistMap);
                                            documentFields.add(ratingsMap);
                                            documentFields.add(cartMap);
                                            documentFields.add(myAddressessMap);


                                            for(int x = 0 ; x < documentNames.size() ; x++){

                                                final int finalX = x;
                                                userDataReference.document(documentNames.get(x))
                                                        .set(documentFields.get(x))
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                 if(task.isSuccessful()){
                                                                     if(finalX == documentNames.size()-1){
                                                                         mainIntent();
                                                                     }

                                                                 }else{
                                                                         signUpBtn.setVisibility(View.VISIBLE);
                                                                         progressBarSignUp.setVisibility(View.GONE);
                                                                         Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                                                                 }
                                                            }
                                                        });

                                            }

                                        }else{

                                            Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });


                            }else{
                                signUpBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Please follow all instructions !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }

    private boolean check(String _id,String _pwd,String _cnfPwd){
        boolean ans;
        _pwd.trim();
        _cnfPwd.trim();
        if(_pwd.equals(_cnfPwd) && _cnfPwd.length()>=8 && _id.matches(EMAIL_PATTERN))
            ans=true;
        else
            ans=false;
        return ans;
    }

    private void mainIntent(){
        Intent mainIntent=new Intent(getActivity(),MainActivity.class);
        startActivity(mainIntent);
        disableCloseBtn = false;
        getActivity().finish();

    }
}
