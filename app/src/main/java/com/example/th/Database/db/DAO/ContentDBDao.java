package com.example.th.Database.db.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.th.Database.db.Entity.ContentDB;

import java.util.List;

@Dao
public interface ContentDBDao {
    @Insert
    void insert(ContentDB entity);

    @Update
    void update(ContentDB entity);

    @Delete
    void delete(ContentDB publication);

    @Query("SELECT * FROM CONTENTDB")
    List<ContentDB> findAll();

    @Query("SELECT * FROM ContentDB ORDER BY id DESC LIMIT 3")
    List<ContentDB> getLast3Entries();

    @Query("DELETE FROM CONTENTDB WHERE content = :content")
    void deleteall(String content);
}
