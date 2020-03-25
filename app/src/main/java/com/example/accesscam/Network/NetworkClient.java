package com.example.accesscam.Network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit;
    private static String BASE_URL= "https://francecentral.api.cognitive.microsoft.com";
    private static String READ_URL= "https://muhieddine.cognitiveservices.azure.com";
    private static String GIMGS_URL="https://serpapi.com";
    private static String IMGS_AWS="https://www.mediawiki.org";
    ///vision/v2.0/recognizeText?mode=Handwritten

    public static Retrofit readRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        retrofit = new Retrofit.Builder()
                    .baseUrl(READ_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                    .build();
        return retrofit;
    }

    public static Retrofit getRetrofit(){
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
         retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
            .build();
    return retrofit;
}

    public static Retrofit postRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                    .build();
        return retrofit;
    }

    //BaseUrl Movies Quotes
    public static Retrofit getGoogleRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(GIMGS_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                    .build();
            return retrofit;
    }
    public static Retrofit getAwsRetrofit(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(GIMGS_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                    .build();
        return retrofit;
    }
    //Retrofit
    //https://www.mediawiki.org/
}
