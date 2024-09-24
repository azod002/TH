package com.example.th.Database.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.th.Database.db.DAO.ContentDBDao;
import com.example.th.Database.db.Entity.ContentDB;

@Database(entities = { ContentDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContentDBDao getContentDao();
}
