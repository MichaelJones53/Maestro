package com.mikejones.maestro;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CreatePostActivity extends AppCompatActivity {

    private TextView mClassNameTextView;
    private TextInputEditText mPostTitleEditText;
    private TextInputEditText mPostTextEditText;

    private String mClassName;
    private String mClassId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Bundle extas = getIntent().getExtras();
        mClassName = extas.getString("className");
        mClassId = extas.getString("classId");

        mClassNameTextView = findViewById(R.id.createPostClassNameTextView);
        mPostTitleEditText = findViewById(R.id.postTitleEditText);
        mPostTextEditText = findViewById(R.id.postTextEditText);






        mClassNameTextView.setText(mClassName);

    }
}
