package com.khush.chatgpt3;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyEntityDao {
    @Insert
    void insert(MyEntity myEntity);

    @Query("SELECT * FROM my_table")
    LiveData<List<MyEntity>> getAllEntities();

    // Define other methods for update, delete, etc.
}
