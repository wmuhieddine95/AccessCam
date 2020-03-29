package com.example.accesscam.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.accesscam.Adapters.ColorsRVAdapter;
import com.example.accesscam.Fragments.DownloadFragment;
import com.example.accesscam.Fragments.HomeFragment;
import com.example.accesscam.Fragments.SettingsFragment;
import com.example.accesscam.Models.GoogleImgs;
import com.example.accesscam.Models.GoogleImgsRes;
import com.example.accesscam.Models.Note;
import com.example.accesscam.Adapters.ImagesRVAdapter;
import com.example.accesscam.Network.NetworkClient;
import com.example.accesscam.Adapters.RecyclerViewAdapter;
import com.example.accesscam.R;
import com.example.accesscam.Services.GoogleImgsService;
import com.example.accesscam.Services.PostMsService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener/*,
        CaptureFragment.CaptureListener*/, HomeFragment.NoteListener, DownloadFragment.DownloadListener
, SettingsFragment.SettingListener {

    private static final String TAG = "DB";
    //Fragments
    private HomeFragment homeFragment;
    private DownloadFragment downloadFragment;
    private SettingsFragment settingsFragment;

    //Outcome
    private int MY_PERMISSIONS_REQUEST=10;
    FirebaseFirestore db;
    DatabaseReference reference;
    //RecyclerView
    public String capIm="";
    public String opLocation;
    public File capFile;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CAPTURE_IMAGE_REQUEST=100;
    private Retrofit retrofit;
    private DrawerLayout drawerLayout;
    //private int[] items;
    //private String[] cnames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Settings Values
        //items = getResources().getIntArray(R.array.colorvalue);
        //cnames = getResources().getStringArray(R.array.colorname);
        //Toast.makeText(getApplicationContext(),"First Colors is: "+items[0],Toast.LENGTH_LONG).show();
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}
                    ,MY_PERMISSIONS_REQUEST);
        }
            // Permission is not granted
            // Should we show an explanation?
        //Database
        db = FirebaseFirestore.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        //Navigation Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawerLayout_home);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,
                toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState==null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home_image);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.home_image:
                replaceFragment(new HomeFragment());
                break;
            case R.id.cam_image:
                onCapture();
                break;
            case R.id.gal_image:
                loadImageFromGallery();
                break;
            case R.id.url_image:
                replaceFragment(new DownloadFragment());
                break;
            case R.id.logout_image:
                logout();
                break;
            case R.id.settings_image:
                replaceFragment(new SettingsFragment());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace
                (R.id.fragment_home,fragment).commit();
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
    public void loadImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                //(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        //Permission to Read
        photoPickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //After Capturing Picture to New Note with Ms Operation Key
        if (requestCode==CAPTURE_IMAGE_REQUEST)
        {
            Toast.makeText(getApplicationContext(),"File To Use "+capFile,Toast.LENGTH_LONG);
            Log.d(TAG,"File To Use "+capFile);
            new GetOpKeyForCapImage().execute(capFile);
        }
        //After Uploading Image
        else if (resultCode == RESULT_OK) {
            try {
                //Uri imageUri = data.getData();
                //Copying file in gallery to the app directory in order to use it
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                capFile=inputStreamToFile(inputStream);
                Toast.makeText(Home.this,"CopiedFile Size: "+capFile.length(), Toast.LENGTH_LONG);
                //Convert from Uri to file (Android 10 doesn't give permission to files not created by the app itself)
                    //For this conversion Recommended to create FilePath Class
                    //Source: https://stackoverflow.com/questions/45520599/creating-file-from-uri/45520771
                    //capFile= new File(FilePath.getPath(getApplicationContext(),imageUri));
                new GetOpKeyForCapImage().execute(capFile);
                }
            catch (FileNotFoundException e){Log.e("GalIm", "File Not Found");} catch (IOException e) {
                Log.e("GalIm", "IO Error");;
            }
        }
        else {
            Toast.makeText(Home.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

        //Capture Navigation Menu
    public void onCapture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                capFile = createImageFile();
                displayMessage(getBaseContext(),capFile.getAbsolutePath());
                Log.i("InTry",capFile.getAbsolutePath());
                // Continue only if the File was successfully created
                if (capFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.accesscam.fileprovider",
                            capFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                }
            }catch (Exception ex) {
                // Error occurred while creating the File
                displayMessage(getBaseContext(),ex.getMessage().toString());
                Log.i("InCatch",ex.getMessage().toString());
            }
        }else
        {
            displayMessage(getBaseContext(),"Null");
        }
    }
    public String uploadBytes (File imagetobinary)
    {  retrofit= NetworkClient.postRetrofit();
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
            opLocation = response.headers().get("Operation-Location");
            String operationId[] = opLocation.split("/");
            opLocation = operationId[operationId.length - 1];
            //getTextRecognition(opLocation);
            } catch (Exception e) {
                Log.d(TAG, "CatchSuccessResponse "+response.headers().toString());
                }
            }
            else{
            Log.d(TAG, "Try Again "+response.code());
            Toast.makeText(getApplicationContext(),"WhyGal! "+
                    response.message(),Toast.LENGTH_LONG).show();
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
        return opLocation;
    }
    //Create a file in Provider that accesses the mobile app directory
    //In order to pass it to the camera intent and store info in it
    //Used in the method onCapture()
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        capIm = image.getAbsolutePath();
        Log.i("InCreateFile",image.getAbsolutePath());
        return image;
    }

    private class GetOpKeyForCapImage extends AsyncTask<File,Integer,String> {

        @Override
        protected void onPreExecute() {
    /*        long x=0;
            do {
                x=capFile.length();
                Log.d("DoPreExCam","Length of file is: "+x);
            }while(x==0); */
        }

        @Override
        protected String doInBackground(File... strings) {
            int count= strings.length;
            String res="";
            for(int i=0;i<count;i++)
               res = uploadBytes(strings[i]);
            return res;
        }
        @Override
        protected void onPostExecute(String res) {
            Log.d(TAG,"In Background we got operation key: " +res);
            Intent intent = new Intent(getApplicationContext(),NewNoteActivity.class);
            intent.putExtra("CAPTURED",res);
            startActivity(intent);
        }
    }


    //Home Fragment Listener
    @Override
    public void addNote() {
        Intent intent = new Intent(Home.this,NewNoteActivity.class);
        startActivity(intent);
    }

    @Override
    public ArrayList<Note> getNote(DataSnapshot dataSnapshot, RecyclerView homeRecyclerView) {
        ArrayList<Note> list= new ArrayList<Note>();
        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
        {
            Note n = dataSnapshot1.getValue(Note.class);
            list.add(n);
            //FillData(list);
            //for (Note s: list) { Log.d("Test", s.getNote()+" "+s.getTitle()); }
            }
        //Add Adapter
        Collections.sort(list);
        RecyclerViewAdapter rva= new RecyclerViewAdapter(this,list);
        homeRecyclerView.setAdapter(rva);
        return list;
    }

    @Override
    public void failedNote(DatabaseError de) {
        Toast.makeText(this,de.getMessage(),Toast.LENGTH_LONG);
    }

    //Settings
    @Override
    public void setAdapterColor(RecyclerView rv) {
        rv.setAdapter(new ColorsRVAdapter(getApplicationContext()));
    }

    //

    //Download Fragment Listener
    @Override
    public void searchQuery(RecyclerView rv, String s) {
        try {
            Retrofit retrofit = NetworkClient.getGoogleRetrofit();
            GoogleImgsService service = retrofit.create(GoogleImgsService.class);
            service.getImgs(s,"isch","France").enqueue(new Callback<GoogleImgsRes>() {

                @Override
                public void onResponse(@NonNull Call<GoogleImgsRes> call,
                                       @NonNull Response<GoogleImgsRes> response) {
                    Log.d(TAG, "onResponse");
                    if (response.isSuccessful()) {
                        try {
                            GoogleImgsRes googleImgsRes = response.body();
                            List<GoogleImgs> itemList = googleImgsRes.getImages_results();
                            rv.setAdapter(new ImagesRVAdapter(itemList));
                        }
                        catch (Exception e)
                        {
                         Log.e("SuccessError",response.message());
                        }

                    }
                }

                @Override
                public void onFailure(Call<GoogleImgsRes> call, Throwable t) {
                    Log.e("Error", "Check Internet Connection");
                    Toast.makeText(getApplicationContext(), "Check Internet Connection", Toast.LENGTH_LONG);
                }
            });
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_LONG);
        }
    }

    //Logout
    public void logout(){
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    //InputStream To Cache
    public File inputStreamToFile(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        is.read(buffer);

        File targetFile = createImageFile();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        return targetFile;
    }
 }