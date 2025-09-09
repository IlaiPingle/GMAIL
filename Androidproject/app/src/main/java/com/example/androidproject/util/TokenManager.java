package com.example.androidproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages secure storage and retrieval of authentication tokens and user information
 * using EncryptedSharedPreferences.
 */
public class TokenManager {
    private static final String PREF_NAME = "SecureAuthPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_SUR_NAME = "sur_name";
    private static final String KEY_PICTURE = "picture";

    /**
     * Initializes and returns an instance of EncryptedSharedPreferences.
     * @param context The application context.
     * @return An instance of SharedPreferences that is encrypted.
     */
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

    /**
     * Saves the authentication token securely.
     * @param context The application context.
     * @param token The authentication token to be saved.
     */
    public static void saveAuthToken(Context context, String token) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    /**
     * Retrieves the stored authentication token.
     * @param context The application context.
     * @return The stored authentication token, or null if not found.
     */
    public static String getToken(Context context) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Saves user information securely.
     * @param context The application context.
     * @param username The username to be saved.
     * @param firstName The first name to be saved.
     * @param surName The surname to be saved.
     * @param picture The picture URL or path to be saved.
     */
    public static void saveUserInfo(Context context, String username, String firstName,
                                    String surName, String picture) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_FIRST_NAME, firstName)
                .putString(KEY_SUR_NAME, surName)
                .putString(KEY_PICTURE, picture == null ? "" : picture)
                .apply();
    }
    // getters for user info
    public static String getUsername(Context context) {
        return getEncryptedSharedPreferences(context).getString(KEY_USERNAME, null);
    }

    public static String getFirstName(Context context) {
        return getEncryptedSharedPreferences(context).getString(KEY_FIRST_NAME, null);
    }

    public static String getSurName(Context context) {
        return getEncryptedSharedPreferences(context).getString(KEY_SUR_NAME, null);
    }

    public static String getPicture(Context context) {
        // Return empty if not set to simplify UI checks
        String v = getEncryptedSharedPreferences(context).getString(KEY_PICTURE, "");
        return v == null ? "" : v;
    }

    /**
     * Retrieves the stored username.
     * @param context The application context.
     */
    public static void clearData(Context context) {
        SharedPreferences prefs = getEncryptedSharedPreferences(context);
        prefs.edit().clear().apply();
    }
}