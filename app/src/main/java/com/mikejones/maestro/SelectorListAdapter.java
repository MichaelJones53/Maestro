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
        final int selection = i;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ClassroomActivity.class);
                intent.putExtra("className", data.get(selection).getClassName());
                intent.putExtra("classId", data.get(selection).getClassId());
                mContext.startActivity(intent);


            }
        });

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.classNameTextView.setText(data.get(i).getClassName().toUpperCase());
        viewHolder.professorNameTextView.setText(data.get(i).getProfessor().toUpperCase());
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView classNameTextView;
        public TextView professorNameTextView;

        ViewHolder(View v){
            super(v);
            classNameTextView = v.findViewById(R.id.classNameTextView);
            professorNameTextView = v.findViewById(R.id.professorNameTextView);



        }
    }


}
