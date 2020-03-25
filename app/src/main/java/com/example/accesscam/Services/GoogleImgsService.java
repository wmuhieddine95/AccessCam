package com.example.accesscam.Services;

import com.example.accesscam.Models.GoogleImgsRes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleImgsService {

    @GET("search")
    Call<GoogleImgsRes> getImgs(@Query("q") String search,@Query("tbm") String type,@Query("Location") String c);//isch

    @GET("search?q={query}&tbm=isch")
    Call<GoogleImgsRes> getImgs(@Query("query") String query);

    @GET("search?q=apple&tbm=isch")
    Call<GoogleImgsRes> getImgs();

}
