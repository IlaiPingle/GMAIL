package com.example.androidproject;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    // Required for token-based auth if your backend returns a token
    @SerializedName("token")
    private String token;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public static class User {
        @SerializedName("username")
        private String username;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("sur_name")
        private String surName;

        @SerializedName("picture")
        private String picture;

        // Getters
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getSurName() { return surName; }
        public String getPicture() { return picture; }
    }
}