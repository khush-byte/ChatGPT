package com.khush.chatgpt3;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_table")
public class MyEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @ColumnInfo(name = "type")
    public Integer type;

    @ColumnInfo(name = "message")
    public String message;

    // Define constructors, getters, and setters as needed
}
