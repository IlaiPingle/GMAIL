package com.example.androidproject.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "labels")
public class Label {
    @PrimaryKey
    @NonNull
    private String name;
    public Label(@NonNull String name) {
        this.name = name;
    }
    @Ignore
    public Label() {
        name = "";
    }
    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}

