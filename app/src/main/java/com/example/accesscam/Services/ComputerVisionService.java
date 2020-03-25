package com.example.accesscam.Services;

import com.example.accesscam.Models.GetRes;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ComputerVisionService{
    //application/octet-stream
    @Headers("ocp-apim-subscription-key: 06e4e3c675ce45b2ad28f0a5f381f222")
    @GET("vision/v2.0/textOperations/{operationId}")
    Call<GetRes> get(@Path("operationId") String opId);
}
