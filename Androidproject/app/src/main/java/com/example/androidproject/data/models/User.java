package com.example.androidproject.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a user in the application.
 * This entity is stored in the "users" table in the database.
 * // * Includes fields for username, first name, surname, picture URL, and an optional token.
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String username;
    @SerializedName("first_name")
    public String first_name;
    @SerializedName("sur_name")
    public String sur_name;

    public String picture; // may be empty for optional photo

    public User(@NonNull String username, String firstName, String surName, String picture) {
        this.username = username;
        this.first_name = firstName;
        this.sur_name = surName;
        this.picture = picture;
    }
    public User () {
    }

    public String getSur_name() {
        return sur_name;
    }

    public void setSur_name(String surName) {
        this.sur_name = surName;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String firstName) {
        this.first_name = firstName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}