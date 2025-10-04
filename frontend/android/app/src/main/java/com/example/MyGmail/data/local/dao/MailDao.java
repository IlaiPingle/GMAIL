package com.example.MyGmail.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.example.MyGmail.data.models.Mail;

import java.util.List;

@Dao
public interface MailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Mail... mails);

    @Query("SELECT * FROM mails")
    LiveData<List<Mail>> getMails();

    @Delete
    void delete(Mail... mails);

    @Update
    void update(Mail... mails);

    @Query("DELETE FROM mails")
    void clear();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Mail> mails);

    @Query("SELECT * FROM mails WHERE id = :id")
    LiveData<Mail> getMail(String id);

    @Query("SELECT * FROM mails WHERE id = :id")
    Mail getMailNow(String id);
}

