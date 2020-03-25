package com.example.accesscam.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesscam.Models.ImageUrl;
import com.example.accesscam.Network.NetworkClient;
import com.example.accesscam.R;
import com.example.accesscam.Services.GoogleImgsService;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class DownloadFragment extends Fragment implements TextWatcher {
    RecyclerView downloadRecyclerView;
    EditText searchText;
    ArrayList<ImageUrl> downloadList;
    DownloadListener listener;
    Retrofit retrofit;
    GoogleImgsService service;
    private static final String ARG_TEXT="argText";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_download,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //EditText queries google
        searchText = getActivity().findViewById(R.id.searchtext);
        searchText.addTextChangedListener(this);
        //RecyclerView
        downloadRecyclerView = getActivity().findViewById(R.id.download_recyclerview);
        downloadRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            wait(15);
        }catch (Exception exception){Log.e("Wait",exception.getMessage());}
        listener.searchQuery(downloadRecyclerView,s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //interface that carries methods to be called from another fragments to contact this context
    public interface DownloadListener{
        void searchQuery(RecyclerView rv, String s);
    }

        //Creating a new instance of the fragment with needed updates on default constructor
        public static DownloadFragment newInstance(String text){
            DownloadFragment fragment=new DownloadFragment();
            Bundle args =new Bundle();
            args.putString(ARG_TEXT,text);
            fragment.setArguments(args);
            return fragment;
        }

        //Create Instance of Interface that contacts activity
        @Override
        public void onAttach(Context context){
            super.onAttach(context);
            if(context instanceof com.example.accesscam.Fragments.DownloadFragment.DownloadListener)
                listener = (DownloadListener) context;
            else
                throw new RuntimeException(context.toString()+" must implement NoteListener");
        }
        //Set free listener when it is not needed (Another fragment replace the fragment)
        @Override
        public void onDetach(){
            super.onDetach();
            listener=null;
        }
}
