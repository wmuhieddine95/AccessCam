package com.example.accesscam.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.accesscam.MainActivity;
import com.example.accesscam.Models.GetRes;
import com.example.accesscam.Models.Line;
import com.example.accesscam.Models.Lines;
import com.example.accesscam.Models.Note;
import com.example.accesscam.Models.Url;
import com.example.accesscam.Network.FilePath;
import com.example.accesscam.Network.NetworkClient;
import com.example.accesscam.R;
import com.example.accesscam.Services.ComputerVisionService;
import com.example.accesscam.Services.PostMsService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewNoteActivity extends AppCompatActivity {
EditText noteText;
EditText noteTitle;
Button cancelBtn;
Button okButton;
Button analyzeButton;
ProgressBar pb;
//Accessed through implemented Intents
boolean source= false;
boolean update= false;
boolean captured = false;
FirebaseDatabase db;
DatabaseReference ref;
//MS API
String imageUrl;
String outputFromMs;
String opLocationMs;
String TAG = "MSNET";
String opKey;
File capFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        outputFromMs = "Initialized Value";
        opLocationMs = "Initialized Value";
        noteTitle=findViewById(R.id.text_note);
        noteText=findViewById(R.id.title_note);
        okButton=findViewById(R.id.save_note);
        cancelBtn=findViewById(R.id.cancel_note);
        analyzeButton=findViewById(R.id.analyzeimage_note);
        pb=findViewById(R.id.pb);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Notes");
        Intent intent=getIntent();
        //Grid RecyclerView on Click
        if(intent.hasExtra("UPDATE")){
            source=true;
            update=true;
            noteTitle.setText(intent.getStringExtra("Title"));
            noteText.setText(intent.getStringExtra("Note"));
        }
        //Linear RecyclerView in Download Fragment
        else if(intent.hasExtra("DOWNLOAD")){
            source = true;
            analyzeButton.setVisibility(View.VISIBLE);
            imageUrl = intent.getStringExtra("MsResult");
            noteTitle.setText(intent.getStringExtra("GoogleNoteTitle"));
        }
        //Capturing an Image in Navigation Drawer
        else if(intent.hasExtra("CAPTURED"))
        {
           opKey = intent.getStringExtra("CAPTURED");
            captured = true;
           analyzeButton.setVisibility(View.VISIBLE);
           getTextRecognition(opKey);//new GetOpKeyForFile().execute(localImagePath);
            Toast.makeText(getApplicationContext(),
                   "Received: "+opKey,Toast.LENGTH_LONG).show();
        }
        //Note from draft
        else {
            source=false;
            Toast.makeText(this,"Don't forget to manipulate the AI Notes :D",Toast.LENGTH_LONG);
        }
        //Analyze in case of download
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!captured)
                    new GetOpKeyForImage().execute(imageUrl);
                else
                    //new GetOpKeyForFile().execute(localImagePath);
                    getTextRecognition(opKey);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=noteText.getText().toString().trim();
                String title=noteTitle.getText().toString().trim();
                //Update feature is not implemented
                if(!content.isEmpty()&& !title.isEmpty()) {
                    if (update) {
                        //Supposed to update and not to add
                        //Not implemented yet
                        //updateNoteOnDb(noteText.getText().toString(),noteTitle.getText().toString());
                        addNoteToDb(noteTitle.getText().toString(), noteText.getText().toString());
                        //finish();
                    } else {
                        //updateNoteOnDb(noteText.getText().toString(),noteTitle.getText().toString());
                        addNoteToDb(noteTitle.getText().toString(),noteText.getText().toString());
                        //finish();
                    }
                }
                else{
                    noteText.setError("Note is Empty");
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void addNoteToDb(String title, String note) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
        Date date = new Date();
        String lastModified=dateFormat.format(date);
        ref.child(UUID.randomUUID().toString()).setValue(new Note(title,note,lastModified));
    }

    void updateNoteOnDb(String title, String note) {

    }

    //Instant Database is used -> this is working but not used
    void addNoteToFireStore(String title, String note)
    {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        //SimpleDateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
        // Create a new user with a first and last name
        Map<String, String> newNote = new HashMap<>();
        newNote.put("title", title);
        newNote.put("last", note);
        newNote.put("lastModified", new Date().toString());

// Add a new document with a generated ID
        db.collection("Notes")
                .add(newNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SuccessAddNote", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FailAddNote", "Error adding document", e);
                    }
                });
    }

    //Recognize Text in Images Using Microsoft Cognitive Services
    private void getTextRecognition(String opKey){
        Retrofit retrofit= NetworkClient.getRetrofit();
        ComputerVisionService service2 = retrofit.create(ComputerVisionService.class);
        service2.get(opKey).enqueue(new Callback<GetRes>() {
            @Override
            public void onResponse(Call<GetRes> call, Response<GetRes> response) {
                if(response.isSuccessful())
                {
                    Log.d(TAG,"In getTextRecognition"+response.headers().toString()+" of code"+response.code());
                    try {
                        GetRes gs= response.body();
                        Lines ls = gs.getRecognitionResult();
                        List<Line> l = ls.getLines();
                        for(int i=0;i<l.size();i++)
                        {
                            CharSequence cs= l.get(i).getText();
                            Log.d(TAG,"word "+cs.toString());
                            noteText.append(cs);
                            outputFromMs = outputFromMs + cs.toString();
                        }
                        Toast.makeText(getApplicationContext(),outputFromMs,Toast.LENGTH_LONG);
                    }
                catch (Exception e){ Log.e("Error",response.headers().toString());
                    Toast.makeText(getApplicationContext(),"Something went wrong with the image",Toast.LENGTH_LONG);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),response.headers().toString(),Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<GetRes> call, Throwable t) {
             Toast.makeText(getApplicationContext(),
                     "Check Your Internet Connection",Toast.LENGTH_LONG);
            }
        });
        Log.d(TAG,"Sentence in image is: "+outputFromMs);
    }
    private String uploadImage(String urlPath) {
        Retrofit retrofit= NetworkClient.postRetrofit();
        PostMsService service1 = retrofit.create(PostMsService.class);
        Url url= new Url(urlPath);
        service1.setUrl(url)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                                Log.d(TAG, "Code is" +response.toString());
                                opLocationMs = response.headers().get("Operation-Location");
                                String operationId[] = opLocationMs.split("/");
                                if(response.code()==202)
                                    opLocationMs = operationId[operationId.length - 1];
                                else
                        Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_LONG);
                        }catch (Exception e){ Log.e("WhyNoResult", "Code is: "+response.code());
                            Toast.makeText(getApplicationContext(),"Request Failed",Toast.LENGTH_LONG);
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                     Toast.makeText(getApplicationContext(),"Check Your Internet Connection",Toast.LENGTH_LONG);
                        Log.e(TAG, "onFailure", t);
                    }
                });
        Log.d(TAG,"upload image is returning "+opLocationMs);
    return opLocationMs;
    }
    public String uploadBytes (File imagetobinary)
    {  Retrofit retrofit= NetworkClient.postRetrofit();
        PostMsService service1 = retrofit.create(PostMsService.class);
        try {
            Log.d(TAG, "BeforeCallingService");
            service1.setIm(RequestBody.create(MediaType.parse("application/octet"),imagetobinary))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()) {
                                try {
                                    Log.d(TAG, "onSuccessResponse "+response.headers().toString());
                                    opLocationMs = response.headers().get("Operation-Location");
                                    String operationId[] = opLocationMs.split("/");
                                    opLocationMs = operationId[operationId.length - 1];
                                    //getTextRecognition(opLocation);
                                } catch (Exception e) {
                                    Log.d(TAG, "CatchSuccessResponse "+response.headers().toString());
                                }
                            }
                            else{
                                Log.d(TAG, "Try Again "+response.code());
                                Log.d(TAG,"Response is: "+ response.message());
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e(TAG, "Check Internet Connection", t);
                        }
                    });
        }
        catch(Exception e){
            Log.d(TAG, "CatchBeforeCallingService");
        }
        return opLocationMs;
    }
    private class GetOpKeyForImage extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            int count= strings.length;
            String res="";
            for(int i=0;i<count;i++)
                res = uploadImage(strings[i]);
            Log.d(TAG,"In Background we got operation key: "+res);
            return res;
        }
        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String res) {
            Log.d(TAG,"In PostExecute with res: "+res);
            getTextRecognition(res);
            //new GetTextFromImage().execute(res);
            pb.setVisibility(View.INVISIBLE);
        }
    }
    private class GetOpKeyForFile extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... strings) {
            int count= strings.length;
            String res="";
            for(int i=0;i<count;i++){
                res = uploadBytes(new File(strings[i]));
            }
            Log.d(TAG,"In Background we got operation key: "+res);
            return res;
        }
        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onPostExecute(String res) {
            Log.d(TAG,"In PostExecute with res: "+res);
            getTextRecognition(res);
            pb.setVisibility(View.INVISIBLE);
        }
    }
}