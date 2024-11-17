package com.example.th.Database.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.th.Database.Converter;
import com.example.th.Database.db.DAO.CalendarDBdao;
import com.example.th.Database.db.Entity.CalendarDB;

@Database(entities = { CalendarDB.class}, version = 2)
public abstract class AppCalDB extends RoomDatabase {
    public abstract CalendarDBdao getContent1Dao();
}
