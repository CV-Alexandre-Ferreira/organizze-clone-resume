package com.example.organizze;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizze.activity.SignUpActivity;
import com.example.organizze.activity.LoginActivity;
import com.example.organizze.activity.PrincipalActivity;
import com.example.organizze.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private Context context;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        verifyLoggedUser();

        context = this;

        setButtonBackVisible(false);
        setButtonNextVisible(false);
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                //.canGoForward(false)
                .build());
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_sign_up)
                .canGoForward(false)
                .build());

    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLoggedUser();
    }

    public void btEnter(View view){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }
    public void btSignUp(View view){
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }
    public void verifyLoggedUser(){
        auth = FirebaseConfig.getFirebaseAuth();
        if(auth.getCurrentUser() != null){
            openMainScreen();
        }
    }

    public void openMainScreen(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}