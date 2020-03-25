package com.example.accesscam.Models;

import com.google.gson.annotations.SerializedName;

public class Url {
    private Integer id;
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public void setId (Integer i)
    {
        this.id= i;
    }
    public Integer getId(){return id;}

    public Url(String url) {
        this.url = url;
    }
}
