//package com.example.androidproject.viewModel;
//
//import android.app.Application;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.androidproject.data.repository.UserRepository;
//import java.io.File;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * ViewModel for handling user registration and auto-login.
// * It interacts with the AuthRepository to perform network operations.
// * The ViewModel exposes LiveData for loading state, error messages,
// * registration results, and login results to the UI.
// */
//public class RegisterViewModel extends AndroidViewModel {
//    private final UserRepository userRepo;
//    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
//    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
//    private final MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
//    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
//    private final MutableLiveData<Boolean> authSucceeded = new MutableLiveData<>(false);
//
//    public RegisterViewModel(@NonNull Application application) {
//        super(application);
//        userRepo = new UserRepository(application);
//    }
//
//    public LiveData<Boolean> getLoading() { return loading; }
//    public LiveData<String> getErrorMessage() { return errorMessage; }
//    public LiveData<RegisterResponse> getRegisterResult() { return registerResult; }
//    public LiveData<LoginResponse> getLoginResult() { return loginResult; }
//    public LiveData<Boolean> getAuthSucceeded() { return authSucceeded; }
//
//    public void register(String first, String last, String user, String pass, @Nullable File imageFile) {
//        loading.setValue(true);
//        userRepo.register(first, last, user, pass, imageFile, new Callback<RegisterResponse>() {
//            @Override public void onResponse(Call<User> call, Response<USer> res) {
//                if (res.isSuccessful() && res.body() != null) {
//                    registerResult.setValue(res.body());
//                    autoLogin(user, pass);
//                } else {
//                    loading.setValue(false);
//                    errorMessage.setValue("Registration failed");
//                }
//            }
//            @Override public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                loading.setValue(false);
//                errorMessage.setValue("Network error: " + t.getMessage());
//            }
//        });
//    }
//
//    private void autoLogin(String username, String password) {
//        userRepo.login(username, password, new Callback<LoginResponse>() {
//            @Override public void onResponse(Call<LoginResponse> call, Response<LoginResponse> res) {
//                loading.setValue(false);
//                if (res.isSuccessful()) {
//                    authSucceeded.setValue(true);
//                } else {
//                    errorMessage.setValue("Auto-login failed");
//                }
//            }
//            @Override public void onFailure(Call<LoginResponse> call, Throwable t) {
//                loading.setValue(false);
//                errorMessage.setValue("Auto-login network error: " + t.getMessage());
//            }
//        });
//    }
//}