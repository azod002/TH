package com.example.th.Database.db.DAO;
// ContentDao.java
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.example.th.Database.db.Entity.ContentDB;

import java.util.List;

@Dao
public interface ContentDao {
    @Query("SELECT * FROM contentdb WHERE id = :id LIMIT 1")
    ContentDB findById(int id);

    @Query("SELECT * FROM contentdb")
    List<ContentDB> findAll();

    @Delete
    void delete(ContentDB contentDB);

    // Другие методы...
}

