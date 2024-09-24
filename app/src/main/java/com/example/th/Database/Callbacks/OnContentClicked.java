package com.example.th.Database.Callbacks;

import com.example.th.Database.db.Entity.ContentDB;

public interface OnContentClicked {
    public void onRemoveClicked(ContentDB contentDB);

    public void onJustClicked(ContentDB contentDB);
}
