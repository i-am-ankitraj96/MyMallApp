package com.example.ankitraj.mymallapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {

    private EditText email_id_recovery;
    private Button recover_Pwd_button;
    private TextView go_back_TextView;

    private FrameLayout parentFrameLayout;

    private String EMAIL_PATTERN="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private FirebaseAuth firebaseAuth;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forgot_password, container, false);

        email_id_recovery=view.findViewById(R.id.email_id_recovery);
        recover_Pwd_button=view.findViewById(R.id.reset_pwd_button);
        go_back_TextView=view.findViewById(R.id.go_back_Image_view);

        parentFrameLayout=getActivity().findViewById(R.id.register_frameLayout);

        firebaseAuth=FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recover_Pwd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email_id_recovery.getText())){
                    email_id_recovery.setError("Empty Id!");
                }else if(! email_id_recovery.getText().toString().matches(EMAIL_PATTERN)){
                    email_id_recovery.setError("Wrongly formatted email id.");
                }else{

                    recover_Pwd_button.setVisibility(View.GONE);

                    firebaseAuth.sendPasswordResetEmail(email_id_recovery.getText().toString()).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(getActivity(),"password sent successfully!",Toast.LENGTH_SHORT).show();
                                    }else{
                                        recover_Pwd_button.setVisibility(View.VISIBLE);
                                        String errorMsg=task.getException().getMessage();
                                        Toast.makeText(getActivity(),errorMsg,Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                }

            }
        });

        go_back_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });
    }


    private void setFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();

    }
}
