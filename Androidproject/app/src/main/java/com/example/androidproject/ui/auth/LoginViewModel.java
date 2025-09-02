package com.example.androidproject.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidproject.data.AuthRepository;
import com.example.androidproject.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for handling user login.
 * Manages login state and communicates with the API.
 * Exposes LiveData for login results and error messages.
 * Uses Retrofit for network requests.
 */
public class LoginViewModel extends ViewModel {
    private final AuthRepository authRepository = new AuthRepository();
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void login(String username, String password) {
        loading.setValue(true);
        authRepository.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Login failed");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}