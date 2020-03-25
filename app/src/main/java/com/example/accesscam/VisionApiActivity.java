package com.example.accesscam;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accesscam.Models.GetRes;
import com.example.accesscam.Models.Line;
import com.example.accesscam.Models.Lines;
import com.example.accesscam.Network.NetworkClient;
import com.example.accesscam.Models.Url;
import com.example.accesscam.Services.ComputerVisionService;
import com.example.accesscam.Services.ImmersiveReaderService;
import com.example.accesscam.Services.PostMsService;

import java.io.File;
import java.util.List;

/*import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.LanguageCodes;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;*/

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VisionApiActivity<onActivityResult> extends AppCompatActivity {

    private static final String TAG = "VisionApiActivity";

    private boolean cameraSource=false;
    private File bimage;
    private String filePath = null;
    private byte[] imToApi;

    private ImageView imageView;
    private EditText editText;
    private Button buttonProceed;
    private TextView result;
    //private ProgressBar dialog;

    public static Retrofit retrofit;
    private String opLocation;

    private PostMsService service1;
    private ComputerVisionService service2;
    private ImmersiveReaderService service3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apivision);
        Bitmap myBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.ittakesalot);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        editText =findViewById(R.id.editText);
        buttonProceed=findViewById(R.id.buttonProceed);
        Intent intent=getIntent();
        if(getIntent().hasExtra("IMAGE_CONVERT")) {
            cameraSource=true;
            filePath = intent.getStringExtra("IMAGE_CONVERT");
            bimage = new File(filePath);
        }
        else if (getIntent().hasExtra("GALIM_CONVERT")) {
            try {
                filePath = intent.getStringExtra("GALIM_CONVERT");
            }
            catch (Exception e){
                Log.e("CATCH GET_EXTRA",e.toString());
            }
            //imToApi = intent.getByteArrayExtra("GALIM_CONVERT");
            //decode base64 string to image
            //Bitmap decodedImage = BitmapFactory.decodeByteArray(imToApi, 0, imToApi.length);
            //imageView.setImageBitmap(decodedImage);
        }
        else
            filePath = "https://www.howtogeek.com/wp-content/uploads/2019/03/sample-data.png";
        //if(filePath!=null)
        //{
         //   try {
                //myBitmap= Bitmap.createScaledBitmap(myBitmap,200,200,true);
                //imageView.setImageBitmap(myBitmap);
                //editText.setText(filePath);
                //ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
                //myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                //imToApi = byteArrayOutputStream.toByteArray();
                //ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
           // } catch (Exception e) {
           //     editText.setText(e.getMessage());
           // }

       // }

        //Process Image
        buttonProceed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try{
                    //getImmersiveReader();
                    uploadImage(filePath);
                    /*if(cameraSource)
                        uploadBytes(bimage);
                    else
                        uploadBytes(imToApi);*/
                    //getTextRecognition(opLocation);
                } catch (Exception e) {
                    editText.setText(e.toString());
                }
            }
        });
    }

    private void getImmersiveReader() {
        Retrofit retrofit= NetworkClient.readRetrofit();
        service3 = retrofit.create(ImmersiveReaderService.class);
        service3.getIR().enqueue(new Callback<GetRes>() {
            @Override
            public void onResponse(Call<GetRes> call, Response<GetRes> response) {
                if(response.isSuccessful())
                {
                    Toast toast = Toast.makeText(getApplicationContext(),"Response Code is: "+response.code(),Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                    editText.setText("Fix needed, "+ call.request().toString());
            }

            @Override
            public void onFailure(Call<GetRes> call, Throwable t) {
                editText.setText("F");
            }
        });
    }

    private void getTextRecognition(String opKey){
        Retrofit retrofit= NetworkClient.getRetrofit();
        service2 = retrofit.create(ComputerVisionService.class);
        service2.get(opKey).enqueue(new Callback<GetRes>() {
            @Override
            public void onResponse(Call<GetRes> call, Response<GetRes> response) {
                if(response.isSuccessful())
                {
                    GetRes gs= response.body();
                    Lines ls = gs.getRecognitionResult();
                    List<Line> l = ls.getLines();
                    for(int i=0;i<l.size();i++)
                    {
                        CharSequence cs= l.get(i).getText();
                        editText.append(cs);
                    }
                }
                else
                    editText.setText("Fix needed, "+ call.request().toString());
            }

            @Override
            public void onFailure(Call<GetRes> call, Throwable t) {
                editText.setText("F");
            }
        });
    }

    private void uploadImage(String filePath) {
        //File file = new File(filePath);
        retrofit= NetworkClient.postRetrofit();
        service1 = retrofit.create(PostMsService.class);
        Url url= new Url("https://www.howtogeek.com/wp-content/uploads/2019/03/sample-data.png");
        try {
            service1.setUrl(url)
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        Log.d(TAG, response.message());
                        opLocation=response.headers().get("Operation-Location");
                        String operationId[]=opLocation.split("/");
                        opLocation=operationId[operationId.length-1];
                        //getTextRecognition(opLocation);
                        editText.setText(opLocation);
                    }catch (Exception e){
                        editText.setText(response.message());
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    editText.setText("Check Your Internet Connection");
                    Log.e(TAG, "onFailure", t);
                }
            });
        }
        catch(Exception e){editText.setText(e.toString());}
        }

    public void uploadBytes(byte[] binaryIm)
    {
            retrofit= NetworkClient.postRetrofit();
            service1 = retrofit.create(PostMsService.class);
            try {
                Log.d(TAG, "BeforeCallingService");
                service1.setIm(RequestBody.create(MediaType.parse("application/octet"), binaryIm))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()) {
                                    try {
                                        Log.d(TAG, "onSuccessResponse");
                                        opLocation = response.headers().get("Operation-Location");
                                        String operationId[] = opLocation.split("/");
                                        opLocation = operationId[operationId.length - 1];
                                        editText.setText(opLocation);
                                        //getTextRecognition(opLocation);
                                    } catch (Exception e) {
                                        editText.setText(response.body().toString());
                                        Log.d(TAG, "CatchSuccessResponse");
                                    }
                                }
                                else{
                                    editText.setText(response.body().toString());
                                    Log.d(TAG, "CatchSuccessResponse");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                editText.setText("Check Your Internet Connection");
                                Log.e(TAG, "onFailure", t);
                            }
                        });
            }
            catch (Exception e)
            {
                editText.setText(e.toString());
                Log.d(TAG, "CatchBeforeCallingService");
            }
            }

    public void uploadBytes (File imagetobinary)
    {  retrofit= NetworkClient.postRetrofit();
            service1 = retrofit.create(PostMsService.class);
            try {
                Log.d(TAG, "BeforeCallingService");
                service1.setIm(RequestBody.create(MediaType.parse("application/octet"),imagetobinary))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()) {
                                    try {
                                        Log.d(TAG, "onSuccessResponse");
                                        opLocation = response.headers().get("Operation-Location");
                                        String operationId[] = opLocation.split("/");
                                        opLocation = operationId[operationId.length - 1];
                                        editText.setText(opLocation);
                                        //getTextRecognition(opLocation);
                                    } catch (Exception e) {
                                        editText.setText(response.body().toString());
                                        Log.d(TAG, "CatchSuccessResponse");
                                    }
                                }
                                else{
                                    editText.setText(response.code());
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                editText.setText("Check Your Internet Connection");
                                Log.e(TAG, "onFailure", t);
                            }
                        });
            }
            catch(Exception e){editText.setText(e.toString());
                Log.d(TAG, "CatchBeforeCallingService");
            }
            /*
            service1.setUrl(imToApi).enqueue(new Callback<PostRes>() {
                @Override
                public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                    Log.d(TAG, "onResponse");
                    if (response.isSuccessful()) {
                        //PostRes postRes = response.body();
                        //editText.setText(postRes.getOperationLocation());
                        editText.setText(response.code());
                    }
                    else{
                        Log.d(TAG, "no idea what is going on");
                        //editText.setText(call.request().toString());
                        editText.setText(response.code());
                        }
                }

                @Override
                public void onFailure(Call<PostRes> call, Throwable t) {
                    editText.setText(call.request().body().toString());
                    Log.e(TAG, "onFailure", t);
                }
            });*/
        }

    }