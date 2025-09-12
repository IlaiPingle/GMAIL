package com.example.androidproject.data.local.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters {
    private static final Gson gson = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();
    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null) return Collections.emptyList();
        return gson.fromJson(value, LIST_TYPE);
    }
    @TypeConverter
    public static String fromList(List<String> list) {
        return list == null ? null : gson.toJson(list, LIST_TYPE);
    }
}
