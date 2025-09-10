package com.example.androidproject.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.androidproject.data.local.dao.LabelDao;
import com.example.androidproject.data.local.dao.MailDao;
import com.example.androidproject.data.local.dao.UserDao;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.models.User;
import com.example.androidproject.data.models.Label;

/**
 * Room database for the application.
 * It includes entities for Mail and User, and provides DAOs for accessing them.
 * The database is a singleton to prevent multiple instances.
 */
@Database(entities = {Mail.class, User.class, Label.class}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDB extends RoomDatabase {
    private static AppDB instance;

    public abstract MailDao mailDao();
    public abstract LabelDao labelDao();
    public abstract UserDao userDao();

    public static synchronized AppDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDB.class, "gmail_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
