package com.example.accesscam.Services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ImmersiveReaderService {
    @Headers("key: 06e4e3c675ce45b2ad28f0a5f381f222")
    @GET()
    Call getIR();
}
