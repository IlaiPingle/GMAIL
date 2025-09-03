package com.example.androidproject.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a user in the application.
 * This entity is stored in the "users" table in the database.
// * Includes fields for username, first name, surname, picture URL, and an optional token.
 */
@Entity(tableName = "users")
public class User {
    @PrimaryKey @NonNull
    public String username;
    public String firstName;
    public String surName;
    public String picture; // may be empty for optional photo
    public String token;   // optional

    public User(@NonNull String username, String firstName, String surName, String picture, String token) {
        this.username = username;
        this.firstName = firstName;
        this.surName = surName;
        this.picture = picture;
        this.token = token;
    }
}
