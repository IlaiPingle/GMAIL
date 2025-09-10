package com.example.androidproject.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;
import com.example.androidproject.data.repository.UserRepository;
import com.example.androidproject.data.models.User;
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
public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepo;
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loginSucceeded = new MutableLiveData<>(false);
    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepo = new UserRepository(application);
    }

    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<User> getCurrentUser() { return userRepo.getCurrentUser(); }
    public LiveData<Boolean> getLoginSucceeded() { return loginSucceeded; }

    public void login(String username, String password) {
        loading.setValue(true);
        userRepo.login(username, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    loginSucceeded.setValue(true);
                    userRepo.autoSignIn(null);
                } else {
                    errorMessage.setValue("Login failed");
                    loginSucceeded.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                loginSucceeded.setValue(false);
            }
        });
    }
}