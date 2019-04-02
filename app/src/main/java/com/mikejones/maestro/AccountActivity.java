package com.mikejones.maestro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private Button mSignoutButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mSignoutButton = findViewById(R.id.signoutButton);

        mAuth = FirebaseAuth.getInstance();


        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null){

            signOut();
        }
    }

    private void signOut(){
        //clear predefs

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();

    }
}
