package com.khush.chatgpt3;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {MyEntity.class}, version = 1, exportSchema = false)
public abstract class MyRoomDatabase extends RoomDatabase {
    // Define abstract methods to get DAOs for interacting with the database
    public abstract MyEntityDao myEntityDao();
    private static volatile MyRoomDatabase appDatabase;

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}