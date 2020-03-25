package com.example.accesscam.Services;

import com.example.accesscam.Models.Url;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface PostMsService {
    @Headers({"content-type: application/json","ocp-apim-subscription-key: 06e4e3c675ce45b2ad28f0a5f381f222"})
    @POST("vision/v2.0/recognizeText?mode=Printed")
    Call<ResponseBody> setUrl(@Body Url url);


    @Headers({"content-type: application/json","ocp-apim-subscription-key: 06e4e3c675ce45b2ad28f0a5f381f222"})
    @POST("vision/v2.0/recognizeText?mode=Printed")
    Call<ResponseBody> setImUrl(@Body String url);

    @Headers({"content-type: application/octet-stream","ocp-apim-subscription-key: 06e4e3c675ce45b2ad28f0a5f381f222"})
    @POST("vision/v2.0/recognizeText?mode=Printed")
    Call<ResponseBody> setIm(@Body RequestBody im);
}
