package com.example.androidproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {
    private static final String PREF_NAME = "SecureAuthPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_SUR_NAME = "sur_name";
    private static final String KEY_PICTURE = "picture";

    private static SharedPreferences getEncryptedSharedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("TokenManager", "Error creating encrypted SharedPreferences", e);
            throw new RuntimeException("Failed to create encrypted SharedPreferences", e);
        }
    }
    public static void saveAuthToken(Context context, String token) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        return prefs.getString(KEY_TOKEN, null);
    }

    public static void saveUserInfo(Context context, String username, String firstName,
                                    String surName, String picture) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_SUR_NAME, surName)
                .putString(KEY_PICTURE, picture)
                .apply();
    }

    public static void clearData(Context context) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit().clear().apply();
    }
}