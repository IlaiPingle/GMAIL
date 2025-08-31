package com.example.androidproject.api;

import android.content.Context;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import com.example.androidproject.BuildConfig;

import okhttp3.JavaNetCookieJar;
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
    private static final String BASE_URL_DEV = "http://10.0.2.2:8080/"; // Localhost for Android emulator
    // For physical device, use your computer's actual IP address:
//    private static final String BASE_URL_PROD = "http://192.168.1.100:8080/"; // Replace with your server's IP address
    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
           if (appContext == null) {
               throw new IllegalStateException("ApiClient is not initialized. Call ApiClient.initialize(context) before using getClient().");
           }

           CookieManager cookieManager = new CookieManager();
           cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

           // Add logging for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ?
                    HttpLoggingInterceptor.Level.BASIC :
                    HttpLoggingInterceptor.Level.NONE);
            logging.redactHeader("Authorization");

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ALL)))
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(appContext))
                    .addInterceptor(logging)
                    .authenticator(new TokenAuthenticator(appContext))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_DEV)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void clearCookies(Context context) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
        java.net.CookieHandler.setDefault(cookieManager);
    }
}
