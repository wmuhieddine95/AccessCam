package com.example.accesscam.immersivereadersdk;

import androidx.annotation.Keep;

import java.util.concurrent.Callable;

@Keep
public class Options {

    public Callable<Void> onExit;
    public String uiLang;
    public Integer timeout;

    public Options(Callable<Void> exitCallback, String uiLang, Integer timeout) {
        this.onExit = exitCallback;
        this.uiLang = uiLang;
        this.timeout = timeout;
    }
}
