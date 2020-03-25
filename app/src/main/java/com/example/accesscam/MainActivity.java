package com.example.accesscam;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String GALIM_CONVERT= "GALIM_CONVERT";
    public static final String IMAGE_CONVERT= "IMAGE_CONVERT";
    public String galIm="";
    public byte[] imageBytes;
    public String capIm="";
    public File capFile;
    private static int RESULT_LOAD_IMG = 1;
    private static final int CAPTURE_IMAGE_REQUEST=100;

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView=findViewById(R.id.imgView);
        //Drawable d = Drawable.createFromPath("/home/wael/Desktop/MobileDev/AccessCam/app/src/main/res/drawable/itakesalot.jpg");
        //imgView.setImageDrawable(d);
        findViewById(R.id.btnNav2Capture2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this,PrimaryActivity.class));
                captureImage();
            }
        });
    }

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

    private void captureImage() {
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
            } catch (Exception ex) {
                // Error occurred while creating the File
                displayMessage(getBaseContext(),ex.getMessage().toString());
                Log.i("InCatch",ex.getMessage().toString());
            }
        }else
            {
                displayMessage(getBaseContext(),"Nullll");
            }
        //Previous Version
            //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
    }

    public void loadImageFromGallery(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAPTURE_IMAGE_REQUEST)
        {
            //Previous Version
                /*Bundle bundle =data.getExtras();
                Bitmap imageBitmap=(Bitmap)bundle.get("data");
                imgView.setImageBitmap(imageBitmap);*/
            Bitmap myBitmap = BitmapFactory.decodeFile(capFile.getAbsolutePath());
            imgView.setImageBitmap(myBitmap);
            Intent intent = new Intent(MainActivity.this,VisionApiActivity.class);
            intent.putExtra(IMAGE_CONVERT,capIm);
            startActivity(intent);
        }
        else if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Toast.makeText(MainActivity.this, imageUri.getPath(), Toast.LENGTH_LONG).show();
                try {
                    Bitmap bitmap =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    //imgView.setImageBitmap(bitmap);
                //encode image to base64 string
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageBytes  = baos.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                //decode base64 string to image
                    imageBytes = Base64.decode(imageString, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imgView.setImageBitmap(decodedImage);
                    //galIm = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    Intent intent = new Intent(MainActivity.this,VisionApiActivity.class);
                    intent.putExtra(GALIM_CONVERT,imageString);
                    startActivity(intent);
                } catch (IOException e) {
                e.printStackTrace();
                Log.e("TryUploadGal",e.toString());
            }
            }
        else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
        private void displayMessage(Context context, String message)
        {
                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
}
