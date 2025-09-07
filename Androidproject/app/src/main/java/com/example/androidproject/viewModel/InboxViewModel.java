package com.example.androidproject.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.androidproject.data.models.User;
import com.example.androidproject.data.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxViewModel extends AndroidViewModel {
    private final UserRepository userRepo;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>(false);

    public InboxViewModel(@NonNull Application app) {
        super(app);
        userRepo = new UserRepository(app);
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }
    public LiveData<User> getCurrentUser() { return userRepo.getCurrentUser(); }

    public void logout() {
        loading.setValue(true);
        userRepo.logout(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                loading.setValue(false);
                loggedOut.setValue(true);
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Network error: " + t.getMessage());
                loggedOut.setValue(true);
            }
        });
    }
}
