package com.example.accesscam.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.accesscam.Activities.NewNoteActivity;
import com.example.accesscam.Models.GoogleImgs;
import com.example.accesscam.R;

import java.util.List;

public class ColorsRVAdapter extends RecyclerView.Adapter<ColorsRVAdapter.ColorItemViewHolder>{
        private Context mcontext;
        private int[] items;
        //private String[] cnames;


    public ColorsRVAdapter(Context mcontext) {
        this.mcontext =mcontext;
        items = mcontext.getResources().getIntArray(R.array.colorvalue);
        //cnames = mcontext.getResources().getStringArray(R.array.colorname);
    }

    @NonNull
        @Override
        public ColorItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list, viewGroup,
                    false);
            return new ColorItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorItemViewHolder colorItemViewHolder, int position) {
            colorItemViewHolder.bind(items[position]);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public class ColorItemViewHolder extends RecyclerView.ViewHolder {
            private TextView colorName;
            private ImageView colorView;

            public ColorItemViewHolder(@NonNull View itemView) {
                super(itemView);
                this.colorName = itemView.findViewById(R.id.color_title);
                this.colorView = itemView.findViewById(R.id.color_thumbnail);
                           }

            public void bind(int item) {
                //colorName.setText(name);
                colorView.setBackgroundColor(item);
                Toast.makeText(mcontext,"Color is "+" of value "+ item,Toast.LENGTH_LONG).show();
                itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
    }

}
