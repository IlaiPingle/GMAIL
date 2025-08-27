package com.example.androidproject.api;

import com.example.androidproject.model.EmailData;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit service interface for email API endpoints.
 * Defines methods to fetch emails, filter by label/category, and search.
 */
public interface EmailApiService {
    @GET("api/mails")
    Call<List<EmailData>> getMails();
    @GET("api/mails")
    Call<List<EmailData>> getMailsByLabel(@Query("label") String label);
    @GET("api/mails")
    Call<List<EmailData>> getMails(@Query("category") String category);

    @GET("api/mails/search")
    Call<List<EmailData>> searchMails(@Query("q") String query);
}