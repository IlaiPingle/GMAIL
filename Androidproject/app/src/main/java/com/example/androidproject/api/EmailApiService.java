package com.example.androidproject.api;

import com.example.androidproject.model.EmailData;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Query;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import java.util.Map;

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
    @POST("api/mails/{id}")
    Call<EmailData> sendMail(@Path("id") String draftId, @Body Map<String, Object> body);
    @POST("api/mails")
    Call<EmailData> createDraft(@Body Map<String, Object> body);
    @DELETE("api/mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);
    @DELETE("api/tokens")
    Call<Void> logout();
    @GET("api/mails/{id}")
    Call<EmailData> getMailById(@Path("id") String mailId);
    @PATCH("api/mails/{id}")
    Call<Void> updateMail(@Path("id") String mailId, @Body Map<String, Object> body);
}