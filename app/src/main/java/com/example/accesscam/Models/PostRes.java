package com.example.accesscam.Models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

public class PostRes {
    @SerializedName("Operation-Location")
    private String operationLocation;

    public String getOperationLocation() {
        return operationLocation;
    }
}
