package com.example.androidproject.api;

import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * ApiService interface defining the endpoints for user authentication and registration.
 * Uses Retrofit annotations to specify HTTP methods and parameters.
 */
public interface ApiService {
    @FormUrlEncoded
    @POST("api/tokens")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @Multipart
    @POST("api/users")
    Call<RegisterResponse> register(
            @Part("first_name") RequestBody firstName,
            @Part("sur_name") RequestBody surName,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part picture
    );
}
