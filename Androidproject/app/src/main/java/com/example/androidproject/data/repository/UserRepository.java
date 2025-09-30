package com.example.androidproject.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.androidproject.data.local.dao.UserDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.models.RegisterRequest;
import com.example.androidproject.data.models.User;
import com.example.androidproject.data.remote.api.WebServiceAPI;
import com.example.androidproject.data.remote.net.ApiClient;
import com.example.androidproject.util.ApiErrorParser;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    public interface ErrorSink {
        void onError(String message);
    }

    private final Context appCtx;
    private final UserDao userDao;
    private final WebServiceAPI api;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Context ctx) {
        appCtx = ctx.getApplicationContext();
        userDao = AppDB.getInstance(appCtx).userDao();
        api = ApiClient.getClient(appCtx).create(WebServiceAPI.class);
    }

    public LiveData<User> getCurrentUser() {
        return userDao.getCurrentUser();
    }

    // LOGIN
    public void login(String username, String password, @Nullable ErrorSink onError) {
        api.login(username, password).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> loginCall, @NonNull Response<Void> loginResp) {
                if (!loginResp.isSuccessful()) {
                    String r = ApiErrorParser.parseMessage(loginResp);
                    if (onError != null)
                        onError.onError("Login failed: (" + loginResp.code() + ") " + r);
                    return;
                }
                refreshMe(onError);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (onError != null) onError.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void register(RegisterRequest request, @Nullable File imageFile, @Nullable ErrorSink onError) {
        Map<String, RequestBody> parts = toPartMap(request);
        MultipartBody.Part imagePart = toImagePart(imageFile);
        api.register(parts, imagePart)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            executor.submit(() -> userDao.upsert(response.body()));
                        } else {
                            String message = ApiErrorParser.parseMessage(response);
                            if (onError != null)
                                onError.onError("Registration failed: (" + response.code() + ") " + message);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        if (onError != null) onError.onError("Network error: " + t.getMessage());
                    }
                });
    }


    // LOGOUT
    public void logout(@Nullable ErrorSink onError) {
        api.logout().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> resp) {
                clearLocal();
                if (!resp.isSuccessful() && onError != null) {
                    String r = ApiErrorParser.parseMessage(resp);
                    onError.onError("Logout failed: (" + resp.code() + ") " + r);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                clearLocal();
                if (onError != null) onError.onError("Network error: " + t.getMessage());
            }
        });
    }

    // AUTO SIGN-IN / CHECK
    public void refreshMe(@Nullable ErrorSink onError) {
        api.getMe().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    upsertUser(response.body());
                } else if (response.code() == 401) {
                    clearLocal(); // unauthorized, clear local user
                } else if (onError != null) {
                    String message = ApiErrorParser.parseMessage(response);
                    onError.onError("Failed to refresh user: (" + response.code() + ") " + message);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                if (onError != null) onError.onError("Network error: " + t.getMessage());
            }
        });
    }

    private void upsertUser(User user) {
        executor.submit(() -> userDao.upsert(user));
    }

    private void clearLocal() {
        executor.submit(userDao::clear);
        ApiClient.clearCookies(appCtx);
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private static Map<String, RequestBody> toPartMap(RegisterRequest r) {
        Map<String, RequestBody> map = new java.util.HashMap<>();
        map.put("first_name", textPart(r.getFirst_name()));
        map.put("sur_name", textPart(r.getSur_name()));
        map.put("username", textPart(r.getUsername()));
        map.put("password", textPart(r.getPassword()));
        return map;
    }

    private static RequestBody textPart(String value) {
        return RequestBody.create(value == null ? "" : value, MediaType.parse("text/plain"));
    }

    @Nullable
    private static MultipartBody.Part toImagePart(@Nullable File imageFile) {
        if (imageFile == null || !imageFile.exists()) return null;
        RequestBody imageBody = RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile);
        //  upload.single('picture')
        return MultipartBody.Part.createFormData("picture", imageFile.getName(), imageBody);
    }
}

