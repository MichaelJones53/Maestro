package com.mikejones.maestro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements IUpdateable{

    private static final String TAG = "SigninActivity";

    private Button mSigninButton;
    private Button mSignupButton;
    private Button mForgotPasswordButton;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            signIn();
        }

        mSigninButton = findViewById(R.id.signinButton);
        mSignupButton = findViewById(R.id.signupButton);
        mForgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mProgressBar = findViewById(R.id.progressBar);

        //login button on click
        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailEditText.getText().toString();
                final String password = mPasswordEditText.getText().toString();

                if(!Validator.isValidEmail(email)){
                    showError("invalid email address");

                }else if(!Validator.isValidPassword(password)){
                    showError("password must be at least 6 characters");
                }else{
                    signInUser(email,password);
                }

            }
        });

        //Sign Up button on click
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });

        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = mEmailEditText.getText().toString();

                if(Validator.isValidEmail(email)){
                    showSpinner();
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            showError("an email was sent to "+email+" if account exists");
                            hideSpinner();
                        }
                    });
                }else{
                    showError("Enter a valid email address in the email field.");
                }
            }
        });


    }

    private void signInUser(final String email, final String password){
        showSpinner();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideSpinner();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            DBManager.getInstance().getUser(mAuth.getUid(), LoginActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            showError("login failed...");

                        }
                    }
                });
    }


    private void signIn(){
        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void showError(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void updateUserData(User user) {
        PrefManager pf = new PrefManager(getApplicationContext());
        pf.setAllPrefs(user.getEmail(), user.getRole(), user.getUsername());
        signIn();
    }
}
