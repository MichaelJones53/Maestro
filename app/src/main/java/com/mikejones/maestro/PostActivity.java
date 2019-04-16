package com.mikejones.maestro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PostActivity extends AppCompatActivity {

    private TextView mPostTitleView;
    private TextView mAuthorTextView;
    private TextView mDateTextView;

    private TextView mPostContentTextView;
    private ImageView mPostImageView;
    private ImageButton mPostAudioImageButton;

    private ScrollView mPostScrollView;

    private RecyclerView mCommentRecyclerView;

    private FloatingActionButton mNewCommentFAB;

    private String mClassId = "";
    private String mPostId = "";

    private ArrayList<Post> mCommentList = new ArrayList<>();

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private RecordingState playerState = RecordingState.STOPPED;
    private File audFile = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post);

            Bundle extras = getIntent().getExtras();
            mClassId = extras.getString("classroomId");
            mPostId = extras.getString("postId");

            mPostScrollView = findViewById(R.id.postScrollView);

            mPostTitleView = findViewById(R.id.singlePostTitleTextView);
            mAuthorTextView = findViewById(R.id.singlePostAuthorTextView);
            mDateTextView = findViewById(R.id.singlePostDateTextView);

            mPostContentTextView = findViewById(R.id.singlePostContentTextView);
            mPostImageView = findViewById(R.id.singlePostImageImageView);
            mPostAudioImageButton = findViewById(R.id.singlePostAudioButton);

            mCommentRecyclerView = findViewById(R.id.commentRecyclerView);
            mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mCommentRecyclerView.setAdapter(new CommentListAdapter(this, mClassId, mPostId, mCommentList));


            mCommentRecyclerView.setNestedScrollingEnabled(false);

        mNewCommentFAB = findViewById(R.id.addCommentFAB);

            mNewCommentFAB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PostActivity.this, CreateCommentActivity.class);
                    i.putExtra("postTitle", mPostTitleView.getText().toString());
                    i.putExtra("classroomId", mClassId);
                    i.putExtra("postId", mPostId);
                    startActivityForResult(i, 12);
                }
            });


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

                        DBManager.getAsset(p.getAudioURL(), new DBManager.DataListener() {
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

                                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        playerState = RecordingState.STOPPED;
                                        mPostAudioImageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));

                                    }
                                });

                                mPostAudioImageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(playerState == RecordingState.STOPPED){
                                            try {

                                                mMediaPlayer.reset();

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

        updateCommentList();
    }

    private void updateCommentList(){
        DBManager.getInstance().getCommentsForPost(mPostId, mClassId, new DBManager.DataListener() {
            @Override
            public void onDataPrepared() {

            }

            @Override
            public void onDataSucceeded(Object o) {
                ArrayList<Post> comments = (ArrayList<Post>) o;
                for(Post c: comments){
                    boolean exists = false;
                    for(Post z: mCommentList){
                        if(z.getPostId().equals(c.getPostId())){
                            exists = true;
                        }
                    }
                    if(!exists){
                        mCommentList.add(c);
                    }
                }
                Collections.sort(mCommentList);
                mCommentRecyclerView.getAdapter().notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "SHould update now", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 12 && resultCode == RESULT_OK){
            //showSpinner();
            updateCommentList();
        }
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
