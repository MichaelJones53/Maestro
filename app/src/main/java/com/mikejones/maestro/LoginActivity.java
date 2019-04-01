package com.mikejones.maestro;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button mSigninButton;
    private Button mSignupButton;
    private Button mForgotPasswordButton;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSigninButton = findViewById(R.id.signinButton);
        mSignupButton = findViewById(R.id.signupButton);
        mForgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);

        //Sign Up button on click
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });


    }
}
