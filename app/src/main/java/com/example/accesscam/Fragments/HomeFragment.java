package com.example.accesscam.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesscam.Models.Note;
import com.example.accesscam.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment implements View.OnClickListener ,ValueEventListener
{
    Button homeButton;
    NoteListener listener;

    RecyclerView homeRecyclerView;
    ArrayList<Note> list;
    //Database
    DatabaseReference reference;
    FirebaseFirestore db;
    private static final String ARG_TEXT="argText";

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        displayMessage(getContext(), "Getting your notes from Database");
        list=listener.getNote(dataSnapshot, homeRecyclerView);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        displayMessage(getContext(), "Failed to get more Data");
        listener.failedNote(databaseError);
    }

    //interface that carries methods to be called from another fragments to contact this context
    public interface NoteListener{
        void addNote();
        ArrayList<Note> getNote(DataSnapshot ds,RecyclerView homeRecyclerView);
        void failedNote(DatabaseError de);
    }
    //Creating a new instance of the fragment with needed updates on default constructor
    public static HomeFragment newInstance(String text){
        HomeFragment fragment=new HomeFragment();
        Bundle args =new Bundle();
        args.putString(ARG_TEXT,text);
        fragment.setArguments(args);
        return fragment;
    }

    //Create Instance of Interface that contacts activity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof NoteListener)
            listener = (NoteListener) context;
        else
            throw new RuntimeException(context.toString()+" must implement NoteListener");
    }
    //Set free listener when it is not needed (Another fragment replace the fragment)
    @Override
    public void onDetach(){
        super.onDetach();
        listener=null;
    }

    //method in order to receive data from fragments
    public void updateImageView(String newText){
      //  textView.setText(newText);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstance)
    {
        View v= inflater.inflate(R.layout.fragment_home,container,false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Button Navigate to New Note
        homeButton = getActivity().findViewById(R.id.home_addbtn);
        homeButton.setOnClickListener(this);

        //RecyclerView
        homeRecyclerView = getActivity().findViewById(R.id.home_recycler);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        //Database
        reference= FirebaseDatabase.getInstance().getReference().child("Notes");
        reference.addValueEventListener(this);
        /*db.collection("Notes").get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(),
                                    "Getting Your Notes",Toast.LENGTH_LONG);
                            for(QueryDocumentSnapshot document : task.getResult()){

                            }
                        }
                        else
                            Log.d("Failed","In getting Notes");

                    }
                });*/
    }

    //Use the instance of the interface on which should be implemented in activity
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.home_addbtn:
                listener.addNote();
                break;
            default:
                Log.d("Button Not Found", "Default in Switch");
                break;
        }
    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public void FillData(ArrayList<Note> list){
        for(int i=0;i<16;i++)
        {
            list.add(new Note("Title"+i,"This is a test note number"+i,new Date().toString()));
        }
    }

}
