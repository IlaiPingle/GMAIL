package com.example.androidproject.data.local.db;

import android.content.Context;

import com.example.androidproject.data.remote.net.ApiClient;

public class MyApplication extends android.app.Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        ApiClient.initialize(context);
    }
}