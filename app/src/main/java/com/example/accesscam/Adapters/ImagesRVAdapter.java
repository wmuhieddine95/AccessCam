package com.example.accesscam.Adapters;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesscam.Activities.NewNoteActivity;
import com.example.accesscam.Models.GoogleImgs;
import com.example.accesscam.R;

import java.util.List;

public class ImagesRVAdapter extends RecyclerView.Adapter<ImagesRVAdapter.SearchItemViewHolder>{
    //private Context mcontext;
    private final List<GoogleImgs> items;

    public ImagesRVAdapter(List<GoogleImgs> items) {
     //   this.mcontext =mContext;
        this.items = items;
    }

    @NonNull
    @Override
    public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_list, viewGroup, false);
        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemViewHolder searchItemViewHolder, int position) {
        searchItemViewHolder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class SearchItemViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView thumbnail;

        public SearchItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
        }
        public void bind(final GoogleImgs googleImgs) {

            if(googleImgs.getOriginal().endsWith(".jpg") || googleImgs.getOriginal().endsWith(".jpeg"))
            {
                title.setText(googleImgs.getSource());
                Glide.with(itemView).load(googleImgs.getThumbnail()).into(thumbnail);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        //New Note
                        Intent intent= new Intent(v.getContext(), NewNoteActivity.class);
                        intent.putExtra("DOWNLOAD",true);
                        intent.putExtra("MsResult",googleImgs.getOriginal());
                        intent.putExtra("GoogleNoteTitle",googleImgs.getTitle());
                        v.getContext().startActivity(intent);
                        //Test VisionApi
                        /*Intent intent2= new Intent(v.getContext(), VisionApiActivity.class);
                        intent.putExtra("DOWNLOAD",true);
                        intent.putExtra("MsResult",googleImgs.getOriginal());
                        intent.putExtra("GoogleNoteTitle",googleImgs.getTitle());
                        v.getContext().startActivity(intent2);*/

                    }catch (Exception e){
                        Log.e("OnClickSearch",e.getMessage());}
                    }
            });
        }
    }

}
