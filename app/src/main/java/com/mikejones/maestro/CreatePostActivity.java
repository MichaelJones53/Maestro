package com.mikejones.maestro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CreatePostActivity extends AppCompatActivity {

    private TextView mClassNameTextView;
    private TextInputEditText mPostTitleEditText;
    private TextInputEditText mPostTextEditText;
    private Button mAddImageButton;
    private Button mAddAudioButton;
    private Button mSubmitButton;
    private ImageView mImageHolder;

    private String mClassName;
    private String mClassId;
    public final int cameraRequestCode = 20;
    public final int audioRequestCode = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        Bundle extras = getIntent().getExtras();
        mClassName = extras.getString("classroomName");
        mClassId = extras.getString("classroomId");

        mClassNameTextView = findViewById(R.id.createPostClassNameTextView);
        mPostTitleEditText = findViewById(R.id.postTitleEditText);
        mPostTextEditText = findViewById(R.id.postTextEditText);
        mAddImageButton = findViewById(R.id.addImageButton);
        mAddAudioButton = findViewById(R.id.addAudioButton);
        mSubmitButton = findViewById(R.id.submitPostButton);
        mImageHolder = findViewById(R.id.postImageView);

        mAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(ci, cameraRequestCode);
            }
        });

        mAddAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        mClassNameTextView.setText(mClassName);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case cameraRequestCode:
                break;
            case audioRequestCode:
                break;
            default:
                break;


        }
        if(cameraRequestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            mImageHolder.setImageBitmap(bitmap);
        }
    }
}
