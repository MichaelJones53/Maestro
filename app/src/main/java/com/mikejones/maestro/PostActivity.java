package com.mikejones.maestro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PostActivity extends AppCompatActivity {

    private TextView mPostTitleView;
    private TextView mAuthorTextView;
    private TextView mDateTextView;

    private TextView mPostContentTextView;
    private ImageView mPostImageView;
    private ImageButton mPostAudioImageButton;

    private FloatingActionButton mNewCommentFAB;

    private String mClassId = "";
    private String mPostId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Bundle extras = getIntent().getExtras();
        mClassId = extras.getString("classroomId");
        mPostId = extras.getString("postId");


        mPostTitleView = findViewById(R.id.singlePostTitleTextView);
        mAuthorTextView = findViewById(R.id.singlePostAuthorTextView);
        mDateTextView = findViewById(R.id.singlePostDateTextView);

        mPostContentTextView = findViewById(R.id.singlePostContentTextView);
        mPostImageView = findViewById(R.id.singlePostImageImageView);
        mPostAudioImageButton = findViewById(R.id.singlePostAudioButton);

        mNewCommentFAB = findViewById(R.id.addCommentFAB);


        DBManager.getInstance().getPost(mClassId, mPostId, new DBManager.DataListener() {
            @Override
            public void onDataPrepared() {

            }

            @Override
            public void onDataSucceeded(Object o) {

                Post p = (Post) o;
                mPostTitleView.setText(p.getPostTitle());
                mAuthorTextView.setText(p.getAuthorName());
                mDateTextView.setText(p.getTimestamp());

                //set text
                if(p.getText() != null){
                    mPostContentTextView.setText(p.getText());
                }else{
                    mPostContentTextView.setVisibility(View.GONE);
                }

                //set image
                if(p.getImageURL() != null){
                    DBManager.getAsset(((Post) o).getImageURL(), new DBManager.DataListener() {
                        @Override
                        public void onDataPrepared() {

                        }

                        @Override
                        public void onDataSucceeded(Object o) {
                            byte[] img  = (byte[]) o;
                            Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);

                            mPostImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, mPostImageView.getWidth(),
                                    mPostImageView.getHeight(), false));
                        }
                    });

                }else{
                    mPostImageView.setVisibility(View.GONE);
                }

                //set audio
                if(p.getAudioURL() != null){

                }else{
                    mPostAudioImageButton.setVisibility(View.GONE);
                }
            }
        });
    }









    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.accountButton) {
            // do something here
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
