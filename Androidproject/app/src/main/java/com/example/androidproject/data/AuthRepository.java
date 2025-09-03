package com.example.androidproject.data;

import com.example.androidproject.data.remote.net.ApiClient;
import com.example.androidproject.api.ApiService;
import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Repository class for handling authentication-related API calls.
 * This class uses Retrofit to communicate with the backend service.
 * It provides methods for user registration and login.
 * Each method returns a Call object that can be used to execute the request asynchronously.
 */
public class AuthRepository {
    private final ApiService api;

    public AuthRepository() {
        this.api = ApiClient.getClient().create(ApiService.class);
    }

    public Call<RegisterResponse> register(RequestBody firstName,
                                           RequestBody surName,
                                           RequestBody username,
                                           RequestBody password,
                                           MultipartBody.Part picture) {
        return api.register(firstName, surName, username, password, picture);
    }

    public Call<LoginResponse> login(String username, String password) {
        return api.login(username, password);
    }
}