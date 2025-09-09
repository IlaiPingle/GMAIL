package com.example.androidproject.data.remote.api;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.models.Mail;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {
    //    mails endpoints
    @GET("mails")
    Call<List<Mail>> getMails();

    @POST("mails")
    Call<Mail> createDraft(@Body Mail mail);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String mailId);

    @PATCH("mails/{id}")
    Call<Void> updateMail(@Path("id") String mailId, @Body Mail mail);

    @POST("mails/{id}")
    Call<Mail> sendMail(@Path("id") String mailId, @Body Mail mail);

    @GET("mails/search")
    Call<List<Mail>> searchMails(@Query("q") String query);

    @GET("mails/{id}")
    Call<Mail> getMailById(@Path("id") String mailId);

    //labels endpoints
    @GET("api/labels")
    Call<List<String>> getLabels();
    @GET("api/labels/{id}")
    Call<Label> getLabelByName(@Path("id") String labelName);
    @POST("api/labels")
    Call<Label> createLabel(@Body Map<String, String> label);
    @PATCH("api/labels/{id}")
    Call<Void> updateLabel(@Path("id") String oldName, @Body Map<String, String> label);
    @DELETE("api/labels/{id}")
    Call<Void> deleteLabel(@Path("id") String labelName);
    @GET("api/labels/mails")
    Call<List<Mail>> getMailsByLabel(@Query("label") String label);
    @POST("api/labels/mails/{mailId}")
    Call<Void> addLabelToMail(@Path("mailId") String mailId, @Body Map<String, String> body);
    @HTTP(method = "DELETE", path = "api/labels/mails/{mailId}", hasBody = true)
    Call<Void> removeLabelFromMail(@Path("mailId") String mailId, @Body Map<String, String> body);
}
