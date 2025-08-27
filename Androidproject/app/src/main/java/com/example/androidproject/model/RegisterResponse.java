package com.example.androidproject.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for the registration response from the server.
 */
public class RegisterResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

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

    /**
     * Inner class representing the user details.
     */
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