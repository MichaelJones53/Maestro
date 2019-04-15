package com.mikejones.maestro;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private String audioFilePath = null;
    private Bitmap imageBitmap = null;

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
                Intent i = new Intent(CreatePostActivity.this, RecorderActivity.class);
                startActivityForResult(i,audioRequestCode);

            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mPostTitleEditText.getText().toString().length() == 0){

                    showError("Your post must have a title.");
                }else if(mPostTextEditText.getText().toString().length() == 0
                    &&  audioFilePath == null
                    &&  imageBitmap == null
                ){
                    showError("Your post must contain at least a message, image or audio contribution.");


                }else{

                    PrefManager pf = new PrefManager(CreatePostActivity.this);
                    DBManager.getInstance().addNewPost(mClassId, pf.getUsername(),
                            mPostTitleEditText.getText().toString(),
                            mPostTextEditText.getText().toString(),
                            imageBitmap, audioFilePath,
                            new DBManager.DataListener() {
                                @Override
                                public void onDataPrepared() {

                                }

                                @Override
                                public void onDataSucceeded(Object o) {
                                    setResult(RESULT_OK, getIntent());
                                    finish();
                                }
                            });

                }



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
            imageBitmap = (Bitmap)data.getExtras().get("data");
            mImageHolder.setImageBitmap(imageBitmap);
        }else if(audioRequestCode == requestCode && resultCode == RESULT_OK){

            audioFilePath = data.getStringExtra("audioPath");
        }
    }


    private void showError(String message){

        AlertDialog.Builder dialog = new AlertDialog.Builder(CreatePostActivity.this);
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
}
