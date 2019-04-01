package com.mikejones.maestro;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText mEmailEditText;
    private TextInputEditText mPasswordEditText;
    private TextInputEditText mConfirmPasswordEditText;
    private TextInputEditText mNameEditText;
    private Spinner mUserTypeSpinner;
    private Button mRegisterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEmailEditText = findViewById(R.id.signupEmailEditText);
        mPasswordEditText = findViewById(R.id.signupPasswordEditText);
        mConfirmPasswordEditText = findViewById(R.id.signupConfirmPasswordEditText);
        mNameEditText = findViewById(R.id.signupNameEditText);
        mUserTypeSpinner = findViewById(R.id.signupUserTypeSpinner);
        mRegisterButton = findViewById(R.id.signupRegisterButton);

        //config spinner
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Student");
        spinnerArray.add("Educator");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerArray);
        mUserTypeSpinner.setAdapter(adapter);
        mUserTypeSpinner.setSelection(0);



        //config register button
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
    }
}
