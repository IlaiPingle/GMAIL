package com.example.androidproject.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.annotation.Nullable;
import com.example.androidproject.data.AuthRepository;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.local.dao.UserDao;
import com.example.androidproject.data.models.User;
import com.example.androidproject.data.remote.net.ApiClient;
import com.example.androidproject.model.IsSignedInResponse;
import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;
import com.example.androidproject.util.TokenManager;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Repository that handles user data operations.
 * It interacts with both local database (Room) and remote authentication service.
 */
public class UserRepository {
    private final AuthRepository remote = new AuthRepository();
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Context appContext;

    public UserRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.userDao = AppDB.getInstance(appContext).userDao();
    }

    public LiveData<User> getCurrentUser() { return userDao.getCurrentUser(); }

    public void clear() { executor.submit(userDao::clear); TokenManager.clearData(appContext);
        ApiClient.clearCookies(appContext);
    }

    public void persistFromLogin(LoginResponse body) {
        if (body == null || body.getUser() == null) return;
        String token = body.getToken();
        LoginResponse.User u = body.getUser();
        User entity = new User(nz(u.getUsername()), nz(u.getFirstName()), nz(u.getSurName()), nz(u.getPicture()), token);
        executor.submit(() -> userDao.upsert(entity));
        TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
        if (token != null) TokenManager.saveAuthToken(appContext, token);
    }

    public void persistFromRegister(RegisterResponse body) {
        if (body == null || body.getUser() == null) return;
        String token = body.getToken();
        RegisterResponse.User u = body.getUser();
        User entity = new User(nz(u.getUsername()), nz(u.getFirstName()), nz(u.getSurName()), nz(u.getPicture()), token);
        executor.submit(() -> userDao.upsert(entity));
        TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
        if (token != null) TokenManager.saveAuthToken(appContext, token);
    }
    private String nz(String s) { return s == null ? "" : s; }

    public void login(String username, String password, Callback<LoginResponse> callback) {
        remote.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    remote.isSignedIn().enqueue(new Callback<IsSignedInResponse>() {
                        @Override
                        public void onResponse(Call<IsSignedInResponse> c, Response<IsSignedInResponse> r) {
                            if (r.isSuccessful() && r.body() != null) {
                                IsSignedInResponse user = r.body();
                                User entity = new User(
                                        user.username == null ? "" : user.username,
                                        user.first_name == null ? "" : user.first_name,
                                        user.sur_name == null ? "" : user.sur_name,
                                        user.picture == null ? "" : user.picture,
                                        null
                                );
                                executor.submit(() -> userDao.upsert(entity));
                                TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
                            }
                            if (callback != null) callback.onResponse(call, response);
                        }

                        @Override
                        public void onFailure(Call<IsSignedInResponse> c, Throwable t) {
                            if (callback != null) {
                                callback.onResponse(call, response);
                            }
                        }
                    });
                } else {
                    if (callback != null) callback.onResponse(call, response);
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (callback != null) callback.onFailure(call, t);
            }
        });
    }

    public void register(String first, String last, String user, String pass, @Nullable File imageFile, Callback<RegisterResponse> callback) {
        RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), first);
        RequestBody surNameBody = RequestBody.create(MediaType.parse("text/plain"), last);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), user);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), pass);

        MultipartBody.Part imagePart = null;
        if (imageFile != null && imageFile.exists()) {
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), imageBody);
        }

        remote.register(firstNameBody, surNameBody, usernameBody, passwordBody, imagePart)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            persistFromRegister(res.body());
                        }
                        if (callback != null) callback.onResponse(call, res);
                    }
                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        if (callback != null) callback.onFailure(call, t);
                    }
                });
    }

    public void logout(Callback<Void> callback) {
        remote.logout().enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> call, Response<Void> response) {
                clear();
                if (callback != null) callback.onResponse(call, response);
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                clear();
                if (callback != null) callback.onFailure(call, t);
            }
        });
    }

    public void autoSignIn(Callback<Boolean> cb) {
        remote.isSignedIn().enqueue(new Callback<IsSignedInResponse>() {
            @Override
            public void onResponse(Call<IsSignedInResponse> call, Response<IsSignedInResponse> response) {
                boolean ok = response.isSuccessful() && response.body() != null;
                if (ok) {
                    IsSignedInResponse user = response.body();
                    User entity = new User(
                            user.username == null ? "" : user.username,
                            user.first_name == null ? "" : user.first_name,
                            user.sur_name == null ? "" : user.sur_name,
                            user.picture == null ? "" : user.picture,
                            null
                    );
                    executor.submit(() -> userDao.upsert(entity));
                    TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
                }
                if (cb != null) cb.onResponse(null, Response.success(ok));
            }
            @Override
            public void onFailure(Call<IsSignedInResponse> call, Throwable t) {
                if (cb != null) cb.onFailure(null, t);
            }
        });
    }
}