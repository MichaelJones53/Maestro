package com.mikejones.maestro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectorListAdapter extends RecyclerView.Adapter<SelectorListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<UserClass> data;

    public SelectorListAdapter(Context c, ArrayList<UserClass> d){
        data = d;
        mContext = c;

    }

    @NonNull
    @Override
    public SelectorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.selector_layout,viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.dataTextView.setText(data.get(i).getClassName());
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dataTextView;

        ViewHolder(View v){
            super(v);
            dataTextView = v.findViewById(R.id.data);



        }
    }
}
