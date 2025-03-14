package com.example.th.Firebase;

import com.example.th.Database.db.Entity.ContentDB;
import java.util.List;

public class MainData {
    private List<ContentDB> contentList;
    private String name;

    public MainData(List<ContentDB> contentList, String name) {
        this.contentList = contentList;
        this.name = name;
    }

    public List<ContentDB> getContentList() {
        return contentList;
    }

    public void setContentList(List<ContentDB> contentList) {
        this.contentList = contentList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
