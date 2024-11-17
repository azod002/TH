package com.example.th.Database.db.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.th.Database.db.Entity.CalendarDB;
import com.example.th.Database.db.Entity.ContentDB;

import java.util.List;

@Dao
public interface CalendarDBdao {
    @Query("SELECT * FROM calendardb WHERE id = :id LIMIT 1")
    CalendarDB findById(int id);

    @Query("SELECT * FROM calendardb WHERE date = :date")
    List<CalendarDB> findByDate(String date);

    @Query("DELETE FROM calendardb WHERE date = :date AND plans = :plans")
    int DelByDatePlans(String date, String plans);

    @Insert
    void insert(CalendarDB entity);

    @Update
    void update(CalendarDB entity);

    @Delete
    void delete(CalendarDB publication);


}
