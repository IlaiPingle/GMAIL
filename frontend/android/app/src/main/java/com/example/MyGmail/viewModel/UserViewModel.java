package com.example.MyGmail.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.MyGmail.data.models.RegisterRequest;
import com.example.MyGmail.data.models.User;
import com.example.MyGmail.data.remote.net.ApiClient;
import com.example.MyGmail.data.repository.UserRepository;

import java.io.File;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    LiveData<User> user;

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserViewModel(@NonNull Application app) {
        super(app);
        userRepository = new UserRepository(app);
        user = userRepository.getCurrentUser();
    }
     public LiveData<User> getUser() {
        return user;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public void login(String username, String password) {
        userRepository.login(username, password, errorMessage::postValue);
    }
    public void register(String firstname, String lastname, String username, String password, File imageFile) {
        userRepository.register(new RegisterRequest(username, firstname, lastname, password), imageFile,errorMessage::postValue);
    }



    public void logout() {
        userRepository.logout(errorMessage::postValue);
    }

    public void refreshMe() {
        userRepository.refreshMe(errorMessage::postValue);
    }
    public boolean hasSession() {
        return ApiClient.hasSession(getApplication());
    }
}
