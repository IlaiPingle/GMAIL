package com.example.androidproject.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.androidproject.data.AuthRepository;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.local.dao.UserDao;
import com.example.androidproject.data.models.User;
import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;
import com.example.androidproject.util.TokenManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository that handles user data operations.
 * It interacts with both local database (Room) and remote authentication service.
 */
public class UserRepository {
    private final AuthRepository remote = new AuthRepository();
    private final UserDao userDao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Context appContext;

    public UserRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.userDao = AppDB.getInstance(appContext).userDao();
    }

    public AuthRepository remote() { return remote; }

    public LiveData<User> getCurrentUser() { return userDao.getCurrentUser(); }

    public void clear() { io.submit(userDao::clear); TokenManager.clearData(appContext); }

    public void persistFromLogin(LoginResponse body) {
        if (body == null || body.getUser() == null) return;
        String token = body.getToken();
        LoginResponse.User u = body.getUser();
        User entity = new User(nz(u.getUsername()), nz(u.getFirstName()), nz(u.getSurName()), nz(u.getPicture()), token);
        io.submit(() -> userDao.upsert(entity));
        TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
        if (token != null) TokenManager.saveAuthToken(appContext, token);
    }

    public void persistFromRegister(RegisterResponse body) {
        if (body == null || body.getUser() == null) return;
        String token = body.getToken();
        RegisterResponse.User u = body.getUser();
        User entity = new User(nz(u.getUsername()), nz(u.getFirstName()), nz(u.getSurName()), nz(u.getPicture()), token);
        io.submit(() -> userDao.upsert(entity));
        TokenManager.saveUserInfo(appContext, entity.username, entity.firstName, entity.surName, entity.picture);
        if (token != null) TokenManager.saveAuthToken(appContext, token);
    }
    private String nz(String s) { return s == null ? "" : s; }
}