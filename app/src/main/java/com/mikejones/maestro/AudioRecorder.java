package com.mikejones.maestro;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AudioRecorder {

    private MediaRecorder recorder;
    public RecordingState recordingState;
    private String filePath;
    private MediaPlayer mediaPlayer;

    public AudioRecorder(){
        recorder = new MediaRecorder();
        recordingState = RecordingState.NEVER_STARTED;
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ UUID.randomUUID().toString()+".3gp";
        recorder.setOutputFile(filePath);

    }

    public void startRecording() throws IOException {
        recordingState = RecordingState.RECORDING;

        recorder.prepare();
        recorder.start();

    }

    public void stopRecording(){
        recordingState = RecordingState.STOPPED;
        recorder.stop();


    }
    public String getFilePath(){
        return filePath;
    }

    public boolean deleteFile(){
        File f = new File(filePath);
        return f.delete();
    }


    public void playRecording(final IAudioCompletable audioCompletion){
        recordingState = RecordingState.PLAYING;
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    recordingState = RecordingState.STOPPEDPLAYING;
                    audioCompletion.onAudioPlayCompleted();
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopPlaying(){
        recordingState = RecordingState.STOPPEDPLAYING;
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void releaseRecording(){
        recordingState = RecordingState.RELEASED;
        recorder.release();
    }


}
