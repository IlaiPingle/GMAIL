package com.example.MyGmail.data.remote.net;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionManager {
    private static volatile SessionManager instance;
    private final MutableLiveData<Event<Boolean>> logoutEvents = new MutableLiveData<>();

    private SessionManager(Context appCtx) {}

    public static SessionManager getInstance(Context appCtx) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) instance = new SessionManager(appCtx.getApplicationContext());
            }
        }
        return instance;
    }

    public LiveData<Event<Boolean>> observeLogout() {
        return logoutEvents;
    }

    public void notifyLogout() {
        Log.i("SessionManager", "Notifying global logout");
        logoutEvents.postValue(new Event<>(true));
    }
    public class Event<T> {
        private final T content;
        private boolean hasBeenHandled = false;
        public Event(T content) { this.content = content; }
        public T getContentIfNotHandled() {
            if (hasBeenHandled) return null;
            hasBeenHandled = true;
            return content;
        }
    }
}
