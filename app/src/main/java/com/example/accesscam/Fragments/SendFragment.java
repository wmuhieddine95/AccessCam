package com.example.accesscam.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accesscam.R;

public class SendFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_send,container,false);
    }

}
