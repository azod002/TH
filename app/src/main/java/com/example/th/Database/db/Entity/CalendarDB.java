package com.example.th.Database.db.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CalendarDB")
public class CalendarDB {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "plans")
    private String plans;

    @ColumnInfo(name = "status")
    private String status;

    public CalendarDB(String date, String plans, String status) {
        this.date = date;
        this.plans = plans;
        this.status = status;
    }

    public CalendarDB(String date, String plans) {
        this(date, plans, "Не начато");
    }

    public CalendarDB() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlans() {
        return plans;
    }

    public void setPlans(String plans) {
        this.plans = plans;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
