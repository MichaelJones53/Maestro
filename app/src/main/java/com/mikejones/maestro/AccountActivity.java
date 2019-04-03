package com.mikejones.maestro;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AccountActivity extends AppCompatActivity implements IUpdateable {

    private static final String TAG = "AccountActivity";

    private Button mSignoutButton;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mUsernameEditText;
    private TextInputEditText mUsertypeEditText;

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
        DBManager.getInstance().getUser(mAuth.getUid(), AccountActivity.this);

        mAuth = FirebaseAuth.getInstance();


        mSignoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                signOut();
            }
        });
    }


    private void signOut(){
        //clear predefs

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();

    }

    public void updateUserData(User user){
        Log.d(TAG, "updateUserData called");
        mEmailEditText.setText(user.getEmail());
        mUsertypeEditText.setText(user.getRole());
        mUsernameEditText.setText(user.getUsername());

    }
}
