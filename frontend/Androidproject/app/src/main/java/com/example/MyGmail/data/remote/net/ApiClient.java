package com.example.MyGmail.data.remote.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Singleton class to manage Retrofit instance for API calls.
 * - Configures OkHttpClient with cookie handling, timeouts, logging, and authentication.
 * - Provides a single Retrofit instance throughout the app.
 * - Must be initialized with application context before use.
 * - Includes method to clear cookies when needed.
 */
public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/api/"; // Localhost for Android emulator
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit != null) return retrofit;

        // Add logging for debugging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        SessionManager sessionManager = SessionManager.getInstance(context);
        PersistentCookieJar cookieJar = new PersistentCookieJar(context);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .addInterceptor(new AuthErrorInterceptor(sessionManager, cookieJar))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static boolean hasSession(Context context) {
        return new PersistentCookieJar(context).hasSession();
    }

    public static void clearCookies(Context context) {
        new PersistentCookieJar(context).clear();
    }
}
