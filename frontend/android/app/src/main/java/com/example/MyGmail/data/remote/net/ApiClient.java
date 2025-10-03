package com.example.MyGmail.data.remote.net;
import android.content.Context;

import com.example.MyGmail.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// ApiClient.java
public class ApiClient {
    private static volatile Retrofit retrofit;
    private static volatile OkHttpClient client;
    private static volatile PersistentCookieJar cookieJar;
    private static volatile SessionManager sessionManager;

    public static Retrofit getClient(Context context) {
        if (retrofit != null) return retrofit;
        synchronized (ApiClient.class) {
            if (retrofit != null) return retrofit;

            Context app = context.getApplicationContext();
            sessionManager = SessionManager.getInstance(app);
            cookieJar = new PersistentCookieJar(app);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .addInterceptor(logging)
                    .addInterceptor(new AuthErrorInterceptor(sessionManager, cookieJar))
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static boolean hasSession(Context context) {
        getClient(context.getApplicationContext());
        return cookieJar.hasSession();
    }

    public static void clearCookies(Context context) {
        getClient(context.getApplicationContext());
        cookieJar.clear();
    }
}
