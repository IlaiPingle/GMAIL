package com.example.androidproject.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;
import com.example.androidproject.data.repository.UserRepository;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for handling user registration and auto-login.
 * It interacts with the AuthRepository to perform network operations.
 * The ViewModel exposes LiveData for loading state, error messages,
 * registration results, and login results to the UI.
 */
public class RegisterViewModel extends AndroidViewModel {
    private final UserRepository userRepo;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepo = new UserRepository(application);
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<RegisterResponse> getRegisterResult() { return registerResult; }
    public LiveData<LoginResponse> getLoginResult() { return loginResult; }

    public void register(String first, String last, String user, String pass, File imageFile) {
        loading.setValue(true);

        RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), first);
        RequestBody surNameBody = RequestBody.create(MediaType.parse("text/plain"), last);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), user);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), pass);

        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), imageBody);
        }

        userRepo.remote().register(firstNameBody, surNameBody, usernameBody, passwordBody, imagePart)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> res) {
                        loading.setValue(false);
                        if (res.isSuccessful() && res.body() != null) {
                            RegisterResponse body = res.body();
                            userRepo.persistFromRegister(body);
                            registerResult.setValue(body);
                            autoLogin(user, pass);
                        } else {
                            errorMessage.setValue("Registration failed");
                        }
                    }
                    @Override public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        loading.setValue(false);
                        errorMessage.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    private void autoLogin(String username, String password) {
        userRepo.remote().login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    userRepo.persistFromLogin(res.body());
                    loginResult.setValue(res.body());
                } else {
                    errorMessage.setValue("Auto-login failed");
                }
            }
            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
                errorMessage.setValue("Auto-login network error: " + t.getMessage());
            }
        });
    }
}