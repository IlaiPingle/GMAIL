package com.example.androidproject.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.androidproject.data.models.User;
import com.example.androidproject.api.EmailApiService;
import com.example.androidproject.data.remote.net.ApiClient;
import com.example.androidproject.data.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final UserRepository userRepo;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application app) {
        super(app);
        userRepo = new UserRepository(app);
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }
    public LiveData<User> getCurrentUser() { return userRepo.getCurrentUser(); }

    public void logout() {
        loading.setValue(true);
        EmailApiService api = ApiClient.getClient().create(EmailApiService.class);
        api.logout().enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                userRepo.clear(); // DAO + session
                loading.setValue(false);
                loggedOut.setValue(true);
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                userRepo.clear(); // still clear locally
                loading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                loggedOut.setValue(true);
            }
        });
    }
}
