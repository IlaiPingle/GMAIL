package com.example.androidproject.data.remote.net;


import androidx.annotation.NonNull;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthErrorInterceptor implements Interceptor {
    private final SessionManager sessionManager;
    private final PersistentCookieJar cookieJar;

    public AuthErrorInterceptor(SessionManager sessionManager, PersistentCookieJar cookieJar) {
        this.sessionManager = sessionManager;
        this.cookieJar = cookieJar;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response res = chain.proceed(chain.request());
        if (res.code() == 401 || res.code() == 403) {
            cookieJar.clear();
            sessionManager.notifyLogout();
        }
        return res;
    }
}

