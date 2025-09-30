package com.example.androidproject.data.remote.api;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebServiceAPI {
    /* user endpoints */
    @GET("users/me")
    Call<User> getMe(); // isSignedIn requests

    @FormUrlEncoded
    @POST("tokens")
    Call<Void> login(@Field("username") String username, @Field("password") String password);

    @DELETE("tokens")
    Call<Void> logout();

    @Multipart
    @POST("users")
    Call<User> register(@PartMap Map<String, RequestBody> userData,
                                    @Part MultipartBody.Part picture // can be null
    );

    /*mails endpoints*/
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

    /* labels endpoints */
    @GET("labels")
    Call<List<String>> getLabels();

    @GET("labels/{id}")
    Call<Label> getLabelByName(@Path("id") String labelName);

    @POST("labels")
    Call<Label> createLabel(@Body Map<String, String> labelName);

    @PATCH("labels/{id}")
    Call<Void> updateLabel(@Path("id") String labelName, @Body Map<String, String> newLabelName);

    @DELETE("labels/{id}")
    Call<Void> deleteLabel(@Path("id") String labelName);

    @GET("labels/mails")
    Call<List<Mail>> getMailsByLabel(@Query("label") String label);

    @POST("labels/mails/{mailId}")
    Call<Void> addLabelToMail(@Path("mailId") String mailId, @Body Map<String, String> body);

    @HTTP(method = "DELETE", path = "labels/mails/{mailId}", hasBody = true)
    Call<Void> removeLabelFromMail(@Path("mailId") String mailId, @Body Map<String, String> body);

    /* blacklist endpoints */
    @POST("blacklist")
    Call<Void> addToBlacklist(@Body Map<String, String> body);

    @DELETE("blacklist/{id}")
    Call<Void> removeFromBlacklist(@Path("id") String url);
}
