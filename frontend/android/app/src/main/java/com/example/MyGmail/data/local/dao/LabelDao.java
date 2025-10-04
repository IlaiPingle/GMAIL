package com.example.MyGmail.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.MyGmail.data.models.Label;

import java.util.List;

@Dao
public interface LabelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Label label);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Label> labels);

    @Delete
    void delete(Label label);

    @Query("DELETE FROM labels WHERE name = :labelName")
    void deleteByName(String labelName);

    @Query("SELECT * FROM labels")
    LiveData<List<Label>> getAllLabels();

    @Query("SELECT * FROM labels WHERE name = :labelName LIMIT 1")
    LiveData<Label> getLabelByName(String labelName);

    @Query("DELETE FROM labels")
    void clear();
}
