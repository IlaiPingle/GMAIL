package com.example.androidproject.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.androidproject.data.models.User;

/**
 * Data Access Object (DAO) for the User entity.
 * Provides methods to perform database operations related to the User.
 * Includes methods for inserting, retrieving, and clearing user data.
 */
@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(User user);

    @Query("SELECT * FROM users LIMIT 1")
    LiveData<User> getCurrentUser();

    @Query("DELETE FROM users")
    void clear();
}