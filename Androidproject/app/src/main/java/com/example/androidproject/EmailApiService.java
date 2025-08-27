package com.example.androidproject;

import com.example.androidproject.model.EmailData;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EmailApiService {
    @GET("api/mails")
    Call<List<EmailData>> getMails();

    @GET("api/mails/search")
    Call<List<EmailData>> searchMails(@Query("q") String query);
}