package com.mikejones.maestro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>  {

    private Context mContext;
    private ArrayList<Post> data;
    private String mClassId;

    public PostListAdapter(Context c, String classId, ArrayList<Post> d){
        data = d;
        mContext = c;
        mClassId = classId;
    }

    @NonNull
    @Override
    public PostListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.classroom_selector_layout,viewGroup, false);


        return new PostListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.postTitleTextView.setText(data.get(i).getPostTitle());
        viewHolder.postAuthorTextView.setText(data.get(i).getAuthorName().toUpperCase());
        viewHolder.postDateTextView.setText(data.get(i).getTimestamp());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PostActivity.class);
                intent.putExtra("classroomId", mClassId);
                intent.putExtra("postId", data.get(i).getPostId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView postTitleTextView;
        public TextView postAuthorTextView;
        public TextView postDateTextView;






        ViewHolder(View v){
            super(v);
            postAuthorTextView = v.findViewById(R.id.postListAuthorTextView);
            postDateTextView = v.findViewById(R.id.postListDateTextView);
            postTitleTextView = v.findViewById(R.id.postListTitleTextView);


        }
    }
}
