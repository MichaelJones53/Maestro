package com.mikejones.maestro;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
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

public class LoginActivity extends AppCompatActivity implements IUpdateable, SensorEventListener {

    private static final String TAG = "SigninActivity";

    private Button mSigninButton;
    private Button mSignupButton;
    private Button mForgotPasswordButton;
    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    private float x1;
    private float x2;
    private float x3;
    private boolean init;
    private static final float ERROR = (float) 7.0;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            signIn();
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
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


    @Override
    public void updateUserData(User user) {
        PrefManager pf = new PrefManager(getApplicationContext());
        pf.setAllPrefs(user.getEmail(), user.getRole(), user.getUsername());
        signIn();
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
//Get x,y and z values
        float x,y,z;
        x = e.values[0];
        y = e.values[1];
        z = e.values[2];


        if (!init) {
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {

            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);

            //Handling ACCELEROMETER Noise
            if (diffX < ERROR) {

                diffX = (float) 0.0;
            }
            if (diffY < ERROR) {
                diffY = (float) 0.0;
            }
            if (diffZ < ERROR) {

                diffZ = (float) 0.0;
            }


            x1 = x;
            x2 = y;
            x3 = z;


            //Horizontal Shake Detected!
            if (diffX > diffY) {

                TextInputEditText curFocus = (TextInputEditText)getCurrentFocus();
                if(curFocus != null){
                    curFocus.setText("");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
