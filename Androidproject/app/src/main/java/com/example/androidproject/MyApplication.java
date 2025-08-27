package com.example.androidproject;

import android.app.Application;

import com.example.androidproject.api.ApiClient;

/**
 * Custom Application class to initialize global configurations.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.initialize(this);
    }
}
