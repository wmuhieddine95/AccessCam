package com.example.accesscam.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesscam.Adapters.ColorsRVAdapter;
import com.example.accesscam.Adapters.ImagesRVAdapter;
import com.example.accesscam.R;

public class SettingsFragment extends Fragment {
    public static int c =Color.WHITE;
    public static String f;
    public RecyclerView colorRecyclerView;
    public SettingListener listener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_settings,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        colorRecyclerView = getActivity().findViewById(R.id.color_recyclerview);
        colorRecyclerView.setLayoutManager(new LinearLayoutManager
                (getContext(), LinearLayoutManager.HORIZONTAL,false));
        listener.setAdapterColor(colorRecyclerView);
    }

    //Create Instance of Interface that contacts activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof SettingListener)
            listener = (SettingListener) context;
        else
            throw new RuntimeException(context.toString()+" must implement NoteListener");
    }
    //Set free listener when it is not needed (Another fragment replace the fragment)
    @Override
    public void onDetach(){
        super.onDetach();
        listener=null;
    }
    public interface SettingListener{
        void setAdapterColor(RecyclerView rv);
    }
}
