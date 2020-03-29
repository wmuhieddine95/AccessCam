package com.example.accesscam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesscam.Activities.NewNoteActivity;
import com.example.accesscam.Models.Note;
import com.example.accesscam.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context mcontext;
    private List<Note> mdata;

    public RecyclerViewAdapter(Context mcontext, List<Note> mdata) {
        this.mcontext = mcontext;
        this.mdata = mdata;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInFlater = LayoutInflater.from(mcontext);
        view = mInFlater.inflate(R.layout.note_inrecyclerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.note_title.setText(mdata.get(position).getTitle());
        holder.note_content.setText(mdata.get(position).getNote());
        holder.note_time.setText(mdata.get(position).getLastModified());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mcontext, NewNoteActivity.class);
                intent.putExtra("UPDATE",true);
                intent.putExtra("Title",mdata.get(position).getTitle());
                intent.putExtra("Note",mdata.get(position).getNote());
                intent.putExtra("LastModified",mdata.get(position).getLastModified());
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView note_content;
        TextView note_title;
        TextView note_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            note_content = itemView.findViewById(R.id.note_content);
            note_title = itemView.findViewById(R.id.note_title);
            note_time = itemView.findViewById(R.id.note_time);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
