package com.mikejones.maestro;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity implements ISignable {

    private static final String TAG = "SignupActivity";

    private TextInputEditText mSignupEmailEditText;
    private TextInputEditText mSignupPasswordEditText;
    private TextInputEditText mSignupConfirmPasswordEditText;
    private TextInputEditText mSignupNameEditText;
    private Spinner mSignupUserTypeSpinner;
    private Button mSignupRegisterButton;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        mSignupEmailEditText = findViewById(R.id.signupEmailEditText);
        mSignupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        mSignupConfirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        mSignupNameEditText = findViewById(R.id.signupNameEditText);
        mSignupUserTypeSpinner = findViewById(R.id.signupUserTypeSpinner);
        mSignupRegisterButton = findViewById(R.id.signupRegisterButton);
        mProgressBar = findViewById(R.id.signupProgressBar);

        //config spinner
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Student");
        spinnerArray.add("Educator");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerArray);
        mSignupUserTypeSpinner.setAdapter(adapter);
        mSignupUserTypeSpinner.setSelection(0);



        //config register button
        mSignupRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mSignupEmailEditText.getText().toString();
                final String password = mSignupPasswordEditText.getText().toString();
                final String confirmPassword = mSignupConfirmPasswordEditText.getText().toString();
                final String username = mSignupNameEditText.getText().toString();
                final String usertype = mSignupUserTypeSpinner.getSelectedItem().toString();

                if(!Validator.isValidEmail(email)){
                    showError("invalid email address");

                }else if(!Validator.isValidPassword(password)){
                    showError("password must be at least 6 characters");
                }else if(!Validator.isValidUsername(username)){
                    showError("username must be at least 6 characters");
                }else if(!password.equals(confirmPassword)){
                    showError("passwords do not match...");
                }else if(!Validator.isValidUserType(usertype)){
                    showError("select a proper user type...");
                }else{
                    signUpUser(email,password, username, usertype);
                }


            }
        });
    }


    private void signUpUser(final String email, String password, final String username, final String usertype){


        showSpinner();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideSpinner();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            DBManager.getInstance().createUser(email, username, usertype, SignupActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            showError("an error has occurred...");

                        }
                    }
                });

    }


    public void signIn(){
        String e = mSignupEmailEditText.getText().toString();
        String r = mSignupUserTypeSpinner.getSelectedItem().toString();
        String u = mSignupNameEditText.getText().toString();

        PrefManager pf = new PrefManager(getApplicationContext());
        pf.setAllPrefs(e, r, u);

        Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(i);
        finish();
    }
    public void failedSignIn(String message){
        showError("Error occured: "+message);
    }

    private void showError(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this);
        dialog.setTitle("MESSAGE");
        dialog.setMessage(message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int id) {
                di.cancel();
            }
        });
        AlertDialog d = dialog.create();
        d.show();
    }

    private void showSpinner(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSpinner(){
        mProgressBar.setVisibility(View.GONE);
    }


}
