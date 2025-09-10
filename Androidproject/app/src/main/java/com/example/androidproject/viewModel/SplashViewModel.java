package com.example.androidproject.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.androidproject.data.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashViewModel extends AndroidViewModel {
    private final UserRepository userRepo;
    public final MutableLiveData<Boolean> isSignedIn = new MutableLiveData<>();

    public SplashViewModel(@NonNull Application app) {
        super(app);
        userRepo = new UserRepository(app);
    }

    public void checkSession() {
        userRepo.autoSignIn(new Callback<Boolean>() {
            @Override public void onResponse(Call<Boolean> call, Response<Boolean> res) {
                isSignedIn.postValue(Boolean.TRUE.equals(res.body()));
            }
            @Override public void onFailure(Call<Boolean> call, Throwable t) {
                isSignedIn.postValue(false);
            }
        });
    }
}