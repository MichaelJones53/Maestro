package com.mikejones.maestro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder>  {

    private Context mContext;
    private ArrayList<Post> data;
    private String mClassId;
    private String mPostId;
    private HashMap<Integer, File> fileMap = new HashMap<>();

    public CommentListAdapter(Context c, String classId, String postId, ArrayList<Post> d){
        data = d;
        mContext = c;
        mClassId = classId;
        mPostId = postId;
    }

    @NonNull
    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.comment_selector_layout,viewGroup, false);


        return new CommentListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentListAdapter.ViewHolder viewHolder, final int i) {
        viewHolder.commentContentTextView.setText(data.get(i).getText());
        if(data.get(i).getImageURL() != null){
            Toast.makeText(mContext, "called", Toast.LENGTH_SHORT).show();

            DBManager.getAsset(data.get(i).getImageURL(), new DBManager.DataListener() {
                @Override
                public void onDataPrepared() {

                }

                @Override
                public void onDataSucceeded(Object o) {
                    byte[] img  = (byte[]) o;
                    Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
                    Toast.makeText(mContext, "set image called", Toast.LENGTH_SHORT).show();
                    viewHolder.commentImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                            bmp.getHeight(), false));
                    viewHolder.commentImageView.setVisibility(View.VISIBLE);
                    viewHolder.commentImageView.invalidate();
                }
            });
        }else{
            viewHolder.commentImageView.setVisibility(View.GONE);
        }
//        //set audio
//        if(data.get(i).getAudioURL() != null){
//
//            DBManager.getAsset(data.get(i).getAudioURL(), new DBManager.DataListener() {
//                @Override
//                public void onDataPrepared() {
//
//                }
//
//                @Override
//                public void onDataSucceeded(Object o) {
//                    final byte[] aud  = (byte[]) o;
//                    final MediaPlayer mp = new MediaPlayer();
//                    File audFile;
//                    try {
//                        audFile = File.createTempFile(UUID.randomUUID().toString(), ".3gp", Environment.getExternalStorageDirectory());
//                        audFile.deleteOnExit();
//                        FileOutputStream fos = new FileOutputStream(audFile);
//                        fos.write(aud);
//                        fos.close();
//                        fileMap.put(i, audFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        @Override
//                        public void onCompletion(MediaPlayer mediaPlayer) {
//                            viewHolder.commentAudioImageButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play));
//
//                        }
//                    });
//
//                    viewHolder.commentAudioImageButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                                try {
//                                    mp.reset();
//                                    FileInputStream fis = new FileInputStream(fileMap.get(i));
//                                    mp.setDataSource(fis.getFD());
//
//                                    mp.prepare();
//
//                                    mp.start();
//                                    viewHolder.commentAudioImageButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_stop));
//
//                                } catch (IOException ex) {
//                                    String s = ex.toString();
//                                    ex.printStackTrace();
//                                }
//                            }
//
//
//                    });
//                }
//            });
//
//        }else{
//            viewHolder.commentAudioImageButton.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView commentContentTextView;
        public ImageView commentImageView;
        public ImageButton commentAudioImageButton;






        ViewHolder(View v){
            super(v);
            commentContentTextView = v.findViewById(R.id.singleCommentContentTextView);
            commentImageView = v.findViewById(R.id.singleCommentImageImageView);
            commentAudioImageButton = v.findViewById(R.id.singleCommentAudioButton);


        }
    }
}
