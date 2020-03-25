package com.example.accesscam.Models;

import com.example.accesscam.VisionApiActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorPost {
    public static Error parseError(Response<?> response){
        Converter<ResponseBody,Error> converter = VisionApiActivity.retrofit.responseBodyConverter
                (Error.class,new Annotation[0]);

        Error error;
        try
        {
            error = converter.convert(response.errorBody());

        } catch (IOException e) {
            return new Error();
        }
        return error;
    }
}