package com.mikejones.maestro;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecorderActivity extends AppCompatActivity {

    private TextView mTimeTextView;
    private ImageButton mRecordButton;
    private ImageView mSaveButton;
    private ImageView mDeleteButton;
    private  AudioRecorder recorder;
    private Handler timeHandler = new Handler();
    private Runnable timer;
    private long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        mTimeTextView = findViewById(R.id.timeTextView);
        mRecordButton = findViewById(R.id.recordImageButton);
        mSaveButton = findViewById(R.id.saveImageView);
        mDeleteButton = findViewById(R.id.deleteImageView);



        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkPermission()){
                    if(recorder == null){
                        try {
                            recorder = new AudioRecorder();
                            startTime = System.currentTimeMillis();
                            timer = new Runnable() {
                                @Override
                                public void run() {
                                    long millis = System.currentTimeMillis() - startTime;
                                    int seconds = (int) (millis / 1000);
                                    int minutes = seconds / 60;
                                    seconds = seconds % 60;

                                    mTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));

                                    timeHandler.postDelayed(this, 500);
                                }
                            };
                            timer.run();
                            recorder.startRecording();

                            //set image to square
                            mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                        } catch (IOException e) {
                            Toast.makeText(RecorderActivity.this, "Recording failed...: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else if(recorder.recordingState == RecordingState.RECORDING){
                        recorder.stopRecording();
                        recorder.releaseRecording();
                        timeHandler.removeCallbacks(timer);
                        showButtons();
                        //set image to play icon
                        mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    }else if(recorder.recordingState == RecordingState.PLAYING){

                        recorder.stopPlaying();
                        showButtons();
                        //set image to square
                        mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                    }else if(recorder.recordingState == RecordingState.STOPPEDPLAYING){
                        recorder.playRecording(new IAudioCompletable() {
                            @Override
                            public void onAudioPlayCompleted() {
                                mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                            }

                        });
                        //set image to square
                        mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    }else if(recorder.recordingState == RecordingState.STOPPED){
                        recorder.playRecording(new IAudioCompletable() {
                            @Override
                            public void onAudioPlayCompleted() {
                                mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                            }
                        });
                        //set image to play icon
                        mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    }else if(recorder.recordingState == RecordingState.RELEASED){
                        recorder.playRecording(new IAudioCompletable() {
                            @Override
                            public void onAudioPlayCompleted() {
                                mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                            }
                        });
                        //set image to play icon
                        mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    }
                }else{
                    requestPermission();
                }


            }
        });




        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("audioPath", recorder.getFilePath());
                setResult(Activity.RESULT_OK, i);
                finish();

            }
        });


        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtons();
                recorder.deleteFile();
                recorder = null;
                mTimeTextView.setText("00:00");
                mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone));
            }
        });

        hideButtons();
    }

    @Override
    protected void onStop() {
        super.onStop();

        timeHandler.removeCallbacks(timer);

        if(recorder != null){
            recorder.releaseRecording();
            mTimeTextView.setText("00:00");
            mRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_microphone));
            recorder = null;
        }
    }

    private void hideButtons(){
        mDeleteButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
    }

    private void showButtons(){
        mDeleteButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result2 =  ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecorderActivity.this, new
                String[]{RECORD_AUDIO, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 0);
    }
}
