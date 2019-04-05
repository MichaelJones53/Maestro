package com.mikejones.maestro;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity{

    private static final String TAG = "AccountActivity";

    private Button mSignoutButton;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mUsernameEditText;
    private TextInputEditText mUsertypeEditText;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mAuth = FirebaseAuth.getInstance();

        mSignoutButton = findViewById(R.id.signoutButton);
        mEmailEditText = findViewById(R.id.accountEmailEditText);
        mUsernameEditText = findViewById(R.id.accountNameEditText);
        mUsertypeEditText = findViewById(R.id.accountUserTypeEditText);
        mProgressBar = findViewById(R.id.accountProgressBar);
        showSpinner();
        updateUserData();
        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSpinner();
                mAuth.signOut();
                signOut();
            }
        });
    }


    private void signOut(){
        //clear predefs

        hideSpinner();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();

    }

    public void updateUserData(){
        Log.d(TAG, "updateUserData called");

        PrefManager pf = new PrefManager(getApplicationContext());
        mEmailEditText.setText(pf.getEmail());
        mUsertypeEditText.setText(pf.getRole());
        mUsernameEditText.setText(pf.getUsername());

        hideSpinner();

    }

    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
    }
}
