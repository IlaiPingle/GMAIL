package com.example.androidproject.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.remote.net.SessionManager;
import com.example.androidproject.ui.auth.LoginActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class BaseActivity extends AppCompatActivity {
    private final Executor io = Executors.newSingleThreadExecutor();
    private volatile boolean isHandlingLogout = false;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SessionManager.getInstance(getApplicationContext())
                .observeLogout()
                .observe(this, shouldLogout -> {
                    if (Boolean.TRUE.equals(shouldLogout)) {
                        handleGlobalLogout();
                    }
                });
    }

    protected final void handleGlobalLogout() {
        if (isHandlingLogout) return;
        isHandlingLogout = true;

        onLogoutWillStart();

        io.execute(() -> {
            try {
                AppDB.getInstance(getApplicationContext()).clearAllTables();
            } catch (Throwable ignored) {
            }
            runOnUiThread(() -> {
                onLogoutDidCleanup();
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });
        });
    }

    protected void onLogoutWillStart() { /* no-op by default */ }

    protected void onLogoutDidCleanup() { /* no-op by default */ }
}
