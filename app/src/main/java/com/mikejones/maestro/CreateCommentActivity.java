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

public class CreateCommentActivity extends AppCompatActivity {

    private TextView mClassNameTextView;
    private TextInputEditText mCommentTextEditText;
    private Button mAddCommentImageButton;
    private Button mAddCommentAudioButton;
    private Button mSubmitCommentButton;
    private ImageView mCommentImageHolder;

    private String mPostTitle;
    private String mClassId;
    private String mPostId;
    public final int cameraRequestCode = 20;
    public final int audioRequestCode = 30;

    private String audioFilePath = null;
    private Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        Bundle extras = getIntent().getExtras();
        mPostTitle = extras.getString("postTitle");
        mClassId = extras.getString("classroomId");
        mPostId = extras.getString("postId");


        mClassNameTextView = findViewById(R.id.createCommentClassNameTextView);
        mCommentTextEditText = findViewById(R.id.commentTextEditText);
        mAddCommentImageButton = findViewById(R.id.addCommentImageButton);
        mAddCommentAudioButton = findViewById(R.id.addCommentAudioButton);
        mSubmitCommentButton = findViewById(R.id.submitCommentButton);
        mCommentImageHolder = findViewById(R.id.commentImageView);

        mAddCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ci = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(ci, cameraRequestCode);
            }
        });

        mAddCommentAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateCommentActivity.this, RecorderActivity.class);
                startActivityForResult(i,audioRequestCode);
            }
        });


        mSubmitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCommentTextEditText.getText().toString().length() == 0
                        &&  audioFilePath == null
                        &&  imageBitmap == null
                ){
                    showError("Your comment must contain at least a message, image or audio contribution.");
                }else{

                    PrefManager pf = new PrefManager(CreateCommentActivity.this);
                    DBManager.getInstance().addNewComment(mClassId, mPostId, pf.getUsername(),
                            mCommentTextEditText.getText().toString(),
                            imageBitmap,
                            audioFilePath,
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


        mClassNameTextView.setText(mPostTitle);




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
            mCommentImageHolder.setImageBitmap(imageBitmap);
        }else if(audioRequestCode == requestCode && resultCode == RESULT_OK){

            audioFilePath = data.getStringExtra("audioPath");
        }
    }


    private void showError(String message){

        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateCommentActivity.this);
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
