package com.example.androidproject.data.remote.net;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionManager {
    private static volatile SessionManager instance;
    private final MutableLiveData<Boolean> logoutEvents = new MutableLiveData<>();

    private SessionManager(Context appCtx) {}

    public static SessionManager getInstance(Context appCtx) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) instance = new SessionManager(appCtx.getApplicationContext());
            }
        }
        return instance;
    }

    public void notifyLogout() { logoutEvents.postValue(true); }

    public LiveData<Boolean> observeLogout() { return logoutEvents; }
}
