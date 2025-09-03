package com.example.androidproject.api;

import android.content.Context;

import com.example.androidproject.model.EmailData;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * An OkHttp interceptor that can be used to add authentication headers to requests.
 * Currently, it does not modify the request but serves as a placeholder for future enhancements.
 */
public class AuthInterceptor implements Interceptor {
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        return chain.proceed(originalRequest);
    }

    public static interface EmailApiService {
        @GET("api/mails")
        Call<List<EmailData>> getMails();

        @GET("api/mails/search")
        Call<List<EmailData>> searchMails(@Query("q") String query);
    }
}
