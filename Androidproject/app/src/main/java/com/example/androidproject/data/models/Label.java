package com.example.androidproject.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "labels")
public class Label {
    @PrimaryKey
    @NonNull
    private String labelName;
    public Label(@NonNull String labelName) {
        this.labelName = labelName;
    }
    @Ignore
    public Label() {
        labelName = "";
    }
    @NonNull
    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(@NonNull String labelName) {
        this.labelName = this.labelName;
    }
}

