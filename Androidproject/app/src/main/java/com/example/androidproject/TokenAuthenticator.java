package com.example.androidproject;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import java.io.IOException;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Authenticator;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context;
    }
    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException{
        if (response.code() == 401) {
            // Token might be expired, clear it and prompt for re-login
            TokenManager.clearData(context);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        return null; // If not a 401, do not attempt to authenticate
    }
}
