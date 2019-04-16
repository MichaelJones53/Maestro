package com.mikejones.maestro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private RecordingState playerState = RecordingState.STOPPED;
    private File audFile = null;

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

                            mPostImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                                    bmp.getHeight(), false));
                        }
                    });

                }else{
                    mPostImageView.setVisibility(View.GONE);
                }

                //set audio
                if(p.getAudioURL() != null){

                    DBManager.getAsset(((Post) o).getAudioURL(), new DBManager.DataListener() {
                        @Override
                        public void onDataPrepared() {

                        }

                        @Override
                        public void onDataSucceeded(Object o) {
                           final byte[] aud  = (byte[]) o;

                            try {
                                audFile = File.createTempFile("postAudio", ".3gp", Environment.getExternalStorageDirectory());
                                audFile.deleteOnExit();
                                FileOutputStream fos = new FileOutputStream(audFile);
                                fos.write(aud);
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                            mPostAudioImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(playerState == RecordingState.STOPPED){
                                        try {
                                            // create temp file that will hold byte array


                                            // resetting mediaplayer instance to evade problems
                                            mMediaPlayer.reset();

                                            // In case you run into issues with threading consider new instance like:
                                            // MediaPlayer mediaPlayer = new MediaPlayer();

                                            // Tried passing path directly, but kept getting
                                            // "Prepare failed.: status=0x1"
                                            // so using file descriptor instead
                                            FileInputStream fis = new FileInputStream(audFile);
                                            mMediaPlayer.setDataSource(fis.getFD());


                                            mMediaPlayer.prepare();

                                            mMediaPlayer.start();
                                            mPostAudioImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                                            playerState = RecordingState.PLAYING;

                                        } catch (IOException ex) {
                                            String s = ex.toString();
                                            ex.printStackTrace();
                                        }
                                    }else{
                                        mMediaPlayer.stop();
                                        mPostAudioImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                                        playerState = RecordingState.STOPPED;
                                    }

                                }
                            });
                        }
                    });

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
