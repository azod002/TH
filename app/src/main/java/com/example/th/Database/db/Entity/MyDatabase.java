package com.example.th.Database.db.Entity;
// MyDatabase.java
import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.th.Database.db.DAO.ContentDao;

@Database(entities = {ContentDB.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract ContentDao getContentDao();
}

