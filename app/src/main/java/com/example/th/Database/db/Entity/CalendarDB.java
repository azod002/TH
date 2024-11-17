package com.example.th.Database.db.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "CalendarDB")
public class CalendarDB {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "plans")
    private String plans;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlans() {
        return plans;
    }

    public CalendarDB(String date, String plans) {
        this.date = date;
        this.plans = plans;
    }

    public CalendarDB() {}

    public void setPlans(String plans) {
        this.plans = plans;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
