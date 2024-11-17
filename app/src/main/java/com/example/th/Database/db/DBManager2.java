package com.example.th.Database.db;

import android.content.Context;

import androidx.room.RoomDatabase;

public class DBManager2 {
    private static DBManager2 manager;
    private AppCalDB database;

    public DBManager2(Context context) {
        database = new RoomDatabase
                .Builder<AppCalDB>(context, AppCalDB.class, "ContentAppDB2")
                //.addMigrations(Migrations.MIGRATION_1_2)
                .allowMainThreadQueries()
                .build();
    }

    public AppCalDB getDatabase() {
        return database;
    }

    public static DBManager2 getInstance(Context context) {
        if (manager == null) manager = new DBManager2(context);
        return manager;
    }
}

