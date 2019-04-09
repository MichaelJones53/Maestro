package com.mikejones.maestro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Post> topics;

    public ClassListAdapter(Context c, ArrayList<Post> d) {
        topics = d;
        mContext = c;
    }

        @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }




    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView postTitleTextView;
        public TextView postAuthorTextView;
        public TextView postDateTextView;

        ViewHolder(View v){
            super(v);
            postTitleTextView = v.findViewById(R.id.postTitleTextView);
            postAuthorTextView = v.findViewById(R.id.postAuthorTextView);
            postDateTextView = v.findViewById(R.id.postDateTextView);

        }
    }
}
